package com.daragetsu.scgextra.entity.guardian_statue;

import com.daragetsu.scgextra.SCGExtra;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

// NOTE: Designed to work around the shitass GuardianLaserAttackGoal from the Guardian entity
public class BeamLaserAttackGoal extends Goal {

    private final GuardianStatueEntity mob;
    protected final int maxInterval;
    protected final float range;
    protected int cooldown = 0;
    protected int activeTicks = 0;
    private Vec3 lastPos;

    public BeamLaserAttackGoal(GuardianStatueEntity mob, int maxInterval, float range) {
        this.mob = mob;
        this.maxInterval = maxInterval;
        this.range = range;
        this.lastPos = this.mob.position().add(this.mob.getLookAngle());
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.mob.getTarget();
        return livingentity != null && livingentity.isAlive();
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = this.maxInterval;
    }

    @Override
    public void stop() {
        super.start();
        this.activeTicks = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    // activeTicks 120-101: silence
    // activeTicks 100-61: eye starts glowing
    // activeTicks 60: eye flashes
    // activeTicks 40: beam renders
    // activeTicks 35: beam hits
    // activeTicks 15: beam starts fading
    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return;

        this.mob.setBeamActiveTimer(this.activeTicks);
        if (this.activeTicks > 0) {
            SCGExtra.LOGGER.debug("active: " + this.activeTicks);
            if (this.activeTicks == 35 && this.mob.hasLineOfSight(target) && target.isAlive()) {
                target.hurt(this.mob.damageSources().mobAttack(this.mob), 40F);
            }
            this.activeTicks--;
        } else if (this.cooldown > 0) {
            this.cooldown--;
            if (cooldown%10 == 0) SCGExtra.LOGGER.debug("cooldown: " + this.cooldown);
        } else {
            this.cooldown = maxInterval;
            this.activeTicks = 120;
        }

        if (this.mob.hasLineOfSight(target) && !(0 < activeTicks && activeTicks < 25)) {
            this.lastPos = target.position().add(0,target.getBbHeight()/2,0);
        }
        this.mob.setBeamLookPos(this.lastPos);
        this.mob.getLookControl().setLookAt(this.lastPos.x, this.lastPos.y, this.lastPos.z, 90F, 90F);
    }
}
