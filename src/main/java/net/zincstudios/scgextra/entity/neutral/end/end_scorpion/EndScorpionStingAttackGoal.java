package net.zincstudios.scgextra.entity.neutral.end.end_scorpion;

import net.zincstudios.scgextra.sounds.NeutralSounds;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EndScorpionStingAttackGoal extends Goal{

    private final EndScorpionEntity mob;
    private int cooldown = 0;

    public EndScorpionStingAttackGoal(EndScorpionEntity entity){
        this.mob = entity;
    }

    @Override
    public boolean canUse() {
        if(this.cooldown>0)this.cooldown--;
        return this.mob.getTarget()!=null && this.cooldown==0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.isStinging()!=false;
    }

    @Override
    public void start() {
        super.start();
        this.cooldown = 120;
        this.mob.setStinging(true);
        this.mob.playSound(NeutralSounds.END_SCORPION_STING.get());
    }
    @Override
    public void tick() {
        super.tick();
        LivingEntity target = this.mob.getTarget();
        if(target == null)return;
        double reach = this.mob.getAttackReachSqr(target);
        double distToEnemySqr = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
        if (distToEnemySqr <= reach) {
            this.mob.doHurtTarget(target);
            this.stop();
        }else{
            this.mob.getNavigation().moveTo(target.getX(), target.getY()+1, target.getZ(), 0.6);
            this.mob.getLookControl().setLookAt(target);
        }
    }
    @Override
    public void stop() {
        super.stop();
    }
}
