package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.projectile.LargeSoulFireball;

public class SoulRipperThrowFireballGoal extends Goal {

    public static final int FIREBALL_INTERVAL_TICKS = 10;

    protected final SoulRipperEntity mob;
    protected final int cooldownDuration;
    protected int cooldownEnd = 0;  // tickCount timestamp
    protected int fireballsLeft = 0;
    protected int fireballCooldown = 0;

    public SoulRipperThrowFireballGoal(SoulRipperEntity mob, int cooldownDuration) {
        this.mob = mob;
        this.cooldownDuration = cooldownDuration;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null
                && this.mob.tickCount > this.cooldownEnd
                && this.mob.isAlive()
                && this.mob.getLives() <= 2
                && this.mob.canFireball()
                && this.mob.distanceToSqr(target) > 4.0D;
    }

    @Override
    public void start() {
        this.fireballsLeft = this.mob.getLives() == 0 ? 3 : 1;
        this.fireballCooldown = 0;
        SCGExtra.LOGGER.debug("fireballs: " + fireballsLeft);

    }

    @Override
    public void tick() {
        if (this.fireballsLeft >= 1) {
            if (this.fireballCooldown <= 0) {
                LivingEntity target = this.mob.getTarget();
                if (target != null) {
                    throwFireball(this.mob.getTarget());
                }
                this.fireballCooldown = FIREBALL_INTERVAL_TICKS;
                this.fireballsLeft--;
            } else {
                this.fireballCooldown--;
            }
        } else {
            this.cooldownEnd = this.mob.tickCount + this.cooldownDuration;
        }
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    // copied from Ghast.GhastShootFireballGoal
    private void throwFireball(LivingEntity target) {
        Vec3 vec3 = this.mob.getViewVector(1.0F);
        double d2 = target.getX() - (this.mob.getX() + vec3.x * 4.0D);
        double d3 = target.getY(0.5D) - (0.5D + this.mob.getY(0.5D));
        double d4 = target.getZ() - (this.mob.getZ() + vec3.z * 4.0D);

        LargeSoulFireball largeSoulFireball = new LargeSoulFireball(this.mob.level(), this.mob, d2, d3, d4, 1);
        largeSoulFireball.setPos(this.mob.getX() + vec3.x * 4.0D, this.mob.getY(0.5D) + 0.5D, largeSoulFireball.getZ() + vec3.z * 4.0D);
        this.mob.level().addFreshEntity(largeSoulFireball);
    }
}
