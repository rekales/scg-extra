package net.zincstudios.scgextra.entity.rrc.drone;

import java.util.EnumSet;
import java.util.Set;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import top.ribs.scguns.init.ModEffects;

public class StunnedGoal extends Goal{

    private final DroneEntity mob;
    protected int tick = 0;
    public StunnedGoal(DroneEntity en){
        this.mob = en;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return this.mob.isStunned();
    }

    @Override
    public void start() {
        this.tick = 0;
        this.mob.getNavigation().stop();
        this.mob.triggerAnim("behaviour", "stun");
        Set<WrappedGoal> goals = this.mob.goalSelector.getAvailableGoals();
        for(WrappedGoal goal : goals){
            if(!goal.getGoal().equals(this)){
                goal.stop();
            }
        }
    }

    @Override
    public void stop() {
        this.tick = 0;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().stop();
        this.mob.triggerAnim("behaviour", "stun");
        if(this.tick > 60){
            this.mob.removeEffect(ModEffects.DEAFENED.get());
            this.mob.removeEffect(ModEffects.BLINDED.get());
        }
        stunVisuals();
        this.tick++;
    }
    @Override
    public boolean canContinueToUse() {
        return this.mob.isStunned();
    }
    private void stunVisuals(){
        Level level = this.mob.level();
        if(!level.isClientSide){
            ServerLevel sLevel = (ServerLevel) level;
            sLevel.sendParticles(
                ParticleTypes.SMOKE, 
                this.mob.getX(), 
                this.mob.getY()+2.5, 
                this.mob.getZ(), 
                10, 
                0.3, 
                0.1, 
                0.3, 
                0.05
            );
            Scoreboard scoreboard = sLevel.getScoreboard();
            PlayerTeam team = scoreboard.getPlayerTeam("red");
            if (team == null) {
                team = scoreboard.addPlayerTeam("red");
            }
            team.setColor(ChatFormatting.RED);
            scoreboard.addPlayerToTeam(this.mob.getStringUUID(), team);
            this.mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
        }
    }
}
