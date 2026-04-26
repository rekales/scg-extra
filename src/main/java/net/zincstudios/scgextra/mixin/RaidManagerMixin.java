package net.zincstudios.scgextra.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.raid.WaveRaid;
import net.zincstudios.scgextra.raid.WaveRaidManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.config.RaidConfig;
import top.ribs.scguns.entity.raid.RaidManager;

@Mixin(value = RaidManager.class, remap = false)
public class RaidManagerMixin {

    @Inject(method = "startRaid", at = @At("HEAD"), cancellable = true)
    private void onStartRaid(RaidConfig.RaidData config, ServerLevel level, Vec3 spawnPos, CallbackInfo ci) {
        RaidManager self = (RaidManager) (Object) this;
        WaveRaid raidData = WaveRaid.getWaveRaid(config.raidId());
        if (!self.hasActiveRaid() && raidData != null) {
            WaveRaidManager.get(level).startRaid(raidData, level, spawnPos);
            ci.cancel();
        }
    }

    // TODO: inject hasActiveRaid, hasActiveRaidInDimension

}
