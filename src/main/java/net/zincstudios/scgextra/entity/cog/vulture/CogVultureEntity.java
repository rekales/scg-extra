package net.zincstudios.scgextra.entity.cog.vulture;


import com.mojang.serialization.Dynamic;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.entity.common.brain.BrainUtils;
import net.zincstudios.scgextra.entity.common.gun.CustomGunHolder;
import net.zincstudios.scgextra.entity.common.gun.CustomSimulatedGun;
import net.zincstudios.scgextra.entity.common.gun.SimulatedGun;
import net.zincstudios.scgextra.sounds.CogSounds;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.init.ModItems;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogVultureEntity extends Monster implements GeoEntity, Gunner, BulletSpawnOffset, CustomGunHolder {

    public static final Vec3 LEFT_GUN_OFFSET = new Vec3(0.45,1.3,-0.4);
    public static final Vec3 RIGHT_GUN_OFFSET = new Vec3(-0.45,1.3,-0.4);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);
    private final SimulatedGun customGun;

    private boolean bulletSpawnLeft = false;

    public CogVultureEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.customGun = new CustomSimulatedGun.Builder(ModItems.PRUSH_GUN.get().getGun())
                .projectileDamage(3)
                .fireRate(2)
                .maxRange(10)
                .idealRange(8)
                .build();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4F)
                .add(Attributes.MAX_HEALTH, 15.0D);
    }

    protected Brain<?> makeBrain(Dynamic<?> dynamic) {
        return CogVultureAi.makeBrain(this, this.brainProvider().makeBrain(dynamic));
    }

    @SuppressWarnings("unchecked")
    public Brain<CogVultureEntity> getBrain() {
        return (Brain<CogVultureEntity>) super.getBrain();
    }

    protected Brain.Provider<CogVultureEntity> brainProvider() {
        return Brain.provider(CogVultureAi.MEMORY_TYPES, CogVultureAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("cogVultureBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        BrainUtils.Standard.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 2, state ->
                state.setAndContinue(RawAnimation.begin().thenLoop("idle"))));

        controllers.add(new AnimationController<>(this, "death", 2, state -> {
            if (state.getAnimatable().isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlayAndHold("death"));
            }
            return PlayState.STOP;
        }).setAnimationSpeed(1.3f));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return CogSounds.GENERAL_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CogSounds.GENERAL_LIGHT_HURT.get();
    }

    @Override
    public void onGunFire(LivingEntity target) {
        this.bulletSpawnLeft = !this.bulletSpawnLeft;
    }

    @Override
    public Vec3 getBulletSpawnOffset() {
        if (this.bulletSpawnLeft) {
            return CogVultureEntity.LEFT_GUN_OFFSET.yRot(-this.yBodyRot * Mth.DEG_TO_RAD);
        } else {
            return CogVultureEntity.RIGHT_GUN_OFFSET.yRot(-this.yBodyRot * Mth.DEG_TO_RAD);
        }
    }

    @Override
    public SimulatedGun getCustomGun() {
        return this.customGun;
    }
}
