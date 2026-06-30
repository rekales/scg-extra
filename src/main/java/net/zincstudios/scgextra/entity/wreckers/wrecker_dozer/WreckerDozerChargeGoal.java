package net.zincstudios.scgextra.entity.wreckers.wrecker_dozer;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.sounds.WreckersSounds;

import java.util.EnumSet;
import java.util.List;

public class WreckerDozerChargeGoal extends Goal {

    private static final int WARNING_TICKS = 10;
    private static final int RECOVER_TICKS = 6;

    private final WreckerDozerEntity dozer;
    private final int cooldownTicks;
    private final float range;
    private final float damage;

    private long cooldownEnd;
    private int warningTimer = -1;
    private int recoverTimer = 0;

    public WreckerDozerChargeGoal(WreckerDozerEntity dozer, int cooldownTicks, float range, float damage) {
        this.dozer = dozer;
        this.cooldownTicks = cooldownTicks;
        this.range = range;
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
        return this.dozer.distanceTo(target) <= this.range;
    }

    @Override
    public boolean canContinueToUse() {
        return this.warningTimer >= 0 || this.recoverTimer > 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        this.warningTimer = WARNING_TICKS;
        this.dozer.getNavigation().stop();
        this.dozer.setChargeWarning(true);
        this.dozer.playSound(WreckersSounds.DOZER_CHARGE.get(), 1.2F, 1.0F);
    }

    @Override
    public void stop() {
        this.warningTimer = -1;
        this.recoverTimer = 0;
        this.dozer.setChargeWarning(false);
        this.cooldownEnd = this.dozer.level().getGameTime() + this.cooldownTicks;
    }

    @Override
    public void tick() {
        this.dozer.getNavigation().stop();
        LivingEntity target = this.dozer.getTarget();
        if (target != null) {
            this.dozer.getLookControl().setLookAt(target, 30.0F, 30.0F);
        }

        if (this.warningTimer > 0) {
            this.spawnWarningParticles();
            this.warningTimer--;
            return;
        }
        if (this.warningTimer == 0) {
            this.warningTimer = -1;
            this.detonate();
            this.recoverTimer = RECOVER_TICKS;
            return;
        }
        if (this.recoverTimer > 0) {
            this.recoverTimer--;
        }
    }

    private void spawnWarningParticles() {
        if (!(this.dozer.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 vent = this.dozer.position().add(0.0D, this.dozer.getBbHeight() * 0.85D, 0.0D);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, vent.x, vent.y, vent.z, 6, 0.4D, 0.2D, 0.4D, 0.02D);
        if (CommonConfig.enableAbilityWarning) {
            Vec3 front = this.dozer.position()
                    .add(this.dozer.getForward().scale(this.range * 0.5D))
                    .add(0.0D, 1.0D, 0.0D);
            level.sendParticles(ParticleTypes.FLASH, front.x, front.y, front.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void detonate() {
        if (!(this.dozer.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 center = this.dozer.position().add(this.dozer.getForward().scale(this.range * 0.4D));
        AABB area = new AABB(center, center).inflate(this.range);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != this.dozer && e.isAlive() && !this.dozer.isAlliedTo(e));
        for (LivingEntity victim : victims) {
            if (victim.distanceToSqr(center) <= this.range * this.range) {
                victim.hurt(this.dozer.damageSources().mobAttack(this.dozer), this.damage);
                Vec3 push = victim.position().subtract(this.dozer.position()).normalize().scale(0.8D);
                victim.push(push.x, 0.4D, push.z);
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 1.0D, center.z, 4, 1.0D, 0.5D, 1.0D, 0.0D);
    }
}
