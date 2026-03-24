package net.zincstudios.scgextra.entity.rrc.flaminghead;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import top.ribs.scguns.entity.projectile.FireRoundEntity;
import top.ribs.scguns.init.ModEntities;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.init.ModSounds;

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
        if(!(this.ticks%15==0))return;
        for (int i = 0; i < 360; i += 10) {
            double rad = Math.toRadians(i);
            double x = this.parent.getX() + Math.cos(rad) * 6;
            double z = this.parent.getZ() + Math.sin(rad) * 6;
            FireRoundEntity en = new FireRoundEntity(
                ModEntities.FIRE_ROUND_PROJECTILE.get(), 
                this.parent.level(), 
                this.parent, 
                ModItems.BASKER.get().getDefaultInstance(), 
                ModItems.BASKER.get(),
                ModItems.BASKER.get().getGun()
            );
            en.setPos(this.parent.position().add(0, 2, 0));
            double dx = x - this.parent.getX();
            double dy = this.parent.getY() - (this.parent.getY()+2);
            double dz = z - this.parent.getZ();
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            en.setDeltaMovement(
                dx / dist * 1F,
                dy / dist * 1F,
                dz / dist * 1F
            );
            this.parent.level().addFreshEntity(en);
            this.parent.level().playSound(null, this.parent.getX(), this.parent.getY(), this.parent.getZ(), ModSounds.FLAMETHROWER_FIRE_2.get(), SoundSource.HOSTILE, 0.2F, 1.2F);
        }
    }
}
