package net.zincstudios.scgextra.entity.asgharian.soulripper;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.AbilityGoal;
import net.zincstudios.scgextra.entity.projectile.SoulFireball;

public class SoulRipperFireballGoal extends AbilityGoal<SoulRipperEntity> {

    public static final int FIREBALL_INTERVAL_TICKS = 15;

    protected int volleys = 0;

    public SoulRipperFireballGoal(SoulRipperEntity mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (!super.canUse()) return false;
        assert target != null;
        return this.mob.getLives() <= 2
                && this.mob.canFireball()
                && this.mob.distanceToSqr(target) > 4.0D;
    }

    @Override
    public void start() {
        super.start();
        if (this.volleys <= 0) {
            this.volleys = this.mob.getLives() == 0 ? 3 : 1;
        }
    }

    @Override
    protected void resetCooldown() {
        if (this.volleys > 0) {
            this.cooldownEnd = this.mob.tickCount + FIREBALL_INTERVAL_TICKS;
        } else {
            super.resetCooldown();
        }
    }

    @Override
    public boolean activate() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            throwFireball(this.mob.getTarget());
            this.volleys--;
        }
        return false;
    }

    // Copied and altered from Ghast.GhastShootFireballGoal
    private void throwFireball(LivingEntity target) {
        Vec3 vec3 = this.mob.getViewVector(1.0F);
        double d2 = target.getX() - (this.mob.getX() + vec3.x * 4.0D);
        double d3 = target.getY(0.5D) - (0.5D + this.mob.getY(0.5D));
        double d4 = target.getZ() - (this.mob.getZ() + vec3.z * 4.0D);

        SoulFireball soulFireball = new SoulFireball(this.mob.level(), this.mob, d2, d3, d4, 2.5f);
        soulFireball.setPos(this.mob.getX() + vec3.x * 4.0D, this.mob.getY(0.5D) + 0.5D, soulFireball.getZ() + vec3.z * 4.0D);
        this.mob.level().addFreshEntity(soulFireball);
    }
}
