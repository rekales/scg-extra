package com.daragetsu.scgextra.entity.guardian_statue;

import com.daragetsu.scgextra.Faction;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;
import java.util.function.Supplier;

// TODO: add datas
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuardianStatueEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    @Nonnull
    private Direction prefferedDirection = Direction.NORTH;

    @Nullable
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    public GuardianStatueEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.setPersistenceRequired();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new GuardianLaserAttackGoal(this));
        this.goalSelector.addGoal(8, new ResetLookToDirection(this, ()->this.prefferedDirection, 1.0F));

        // Bosses will prioritize players and does not require line of sight to maintain targeting to avoid cheese
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                // Avoid retaliation from friendly fire
                if (this.mob.getLastHurtByMob() != null && Faction.isFriendlies(this.mob, this.mob.getLastHurtByMob())) {
                    return false;
                }
                return super.canUse();
            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true,
                entity -> Faction.isEnemies(this, entity)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        this.prefferedDirection = Direction.fromYRot(this.getYRot());
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000)
                .add(Attributes.ARMOR, 12);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Centering the entity to the block
            Vec3 oldPos = this.position();
            Vec3 newPos = BlockPos.containing(oldPos).getCenter();
            newPos = new Vec3(newPos.x, oldPos.y, newPos.z);
            if (!oldPos.equals(newPos)) {
                this.setPos(newPos);
            }
        }
    }

    @Override
    public void aiStep() {
        if (this.isAlive()) {
            if (this.level().isClientSide) {

                if (this.hasActiveAttackTarget()) {
                    if (this.clientSideAttackTime < this.getAttackDuration()) {
                        ++this.clientSideAttackTime;
                    }

                    LivingEntity livingentity = this.getActiveAttackTarget();
                    if (livingentity != null) {
                        this.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                        this.getLookControl().tick();
                        double d5 = this.getAttackAnimationScale(0.0F);
                        double d0 = livingentity.getX() - this.getX();
                        double d1 = livingentity.getY(0.5F) - this.getEyeY();
                        double d2 = livingentity.getZ() - this.getZ();
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        double d4 = this.random.nextDouble();
                    }
                }
            }

            if (this.hasActiveAttackTarget()) {
                this.setYRot(this.yHeadRot);
            }
        }

        super.aiStep();
    }

    public void setActiveAttackTarget(int activeAttackTargetId) {
        this.entityData.set(DATA_ID_ATTACK_TARGET, activeAttackTargetId);
    }

    public float getClientSideAttackTime() {
        return (float)this.clientSideAttackTime;
    }

    public int getAttackDuration() {
        return 80;
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
    }

    public float getAttackAnimationScale(float partialTick) {
        return ((float)this.clientSideAttackTime + partialTick) / (float)this.getAttackDuration();
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level().isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level().getEntity((Integer)this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity)entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_ID_ATTACK_TARGET.equals(key)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_ATTACK_TARGET, 0);
    }

    static {
        DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(GuardianStatueEntity.class, EntityDataSerializers.INT);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }


    public static class ResetLookToDirection extends Goal {

        protected final Mob mob;
        protected final float turnSpeed;
        private final Supplier<Direction> dirSupplier;

        public ResetLookToDirection(Mob mob, Supplier<Direction> dirSupplier, float turnSpeed) {
            this.mob = mob;
            this.dirSupplier = dirSupplier;
            this.turnSpeed = turnSpeed;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            this.mob.setYRot(Mth.approachDegrees(
                    this.mob.getYRot(),
                    getPreferredDirection().toYRot(),
                    this.turnSpeed
            ));

            // TODO: force updates to client or override look control to have a slower turn rate

            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
        }

        public Direction getPreferredDirection() {
            Direction dir = this.dirSupplier.get();
            if (dir == Direction.UP || dir == Direction.DOWN) return Direction.NORTH;
            return dir;
        }
    }
}
