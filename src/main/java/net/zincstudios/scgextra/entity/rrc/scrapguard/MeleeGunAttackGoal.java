package net.zincstudios.scgextra.entity.rrc.scrapguard;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoEntity;
import top.ribs.scguns.entity.ai.AIType;
import top.ribs.scguns.entity.ai.GunAttackGoal;

// Much copied from MeleeAttackGoal
public class MeleeGunAttackGoal<T extends PathfinderMob> extends GunAttackGoal<T> {

    private final int damageDelay;
    private int ticksUntilNextAttack;
    private int ticksUntilDamage;

    public MeleeGunAttackGoal(T shooter, ItemStack gunStack, float speedModifier, AIType aiType, int difficulty) {
        this(shooter, gunStack, speedModifier, aiType, difficulty, 0);
    }

    public MeleeGunAttackGoal(T shooter, ItemStack gunStack, float speedModifier, AIType aiType, int difficulty, int damageDelay) {
        super(shooter, gunStack, speedModifier, aiType, difficulty);
        this.damageDelay = damageDelay;
    }


    @Override
    public void start() {
        super.start();
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.shooter.getTarget();
        if (target != null) {
            double distToEnemySqr = this.shooter.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            checkAndPerformAttack(target, distToEnemySqr);

            if (this.ticksUntilNextAttack > 10) {
                this.attackTime++;  // delay shooting
            }

            if (this.ticksUntilDamage != -1000) {
                if (this.ticksUntilDamage > 0) {
                    this.ticksUntilDamage--;
                } else {
                    if (distToEnemySqr <= this.getAttackReachSqr(target)) {
                        this.shooter.doHurtTarget(target);
                        this.ticksUntilDamage = -1000;
                    }
                }
            }
        }
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0 && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.shooter.swing(InteractionHand.MAIN_HAND);
            this.ticksUntilDamage = this.damageDelay;
            if (this.shooter instanceof GeoEntity geoEntity) {
                geoEntity.triggerAnim("behaviour", "melee");
            }
        }
    }

    protected void checkAndDamageTarget(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= this.getAttackReachSqr(enemy)) {
            this.shooter.doHurtTarget(enemy);
        }
    }

    // From MeleeAttackGoal
    protected double getAttackReachSqr(LivingEntity attackTarget) {
        return (this.shooter.getBbWidth() * 2.0F * this.shooter.getBbWidth() * 2.0F + attackTarget.getBbWidth());
    }

    // From MeleeAttackGoal
    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(30);
    }

}
