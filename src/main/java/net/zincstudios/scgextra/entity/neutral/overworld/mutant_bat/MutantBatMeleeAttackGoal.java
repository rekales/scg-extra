package net.zincstudios.scgextra.entity.neutral.overworld.mutant_bat;

import java.util.EnumSet;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantBatMeleeAttackGoal extends Goal{
    protected final MutantBatEntity mob;
    private int cooldown = 0;
    private boolean bit = false;

    public MutantBatMeleeAttackGoal(MutantBatEntity mob) {
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
        if(this.mob.distanceToSqr(target)>=16){
            this.mob.getNavigation().moveTo(target, 0.5);
        }else{
            this.mob.doHurtTarget(target);
            this.bit = true;
        }
    }
}