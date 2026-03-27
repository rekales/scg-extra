package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.world.entity.ai.goal.Goal;
import net.zincstudios.scgextra.entity.projectile.FireProjectile;

public class ThrowFlamesGoal extends Goal{
    private final FlamingHeadEntity parent;
    private int ticks = 0;
    public ThrowFlamesGoal(FlamingHeadEntity mob){
        this.parent = mob;
    }
    @Override
    public boolean canUse() {
        return true;
    }
    @Override
    public boolean canContinueToUse() {
        return true;
    }
    @Override
    public void start() {
        super.start();
        this.ticks = 0;
    }
    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        if(!(this.ticks%8==0))return;
        for (int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = this.parent.getX() + Math.cos(rad) * 4;
            double z = this.parent.getZ() + Math.sin(rad) * 4;
            FireProjectile en = new FireProjectile(
                    this.parent.level(),
                    this.parent
                );
            en.setPos(this.parent.position().add(0, 3, 0));
            double dx = x - this.parent.getX();
            double dy = this.parent.getY() - (this.parent.getY()+3);
            double dz = z - this.parent.getZ();
            en.shoot(
                dx,
                dy,
                dz,
                2.5F,
                0F
            );
            this.parent.level().addFreshEntity(en);
        }
    }
}