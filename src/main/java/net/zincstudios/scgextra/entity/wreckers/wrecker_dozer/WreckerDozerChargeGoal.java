package net.zincstudios.scgextra.entity.wreckers.wrecker_dozer;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.Faction;
import net.zincstudios.scgextra.sounds.WreckersSounds;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class WreckerDozerChargeGoal extends Goal {

    private static final int WARNING_TICKS = 100;
    private static final int CHARGE_TICKS = 30;
    private static final double SPEED_MULTIPLIER = 6.0D;
    private static final double MIN_TRIGGER_DISTANCE = 4.0D;
    private static final double MAX_TRIGGER_DISTANCE = 20.0D;

    private final WreckerDozerEntity dozer;
    private final int cooldownTicks;
    private final float damage;
    private final List<LivingEntity> affectedEntities = new ArrayList<>();

    private long cooldownEnd;
    private Vec3 ramDirection = Vec3.ZERO;
    private int duration;

    public WreckerDozerChargeGoal(WreckerDozerEntity dozer, int cooldownTicks, float damage) {
        this.dozer = dozer;
        this.cooldownTicks = cooldownTicks;
        this.damage = damage;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.dozer.isStunned()) {
            return false;
        }
        LivingEntity target = this.dozer.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.dozer.level().getGameTime() < this.cooldownEnd) {
            return false;
        }
        double dist = this.dozer.distanceTo(target);
        return dist >= MIN_TRIGGER_DISTANCE && dist <= MAX_TRIGGER_DISTANCE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.duration < WARNING_TICKS + CHARGE_TICKS && !this.dozer.isStunned();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.duration = 0;
        this.affectedEntities.clear();
        this.affectedEntities.add(this.dozer);
        this.dozer.getNavigation().stop();
        this.dozer.setChargeWarning(true);
        this.dozer.playSound(WreckersSounds.DOZER_CHARGE.get(), 1.2F, 1.0F);
        LivingEntity target = this.dozer.getTarget();
        if (target != null) {
            Vec3 direction = target.position().subtract(this.dozer.position());
            this.ramDirection = new Vec3(direction.x, 0.0D, direction.z).normalize();
        }
    }

    @Override
    public void stop() {
        this.dozer.setChargeWarning(false);
        this.cooldownEnd = this.dozer.level().getGameTime() + this.cooldownTicks;
    }

    @Override
    public void tick() {
        this.duration++;

        if (this.duration <= WARNING_TICKS) {
            this.dozer.getNavigation().stop();
            LivingEntity target = this.dozer.getTarget();
            if (target != null) {
                this.dozer.getLookControl().setLookAt(target, 30.0F, 30.0F);
                Vec3 direction = target.position().subtract(this.dozer.position());
                this.ramDirection = new Vec3(direction.x, 0.0D, direction.z).normalize();
            }
            return;
        }

        if (this.duration == WARNING_TICKS + 1) {
            this.dozer.setChargeWarning(false);
        }

        float ramYaw = (float) Mth.wrapDegrees(Math.toDegrees(Mth.atan2(this.ramDirection.z, this.ramDirection.x)) - 90.0D);
        this.dozer.setYRot(ramYaw);
        this.dozer.yBodyRot = ramYaw;
        this.dozer.yHeadRot = ramYaw;

        double speed = this.dozer.getAttributeValue(Attributes.MOVEMENT_SPEED) * SPEED_MULTIPLIER;
        this.dozer.setDeltaMovement(
                this.ramDirection.x * speed,
                this.dozer.getDeltaMovement().y,
                this.ramDirection.z * speed);

        List<LivingEntity> victims = this.dozer.level().getEntitiesOfClass(LivingEntity.class,
                this.dozer.getBoundingBox().inflate(0.5D));
        for (LivingEntity victim : victims) {
            if (this.affectedEntities.contains(victim)) {
                continue;
            }
            if (!Faction.isFriendlies(this.dozer, victim)) {
                victim.hurt(this.dozer.damageSources().mobAttack(this.dozer), this.damage);
                victim.knockback(1.0D, this.dozer.getX() - victim.getX(), this.dozer.getZ() - victim.getZ());
            }
            this.affectedEntities.add(victim);
        }
    }
}
