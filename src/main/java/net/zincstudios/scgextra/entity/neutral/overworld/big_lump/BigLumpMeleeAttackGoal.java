package net.zincstudios.scgextra.entity.neutral.overworld.big_lump;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class BigLumpMeleeAttackGoal extends Goal{
    protected final BigLumpEntity mob;
    private int cooldown = 0;
    private boolean bit = false;

    public BigLumpMeleeAttackGoal(BigLumpEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if(this.cooldown>0){this.cooldown--;}
        return this.mob.getTarget()!=null && this.cooldown == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getTarget()!=null && !this.bit;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 20;
        this.bit = false;
    }

    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.mob.getTarget();
        if(target==null)return;
        this.mob.lookAt(target, 10, 10);
        if(this.mob.distanceToSqr(target)>=25){
            this.mob.getNavigation().moveTo(target, 0.5);
        }else{
            this.mob.doHurtTarget(target);
            this.bit = true;
        }
    }
}