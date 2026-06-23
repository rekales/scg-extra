package net.zincstudios.scgextra.entity.cog.centipede;

import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.zincstudios.scgextra.entity.ModBrainMemories;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.entity.common.gun.CustomGunHolder;
import net.zincstudios.scgextra.entity.common.gun.CustomSimulatedGun;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import net.zincstudios.scgextra.sounds.CogSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogCentipedeEntity extends Monster implements GeoEntity, CustomGunHolder, Gunner {

    static final int STUN_RECOVERY_TICKS = 12;
    static final int STUN_DURATION = 80;
    static final int SLAM_DAMAGE_DELAY = 18;
    static final int SLAM_DURATION = 25;

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);
    private final SimulatedGun customGun;
    private final CogCentipedeSegmentPartEntity headPart;
    private final CogCentipedeSegmentPartEntity midPart;
    private final CogCentipedeSegmentPartEntity tailPart;
    private final CogCentipedeWeakpointPartEntity eyePart;
    private final CogCentipedeSegmentPartEntity[] subEntities;

    public CogCentipedeEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.headPart = new CogCentipedeSegmentPartEntity(this, 24/16f, 24/16f);
        this.midPart = new CogCentipedeSegmentPartEntity(this, 28/16f, 30/16f);
        this.tailPart = new CogCentipedeSegmentPartEntity(this, 22/16f, 24/16f);
        this.eyePart = new CogCentipedeWeakpointPartEntity(this, 9/16f, 9/16f);
        this.subEntities = new CogCentipedeSegmentPartEntity[] {
                this.headPart,
                this.midPart,
                this.tailPart,
                this.eyePart
        };
        this.customGun = new CustomSimulatedGun.Builder(ModItems.LIBERTAS.get().getGun())
                .projectileDamage(15)
                .fireRate(80)
                .maxRange(10)
                .idealRange(8)
                .velocityModifier(vec -> vec.scale(1/3f))
                .projectileFactory(PlasmaCannonProjectileEntity::new)
                .build();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 400.0D);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CogCentipedeAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<CogCentipedeEntity> getBrain() {
        return (Brain<CogCentipedeEntity>) super.getBrain();
    }

    protected Brain.Provider<CogCentipedeEntity> brainProvider() {
        return Brain.provider(CogCentipedeAi.MEMORY_TYPES, CogCentipedeAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cogCentipedeBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        CogCentipedeAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public @Nullable PartEntity<?>[] getParts() {
        return this.subEntities;
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        updateSubEntities();

        if (brain.getTimeUntilExpiry(ModBrainMemories.DELAYED_MELEE.get()) == SLAM_DURATION) {
            this.triggerAnim("behaviour", "slam");
        }
        if (brain.getTimeUntilExpiry(ModBrainMemories.STUNNED.get()) == STUN_DURATION) {
            this.triggerAnim("behaviour", "stun");
        }
        if (brain.getTimeUntilExpiry(ModBrainMemories.STUNNED.get()) == STUN_RECOVERY_TICKS) {
            this.triggerAnim("behaviour", "end_stun");
        }
    }

    protected void updateSubEntities() {
        this.headPart.setOldPosAndRot();
        this.midPart.setOldPosAndRot();
        this.tailPart.setOldPosAndRot();
        this.eyePart.setOldPosAndRot();

        Vec3 headOffset = new Vec3(0, 0, 1.5);
        this.headPart.setPos(this.position().add(headOffset.yRot(-this.yBodyRot * Mth.DEG_TO_RAD)));
        Vec3 midOffset = new Vec3(0, 0, -28/16f);
        this.midPart.setPos(this.position().add(midOffset.yRot(-this.yBodyRot * Mth.DEG_TO_RAD)));
        Vec3 tailOffset = new Vec3(0, 0, -28/16f - 24/16f);
        this.tailPart.setPos(this.position().add(tailOffset.yRot(-this.yBodyRot * Mth.DEG_TO_RAD)));
        Vec3 eyeOffset = new Vec3(0, 11/16f, 2.2);
        this.eyePart.setPos(this.position().add(eyeOffset.yRot(-this.yBodyRot * Mth.DEG_TO_RAD)));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (state.isMoving()) {
                state.setAnimation(RawAnimation.begin().thenLoop("walk"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "gun", 2, state -> PlayState.STOP)
                .triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))
        );

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("stun", RawAnimation.begin().thenPlayAndHold("stun_start"))
                .triggerableAnim("end_stun", RawAnimation.begin().thenPlay("stun_end"))
                .triggerableAnim("slam", RawAnimation.begin().thenPlay("slam"))
        );

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    public SimulatedGun getCustomGun() {
        return this.customGun;
    }

    @Override
    protected void tickDeath() {
        // Override to only extend death time
        ++this.deathTime;
        if (this.deathTime >= 42 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return CogSounds.COG_CENTIPEDE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CogSounds.GENERAL_HEAVY_HURT.get();
    }
}
