package net.zincstudios.scgextra.entity.neutral.nether.netherite_eater;

import net.minecraft.world.entity.ai.goal.Goal;

public class NetheriteEaterRunGoal extends Goal{
    private final NetheriteEaterEntity entity;
    private final float speedModifier;

    public NetheriteEaterRunGoal(NetheriteEaterEntity mob, float speed){
        this.entity = mob;
        this.speedModifier = speed;
    }

    @Override
    public boolean canUse() {
        return this.entity.getTarget() !=null && this.entity.distanceToSqr(this.entity.getTarget())<=400 && this.entity.distanceToSqr(this.entity.getTarget())>=36;
    }
    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }
    @Override
    public void start() {
        super.start();
        this.entity.setSpeed(speedModifier);
        this.entity.setRunning(true);
    }
    @Override
    public void tick() {
        super.tick();
        if(this.entity.getTarget()!=null){
            this.entity.getNavigation().moveTo(this.entity.getTarget(), speedModifier);
        }
    }
    @Override
    public void stop() {
        super.stop();
        this.entity.setRunning(false);
    }
}
