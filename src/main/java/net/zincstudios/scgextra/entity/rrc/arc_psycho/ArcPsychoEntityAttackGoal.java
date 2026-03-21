package net.zincstudios.scgextra.entity.rrc.arc_psycho;

import net.minecraft.world.entity.ai.goal.Goal;

public class ArcPsychoEntityAttackGoal extends Goal{
    private final ArcPsychoEntity parent;
    private int cooldown = 0;
    private final int cooldownDur;
    public ArcPsychoEntityAttackGoal(ArcPsychoEntity mob, int cooldown){
        this.parent = mob;
        this.cooldownDur = cooldown;
    }
    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.parent.getTarget()!=null && this.cooldown==0 && this.parent.distanceToSqr(this.parent.getTarget().getX(), this.parent.getY(), this.parent.getTarget().getZ())<16*16;
    }
    @Override
    public boolean canContinueToUse() {
        return false;
    }
    @Override
    public void start() {
        super.start();
        this.cooldown = this.cooldownDur;
        this.parent.triggerAnim("attack", "attack");
        //TODO: Figure out how to shoot a lightning projectile
    }
}