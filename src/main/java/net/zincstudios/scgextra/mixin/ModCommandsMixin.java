package net.zincstudios.scgextra.mixin;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.zincstudios.scgextra.raid.WaveRaidManager;
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
    private static void afterExecuteStopAllRaids(CommandSourceStack source, CallbackInfoReturnable<Integer> cir) {
        List<WaveRaidManager> managers = WaveRaidManager.getAll();
        int count = 0;
        for (WaveRaidManager manager : managers) {
            if (manager.hasActiveRaid()) {
                manager.endRaid(source.getLevel(), false);
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
