package net.zincstudios.scgextra.entity.asgharian.surgeon;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.entity.asgharian.GoalState;
import net.zincstudios.scgextra.entity.asgharian.SimpleBurstGunAttackGoal;
import top.ribs.scguns.init.ModEffects;

import java.util.Objects;

// Much copied from MeleeAttackGoal
public class AsgharSurgeonAttackGoal<T extends AsgharSurgeonEntity> extends SimpleBurstGunAttackGoal<T> {

    public static final GoalState MELEE_STATE = new GoalState("asghar_surgeon_melee_state");

    private static final int MELEE_DAMAGE_DELAY = 12;  // match with animation
    private static final int MELEE_FULL_DURATION = 25;  // match with animation
    private int meleeTicks = 0;

    public AsgharSurgeonAttackGoal(T mob) {
        super(mob, 12, 3);
        this.attackInterval = 60;
        this.maxRange = 16;
    }

    @Override
    public void start() {
        super.start();
        this.meleeTicks = 0;
    }

    @Override
    public void tick() {
        handleMeleeAttack();

        if (!Objects.equals(this.getGoalState(), MELEE_STATE)) {
            super.tick();
        }
    }

    protected void handleMeleeAttack() {
        if (Objects.equals(this.getGoalState(), FIRING_STATE)) return;

        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            double distToEnemySqr = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            checkAndPerformAttack(target, distToEnemySqr);

            if (Objects.equals(this.getGoalState(), MELEE_STATE)) {
                this.meleeTicks++;
                if (this.meleeTicks == MELEE_DAMAGE_DELAY) {
                    if (distToEnemySqr <= this.getAttackReachSqr(target) * 1.2) {
                        this.meleeDamageTarget(target);
                    }
                } else if (this.meleeTicks >= MELEE_FULL_DURATION) {
                    this.setGoalState(AIMING_STATE);
                }
            }
        }
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        if (Objects.equals(this.getGoalState(), MELEE_STATE)) return;  // don't do another attack if already performing

        double reachSqr = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= reachSqr) {
            this.setGoalState(MELEE_STATE);
            this.meleeTicks = 0;
        }
    }

    protected void meleeDamageTarget(LivingEntity target) {
        this.mob.doHurtTarget(target);
        target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 80));
    }

    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    @Override
    protected float getAccuracyModifier() {
        return super.getAccuracyModifier() * 0.4f;
    }
}
