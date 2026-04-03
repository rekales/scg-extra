package net.zincstudios.scgextra.entity.common.ai;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.zincstudios.scgextra.entity.common.Stunnable;

public class StunnedWithVisualGoal <T extends PathfinderMob & Stunnable> extends StunnedGoal<T> {

    private boolean smoking = false;

    public StunnedWithVisualGoal(T mob) {
        super(mob);
    }

    public StunnedWithVisualGoal<T> smoking(boolean smoking) {
        this.smoking = smoking;
        return this;
    }

    @Override
    public void start() {
        super.start();
        if (this.mob.level() instanceof ServerLevel level) {
            Scoreboard scoreboard = level.getScoreboard();
            PlayerTeam team = scoreboard.getPlayerTeam("red");
            if (team == null) {
                team = scoreboard.addPlayerTeam("red");
            }
            team.setColor(ChatFormatting.RED);
            scoreboard.addPlayerToTeam(this.mob.getStringUUID(), team);
            this.mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, this.getStunTimer()));
        }

    }

    @Override
    public void stop() {
        super.stop();
        this.mob.removeEffect(MobEffects.GLOWING);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.smoking && this.mob.level() instanceof ServerLevel level) {
            level.sendParticles(
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
        }
    }
}
