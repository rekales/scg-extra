package net.zincstudios.scgextra.raidwave;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import top.ribs.scguns.config.RaidConfig;

import java.util.List;

public final class RaidWaveBalanceUtil {
    private RaidWaveBalanceUtil() {
    }

    public static int scaledRaidSpawnRadius(RaidConfig.HenchmenData henchmenData, int minimumRadius, double multiplier) {
        if (henchmenData == null) return minimumRadius;
        int scaled = (int) Math.round(Math.max(1, henchmenData.spawnRadius()) * multiplier);
        return Math.max(minimumRadius, scaled);
    }

    public static void applyRaidBossHealthWithoutMultiplier(LivingEntity boss) {
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(boss.getType());
        if (key == null) return;
        Double targetHealth = switch (key.getPath()) {
            case "armored_whale" -> 700.0D;
            case "fac_tank"-> 400.0D;
            case "flaming_head" -> 600.0D; //write 200 more
            default -> null;
        };
        if (targetHealth == null) return;
        AttributeInstance maxHealth = boss.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(targetHealth);
        }
        boss.setHealth(targetHealth.floatValue());
    }

    public static void addSpawnCandidate(List<Vec3> candidates, Vec3 candidate) {
        if (candidate == null) return;
        for (Vec3 existing : candidates) {
            if (existing.distanceToSqr(candidate) < 1.0) return;
        }
        candidates.add(candidate);
    }
}
