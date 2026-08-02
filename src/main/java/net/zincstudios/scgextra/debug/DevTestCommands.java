package net.zincstudios.scgextra.debug;

import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;

// TODO: needs to be cleaned up at prod
public class DevTestCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("scgextra")
                .then(Commands.literal("devtest")
                        .requires(css-> css.hasPermission(2))
                )
        );
    }
}
