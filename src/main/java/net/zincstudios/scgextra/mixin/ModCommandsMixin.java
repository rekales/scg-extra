package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.zincstudios.scgextra.raid.WaveRaidManager;
import net.zincstudios.scgextra.raid.WaveRaidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ribs.scguns.init.ModCommands;

import java.util.List;

@Mixin(value = ModCommands.class, remap = false)
public class ModCommandsMixin {

    @Inject(
            method = "executeStopAllRaids",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/ribs/scguns/entity/raid/RaidManager;get(Lnet/minecraft/server/level/ServerLevel;)Ltop/ribs/scguns/entity/raid/RaidManager;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private static void afterGetActiveRaids(CommandSourceStack source, CallbackInfoReturnable<Integer> cir, @Local(name = "serverLevel") ServerLevel serverLevel) {
        List<WaveRaidManager> managers = WaveRaidManager.getAll();
        int count = 0;
        for (WaveRaidManager manager : managers) {
            WaveRaidState raid = manager.getCurrentRaidState();
            if (raid != null) {
                raid.endRaid();
                count++;
            }
        }

        if (count > 0) {
            int finalCount = count;
            source.sendSuccess(() -> Component.translatable("commands.scguns.raid.stopped", finalCount), true);
            cir.setReturnValue(1);
            cir.cancel();
        }
    }
}
