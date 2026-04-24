package net.zincstudios.scgextra.entity.asgharian.candlefiend;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import top.ribs.scguns.init.ModEffects;

import java.util.EnumSet;

// Modified and simplified MeleeAttackGoal
// Removed canPenalize because dead code
public class CandleFiendAttackGoal extends Goal {

    private static final int SLAM_COOLDOWN = 200;
    private static final int SLAM_HURT_DELAY = 15;
    private static final int SLASH_SECOND_HURT_DELAY = 12;

    private final CandleFiendEntity mob;
    private final boolean followingTargetEvenIfNotSeen;
    private final int attackInterval;
    private Path path;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int attackVar = 0;  // 0: none, 1: slash, 2: slam
    private int hurtDelay = -1;
    private int stateClearDelay = -1;
    private int slamCooldown = 0;

    public CandleFiendAttackGoal(CandleFiendEntity mob, int attackIntervalTicks, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.attackInterval = attackIntervalTicks;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else {
            this.path = this.mob.getNavigation().createPath(target, 0);
            if (this.path != null) {
                return true;
            } else {
                return this.getAttackReachSqr(target) >= this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());
            }
        }
    }

    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        } else if (!target.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else if (!this.mob.isWithinRestriction(target.blockPosition())) {
            return false;
        } else {
            return !(target instanceof Player) || !target.isSpectator() && !((Player)target).isCreative();
        }
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, 1);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
        this.slamCooldown = SLAM_COOLDOWN;
    }

    public void stop() {
        LivingEntity target = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(target)) {
            this.mob.setTarget(null);
        }

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
            double distToEnemySqr = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(target))
                    && this.ticksUntilNextPathRecalculation <= 0
                    && (this.pathedTargetX == 0.0D
                    && this.pathedTargetY == 0.0D
                    && this.pathedTargetZ == 0.0D
                    || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D
                    || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = target.getX();
                this.pathedTargetY = target.getY();
                this.pathedTargetZ = target.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                if (distToEnemySqr > 1024.0D) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (distToEnemySqr > 256.0D) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(target, 1)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack -= ticksUntilNextAttack > 0 ? 1 : 0;
            this.slamCooldown -= slamCooldown > 0 ? 1 : 0;
            this.checkAndPerformAttack(target, distToEnemySqr);

            if (this.hurtDelay > 0) {
                this.hurtDelay--;
            } else {
                if (this.attackVar != 0) {
                    double reach = this.getAttackReachSqr(target);
                    if (this.attackVar == 1 && distToEnemySqr <= reach * 1.2) {
                        this.mob.doHurtTarget(target);
                    } else if (this.attackVar == 2 && distToEnemySqr <= reach * 1.6) {
                        target.hurt(target.damageSources().mobAttack(this.mob), 30);
                    }
                    this.attackVar = 0;
                }
            }

            if (this.stateClearDelay > 0) {
                this.stateClearDelay--;
            } else {
                if (this.mob.getBehaviourState() == CandleFiendEntity.BehaviorState.SLASH
                    || this.mob .getBehaviourState() == CandleFiendEntity.BehaviorState.SLAM) {
                    this.mob.setBehaviorState(CandleFiendEntity.BehaviorState.NONE);
                }
            }
        }
    }

    protected void checkAndPerformAttack(LivingEntity target, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(target);

        if (this.mob.getBehaviourState() == CandleFiendEntity.BehaviorState.NONE
                && distToEnemySqr <= d0
                && this.ticksUntilNextAttack <= 0) {

            this.attackVar = this.slamCooldown > 0 ? 1 : 2;
            if (this.attackVar == 1) {
                this.mob.setBehaviorState(CandleFiendEntity.BehaviorState.SLASH);
                target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 80));
                this.mob.doHurtTarget(target);
                this.hurtDelay = SLASH_SECOND_HURT_DELAY;
                this.stateClearDelay = 20;
            } else {
                this.mob.setBehaviorState(CandleFiendEntity.BehaviorState.SLAM);
                this.hurtDelay = SLAM_HURT_DELAY;
                this.stateClearDelay = 30;
                this.slamCooldown = SLAM_COOLDOWN;
            }

            this.ticksUntilNextAttack = this.attackInterval;
            this.mob.swing(InteractionHand.MAIN_HAND);
        }
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }
}
