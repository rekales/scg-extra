package net.zincstudios.scgextra.raidwave;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import top.ribs.scguns.entity.raid.ActiveRaid;

import java.util.ArrayList;

public final class RaidWaveBossBarUtil {
    private RaidWaveBossBarUtil() {
    }

    public static void updateWaveBossBar(ActiveRaid raid, String raidId, int[][] plans, RaidWaveState state, int trackedAlive, int totalStrictWaves) {
        if (raid.getBossBar() == null) return;
        if (state.isBossReleased()) {
            LivingEntity boss = raid.getBoss();
            if (boss == null || !boss.isAlive()) {
                raid.getBossBar().setVisible(false);
                return;
            }
            float health = boss.getHealth();
            float maxHealth = Math.max(1.0f, boss.getMaxHealth());
            float progress = Math.max(0.0f, Math.min(1.0f, health / maxHealth));
            raid.getBossBar().setVisible(true);
            raid.getBossBar().setName(boss.getDisplayName().copy());
            raid.getBossBar().setProgress(progress);
            return;
        }
        if (state.getWaveIndex() >= totalStrictWaves) {
            raid.getBossBar().setVisible(true);
            raid.getBossBar().setName(Component.literal(getFactionLabel(raidId) + " Wave 4"));
            raid.getBossBar().setProgress(0.0f);
            return;
        }
        int[] currentPlan = plans[state.getWaveIndex()];
        int total = currentPlan[0] + currentPlan[1] + currentPlan[2];
        int alive = Math.min(trackedAlive, total);
        float progress = total <= 0 ? 0.0f : Math.max(0.0f, Math.min(1.0f, (float) alive / (float) total));
        raid.getBossBar().setVisible(true);
        raid.getBossBar().setName(Component.literal(getFactionLabel(raidId) + " Wave " + (state.getWaveIndex() + 1)));
        raid.getBossBar().setProgress(progress);
    }

    public static void syncWaveBossBarPlayers(ServerLevel level, ActiveRaid raid) {
        ServerBossEvent bossBar = raid.getBossBar();
        if (bossBar == null) return;
        double radius = Math.max(128.0, raid.getConfig().spawnConditions().searchRadius());
        Vec3 center = raid.getSpawnCenter();
        ArrayList<ServerPlayer> currentPlayers = new ArrayList<>(bossBar.getPlayers());
        for (ServerPlayer player : currentPlayers) {
            boolean valid = player.isAlive() && !player.isSpectator() && player.position().distanceToSqr(center) <= radius * radius;
            if (!valid) bossBar.removePlayer(player);
        }
        for (ServerPlayer player : level.players()) {
            boolean valid = player.isAlive() && !player.isSpectator() && player.position().distanceToSqr(center) <= radius * radius;
            if (valid && !bossBar.getPlayers().contains(player)) bossBar.addPlayer(player);
        }
    }

    private static String getFactionLabel(String raidId) {
        return switch (raidId) {
            case "fac" -> "FAC";
            case "rrc" -> "RRC";
            case "whale" -> "Whaler";
            default -> raidId.toUpperCase();
        };
    }
}
