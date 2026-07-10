package net.zincstudios.scgextra.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.zincstudios.scgextra.raid.WaveRaidManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ribs.scguns.entity.raid.RaidManager;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

@Mixin(value = RaidManager.class, remap = false, priority = 800)
public class RaidManagerMixin {

    @Shadow
    @Final
    private static Map<ResourceLocation, RaidManager> INSTANCES;

    @Inject(method = "hasActiveRaid", at = @At("HEAD"), cancellable = true)
    private void onHasActiveRaid(CallbackInfoReturnable<Boolean> cir) {
        RaidManager self = (RaidManager) (Object) this;
        ResourceLocation key = SCGExtra$findRaidManagerKey(self);
        if (key != null) {
            if (WaveRaidManager.get(key).hasActiveRaid()) {
                cir.setReturnValue(true);
                cir.cancel();
            }
        }
    }

    @Inject(method = "surrenderRaid", at = @At("HEAD"))
    private static void onSurrenderRaid(ServerLevel level, CallbackInfo ci) {
        WaveRaidManager manager = WaveRaidManager.get(level);
        manager.endRaid(level, false);
    }

    @Unique
    private @Nullable ResourceLocation SCGExtra$findRaidManagerKey(RaidManager instance) {
        for (Map.Entry<ResourceLocation, RaidManager> entry : INSTANCES.entrySet()) {
            if (Objects.equals(entry.getValue(), instance)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
