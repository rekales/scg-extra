package com.daragetsu.scgextra.entity.guardian_statue;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

// NOTE: check Shulker
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GuardianStatueEntity extends Monster {

    public GuardianStatueEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        // TODO: no body rotations

//        this.goalSelector.addGoal(4, new GuardianAttackGoal(this));
//        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));

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

    //    static class GuardianAttackGoal extends Goal {
//        private final Guardian guardian;
//        private int attackTime;
//        private final boolean elder;
//
//        public GuardianAttackGoal(Guardian guardian) {
//            this.guardian = guardian;
//            this.elder = guardian instanceof ElderGuardian;
//            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
//        }
//
//        public boolean canUse() {
//            LivingEntity livingentity = this.guardian.getTarget();
//            return livingentity != null && livingentity.isAlive();
//        }
//
//        public boolean canContinueToUse() {
//            return super.canContinueToUse() && (this.elder || this.guardian.getTarget() != null && this.guardian.distanceToSqr(this.guardian.getTarget()) > (double)9.0F);
//        }
//
//        public void start() {
//            this.attackTime = -10;
//            this.guardian.getNavigation().stop();
//            LivingEntity livingentity = this.guardian.getTarget();
//            if (livingentity != null) {
//                this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
//            }
//
//            this.guardian.hasImpulse = true;
//        }
//
//        public void stop() {
//            this.guardian.setActiveAttackTarget(0);
//            this.guardian.setTarget((LivingEntity)null);
//            this.guardian.randomStrollGoal.trigger();
//        }
//
//        public boolean requiresUpdateEveryTick() {
//            return true;
//        }
//
//        public void tick() {
//            LivingEntity livingentity = this.guardian.getTarget();
//            if (livingentity != null) {
//                this.guardian.getNavigation().stop();
//                this.guardian.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
//                if (!this.guardian.hasLineOfSight(livingentity)) {
//                    this.guardian.setTarget((LivingEntity)null);
//                } else {
//                    ++this.attackTime;
//                    if (this.attackTime == 0) {
//                        this.guardian.setActiveAttackTarget(livingentity.getId());
//                        if (!this.guardian.isSilent()) {
//                            this.guardian.level().broadcastEntityEvent(this.guardian, (byte)21);
//                        }
//                    } else if (this.attackTime >= this.guardian.getAttackDuration()) {
//                        float f = 1.0F;
//                        if (this.guardian.level().getDifficulty() == Difficulty.HARD) {
//                            f += 2.0F;
//                        }
//
//                        if (this.elder) {
//                            f += 2.0F;
//                        }
//
//                        livingentity.hurt(this.guardian.damageSources().indirectMagic(this.guardian, this.guardian), f);
//                        livingentity.hurt(this.guardian.damageSources().mobAttack(this.guardian), (float)this.guardian.getAttributeValue(Attributes.ATTACK_DAMAGE));
//                        this.guardian.setTarget((LivingEntity)null);
//                    }
//
//                    super.tick();
//                }
//            }
//
//        }
//    }
}
