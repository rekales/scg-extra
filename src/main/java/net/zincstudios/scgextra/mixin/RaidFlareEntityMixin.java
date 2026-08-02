package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.zincstudios.scgextra.raid.WaveRaidData;
import net.zincstudios.scgextra.raid.WaveRaidManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ribs.scguns.config.RaidConfig;
import top.ribs.scguns.entity.projectile.RaidFlareEntity;
import top.ribs.scguns.entity.raid.RaidManager;

@Mixin(value = RaidFlareEntity.class, remap = false)
public class RaidFlareEntityMixin {

    @WrapOperation(
            method = "performBurst",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/ribs/scguns/config/RaidConfig;getRaidByRaidId(Ljava/lang/String;)Ltop/ribs/scguns/config/RaidConfig$RaidData;"
            )
    )
    private static RaidConfig.RaidData atGetRaidByRaidId(String raidId, Operation<RaidConfig.RaidData> original,
                                                         @Local(name = "serverLevel") ServerLevel serverLevel,
                                                         @Local(name = "player") ServerPlayer player) {
        RaidManager raidManager = RaidManager.get(serverLevel);
        WaveRaidManager waveRaidManager = WaveRaidManager.get(serverLevel);
        if (raidManager.hasActiveRaid() || waveRaidManager.hasActiveRaid()) return original.call(raidId);

        WaveRaidData waveRaidData = WaveRaidData.getWaveRaidFromFlareRaid(raidId);
        if (waveRaidData == null) return original.call(raidId);
        waveRaidManager.startRaid(waveRaidData, serverLevel, player);
        return null;
    }

}
