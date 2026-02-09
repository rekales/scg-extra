package com.daragetsu.scgextra.entity.guardian_statue;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

// NOTE: check Shulker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuardianStatueEntity extends Monster {

    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET;

    @Nullable
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    public GuardianStatueEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        // TODO: no body rotations
        // ok maybe yes body rotations
        // TODO: no body rotation when idle
        // TODO: rotate slowly towards target?

        this.goalSelector.addGoal(4, new LaserAttackGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // TODO: broaden to target everything but whaler faction mobs
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> !((Player) player).isCreative() && !player.isSpectator()));
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

    protected static class LaserAttackGoal extends Goal {
        private final GuardianStatueEntity self;
        private int attackTime;

        public LaserAttackGoal(GuardianStatueEntity self) {
            this.self = self;
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.self.getTarget();
            return livingentity != null && livingentity.isAlive();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && this.self.getTarget() != null
                    && this.self.distanceToSqr(this.self.getTarget()) > 9.0F;
        }

        @Override
        public void start() {
            this.attackTime = -10;
            LivingEntity target = this.self.getTarget();
            if (target != null) {
                this.self.getLookControl().setLookAt(target, 90.0F, 90.0F);
            }
            this.self.hasImpulse = true;  // TODO: figure out what impulse is
        }

        @Override
        public void stop() {
            this.self.setActiveAttackTarget(0);
            this.self.setTarget(null);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.self.getTarget();
            if (livingentity != null) {
                this.self.getNavigation().stop();
                this.self.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
                if (!this.self.hasLineOfSight(livingentity)) {
                    this.self.setTarget(null);
                } else {
                    ++this.attackTime;
                    if (this.attackTime == 0) {
                        this.self.setActiveAttackTarget(livingentity.getId());
                        if (!this.self.isSilent()) {
                            this.self.level().broadcastEntityEvent(this.self, (byte)21);
                        }
                    } else if (this.attackTime >= this.self.getAttackDuration()) {
                        float f = 1.0F;
                        if (this.self.level().getDifficulty() == Difficulty.HARD) {
                            f += 2.0F;
                        }

                        livingentity.hurt(this.self.damageSources().indirectMagic(this.self, this.self), f);
                        livingentity.hurt(this.self.damageSources().mobAttack(this.self), (float)this.self.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        this.self.setTarget(null);
                    }

                    super.tick();
                }
            }

        }
    }
}
