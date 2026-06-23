package net.zincstudios.scgextra.entity.cog.devastator;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.Gunner;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import net.zincstudios.scgextra.entity.common.brain.BrainUtils;
import net.zincstudios.scgextra.entity.common.gun.CustomSimulatedGun;
import net.zincstudios.scgextra.entity.common.gun.HeadAttachedMountedGun;
import net.zincstudios.scgextra.entity.common.gun.MountedGun;
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
public class CogDevastatorEntity extends GunnerEntity implements GeoEntity, Gunner, BulletSpawnOffset {

    public static final Vec3 MACHINE_GUN_OFFSET = new Vec3(-0.7,2.1,0.4);
    public static final Vec3 SHOTGUN_OFFSET = new Vec3(-0.7,2.6,0.5);
    public static final Vec3 GATLING_GUN_OFFSET = new Vec3(0.7,2.6,0.3);

    private static final EntityDataAccessor<Integer> TARGET_ID =
            SynchedEntityData.defineId(CogDevastatorEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);
    private final MountedGun mountedMachineGun;
    private final MountedGun mountedShotgun;
    private final MountedGun mountedGatlingGun;

    @Nullable
    private LivingEntity clientSideCachedTarget;

    public CogDevastatorEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.mountedMachineGun = new HeadAttachedMountedGun(this,
                new CustomSimulatedGun.Builder(ModItems.PRUSH_GUN.get().getGun())
                        .burstAmount(16)
                        .burstInterval(2)
                        .fireRate(70)
                        .maxRange(15)
                        .idealRange(10)
                        .gunIndex(0)
                        .build(),
                MACHINE_GUN_OFFSET,
                BrainUtils::isNotStunned
        );
        this.mountedShotgun = new HeadAttachedMountedGun(this,
                new CustomSimulatedGun.Builder(ModItems.JACKHAMMER.get().getGun())
                        .burstAmount(3)
                        .burstInterval(10)
                        .maxRange(6)
                        .idealRange(5)
                        .fireRate(120)
                        .gunIndex(1)
                        .build(),
                SHOTGUN_OFFSET,
                BrainUtils::isNotStunned
        );
        this.mountedGatlingGun = new HeadAttachedMountedGun(this,
                new CustomSimulatedGun.Builder(ModItems.GATTALER.get().getGun())
                        .burstAmount(32)
                        .burstInterval(1)
                        .fireRate(80)
                        .maxRange(10)
                        .idealRange(8)
                        .gunIndex(2)
                        .build(),
                GATLING_GUN_OFFSET,
                BrainUtils::isNotStunned
        );
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F, 1.0f, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true,
                player -> !player.isSpectator()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.MAX_HEALTH, 400.0D);
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();

        if (!this.level().isClientSide) {
            if (target != null) {
                this.mountedMachineGun.tick(target, 2f);
                this.mountedShotgun.tick(target, 2.4f);
                this.mountedGatlingGun.tick(target, 1.6f);
            }
        } else {
            if (target != null) {
                this.getLookControl().setLookAt(target, 90.0F, 90.0F);
            }
        }
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target instanceof Player player && (player.isCreative() || player.isSpectator())) {
            target = null;
        }
        super.setTarget(target);
        this.entityData.set(TARGET_ID, target == null ? 0 : target.getId());
    }

    @Override
    public @Nullable LivingEntity getTarget() {
        if (this.level().isClientSide()) {
            if (this.clientSideCachedTarget != null) {
                return this.clientSideCachedTarget;
            } else {
                Entity entity = this.level().getEntity(this.entityData.get(TARGET_ID));
                if (entity instanceof LivingEntity livingEntity) {
                    this.clientSideCachedTarget = livingEntity;
                    return this.clientSideCachedTarget;
                } else {
                    return null;
                }
            }
        } else {
            return super.getTarget();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ID.equals(key)) {
            this.clientSideCachedTarget = null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TARGET_ID, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            if (state.getAnimatable().getTarget() != null) {
                state.setAnimation(RawAnimation.begin().thenLoop("aim"));
            } else {
                state.setAnimation(RawAnimation.begin().thenLoop("idle"));
            }
            return PlayState.CONTINUE;
        }));

        controllers.add(new AnimationController<>(this, "behaviour", 0, state -> PlayState.STOP)
                .triggerableAnim("stun", RawAnimation.begin().thenPlayAndHold("stun_start"))
                .triggerableAnim("end_stun", RawAnimation.begin().thenPlay("stun_end"))
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
    protected @Nullable SoundEvent getAmbientSound() {
        return CogSounds.COG_DEVASTATOR_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return CogSounds.GENERAL_HEAVY_HURT.get();
    }

    @Override
    public Vec3 getBulletSpawnOffset(int gunIndex) {
        return switch (gunIndex) {
            case 0 -> MACHINE_GUN_OFFSET.yRot(-this.yHeadRot * Mth.DEG_TO_RAD);
            case 1 -> SHOTGUN_OFFSET.yRot(-this.yHeadRot * Mth.DEG_TO_RAD);
            case 2 -> GATLING_GUN_OFFSET.yRot(-this.yHeadRot * Mth.DEG_TO_RAD);
            default -> throw new IllegalArgumentException();
        };
    }
}
