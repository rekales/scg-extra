package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.zincstudios.scgextra.entity.common.Stunnable;

public class StunnedWithVisualGoal <T extends PathfinderMob & Stunnable> extends StunnedGoal<T> {
    private boolean stunOut = false;
    public StunnedWithVisualGoal(T mob) {
        super(mob);
    }

    public StunnedWithVisualGoal(T mob, int endAnimDuration) {
        super(mob, endAnimDuration);
    }

    @Override
    public void tick() {
        super.tick();
        stunVisuals();
        stunOut = true;
    }

    @Override
    public void start() {
        stunOut = false;
        super.start();
    }

    protected void stunVisuals(){
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
            if(!stunOut){
                this.mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.mob.getDefaultStunDuration()));
            }
        }
    }
}
