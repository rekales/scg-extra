package net.zincstudios.scgextra.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.ribs.scguns.config.RaidConfig;
import top.ribs.scguns.entity.raid.ActiveRaid;
import top.ribs.scguns.entity.raid.RaidManager;

@Mixin(value = RaidManager.class, remap = false)
public interface RaidManagerInvoker {

    @Invoker("findHenchmanSpawnPos")
    Vec3 scgextra$invokeFindHenchmanSpawnPos(ServerLevel level, Vec3 center, int radius);

    @Invoker("spawnHenchman")
    Mob scgextra$invokeSpawnHenchman(ActiveRaid raid, RaidConfig.HenchmanType type, ServerLevel level, Vec3 spawnPos);

    @Invoker("spawnBoss")
    Mob scgextra$invokeSpawnBoss(ActiveRaid raid, ServerLevel level, Vec3 spawnPos);
}
