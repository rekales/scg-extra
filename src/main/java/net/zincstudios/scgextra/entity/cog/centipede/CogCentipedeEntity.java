package net.zincstudios.scgextra.entity.cog.centipede;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.goal.HurtByNonFactionGoal;
import net.zincstudios.scgextra.entity.common.goal.StunnedWithVisualGoal;
import net.zincstudios.scgextra.sounds.CogSounds;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogCentipedeEntity extends GunnerEntity implements GeoEntity, Stunnable, HeadShotHandler {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);
    private final CogCentipedeSegmentPartEntity headPart;
    private final CogCentipedeSegmentPartEntity midPart;
    private final CogCentipedeSegmentPartEntity tailPart;
    private final CogCentipedeWeakpointPartEntity eyePart;
    private final CogCentipedeSegmentPartEntity[] subEntities;

    private static final int STUN_DURATION = 80;

    // Server-side only for stunnable handling
    private int headshotCounter = 0;
    private boolean stunCooldown = false;
    private boolean stunned = false;

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
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new StunnedWithVisualGoal<>(this));
        this.goalSelector.addGoal(3, new CogCentipedeAttackGoal(this, 120)
                .maxRange(10)
                .approachDist(4)
                .attackInterval(80)
        );
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new HurtByNonFactionGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23F)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 400.0D);
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
    protected void tickDeath() {
        // Override to only extend death time
        ++this.deathTime;
        if (this.deathTime >= 42 && !this.level().isClientSide() && !this.isRemoved()) {
            this.level().broadcastEntityEvent(this, (byte)60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public boolean headshot(DamageSource source, float amount) {
        if (this.headshotCounter < CommonConfig.abilityWeaknessHeadshots-1 || !this.stunCooldown) {
            this.headshotCounter++;
        }
        return false;
    }

    @Override
    public int shouldStun() {
        if (!CommonConfig.enableAbilityWeakness) return 0;

        if (this.headshotCounter >= CommonConfig.abilityWeaknessHeadshots) {
            return STUN_DURATION;
        }

        return 0;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
        if (stunned) {
            this.triggerAnim("behaviour", "stun");
        } else {
            this.headshotCounter = 0;
        }
    }

    @Override
    public void setStunCooldown(boolean stunCooldown) {
        this.stunCooldown = stunCooldown;
    }

    @Override
    public boolean isStunned() {
        return this.stunned;
    }

    @Override
    public boolean tickStunned(int ticksLeft) {
        if (ticksLeft == 12) {
            this.triggerAnim("behaviour", "end_stun");
        }
        return false;
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
