package net.zincstudios.scgextra.entity.rrc.copper_knight;

import java.util.EnumSet;

import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.world.entity.ai.goal.Goal;

public class CopperKnightKickGoal extends Goal{
    private int cooldown = 0;
    private int ticks = 0;
    private final CopperKnightEntity parent;
    public CopperKnightKickGoal(CopperKnightEntity mob){
        this.parent = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }
    @Override
    public boolean canUse() {
        if(this.cooldown>0){
            this.cooldown--;
        }
        return this.parent.getTarget()!=null && this.parent.distanceToSqr(this.parent.getTarget())<=2 && this.cooldown<=0;
    }
    @Override
    public boolean canContinueToUse() {
        return this.parent.getTarget()!=null && this.ticks <= 4;
    }
    @Override
    public void start() {
        super.start();
        this.cooldown = 25;
        this.ticks = 0;
        this.parent.triggerAnim("kick", "kick");
    }
    @Override
    public void tick() {
        super.tick();
        this.ticks++;
        if(this.parent.getTarget()!=null){
            this.parent.lookAt(Anchor.EYES, this.parent.getTarget().position());
        }
    }
    @Override
    public void stop() {
        super.stop();
        if(this.parent.getTarget()!=null){
            this.parent.getTarget().hurt(this.parent.damageSources().mobAttack(this.parent), 7);
            double dx = this.parent.getX() - this.parent.getTarget().getX();
            double dz = this.parent.getZ() - this.parent.getTarget().getZ();
            this.parent.getTarget().knockback(2, dx, dz);
        }
    }
}