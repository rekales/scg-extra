package net.zincstudios.scgextra.entity.neutral.end.end_stone_crab;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EndStoneCrabMeleeAttackGoal extends Goal{
    protected final EndStoneCrabEntity mob;
    private int cooldown = 0;
    private boolean bit = false;

    public EndStoneCrabMeleeAttackGoal(EndStoneCrabEntity mob) {
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
        this.mob.getLookControl().setLookAt(target);
        this.mob.lookAt(target, 20, 20);
        this.mob.getNavigation().moveTo(target, 0.5);
        if(this.mob.distanceToSqr(target)<=12){
            this.mob.doHurtTarget(target);
            this.bit = true;
        }
    }
}