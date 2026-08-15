package net.zincstudios.scgextra.entity.common.brain;

import com.google.common.collect.ImmutableMap;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.zincstudios.scgextra.entity.ModBrainMemories;

public class HandleStunnedVisuals extends Behavior<LivingEntity> {

    public static final String TEAM_NAME = "stunned_red_outline";

    public HandleStunnedVisuals() {
        super(ImmutableMap.of(
                ModBrainMemories.STUNNED.get(), MemoryStatus.VALUE_PRESENT
        ), 600);
    }

    @Override
    protected boolean canStillUse(ServerLevel level, LivingEntity entity, long gameTime) {
        return entity.getBrain().hasMemoryValue(ModBrainMemories.STUNNED.get());
    }

    // TODO: redo to not use teams and glowing effect to not interfere with those vanilla features
    @Override
    protected void start(ServerLevel level, LivingEntity entity, long gameTime) {
        Scoreboard scoreboard = level.getScoreboard();
        PlayerTeam team = scoreboard.getPlayerTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.addPlayerTeam(TEAM_NAME);
        }
        team.setColor(ChatFormatting.RED);
        scoreboard.addPlayerToTeam(entity.getStringUUID(), team);

        entity.addEffect(new MobEffectInstance(
                MobEffects.GLOWING,
                (int) entity.getBrain().getTimeUntilExpiry(ModBrainMemories.STUNNED.get()))
        );
    }

    @Override
    protected void stop(ServerLevel level, LivingEntity entity, long gameTime) {
        entity.removeEffect(MobEffects.GLOWING);
    }
}