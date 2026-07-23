package net.zincstudios.scgextra.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.zincstudios.scgextra.SCGExtra;

public final class ModNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            SCGExtra.asResource("main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private ModNetwork() {}

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, TurretTriggerC2S.class,
                TurretTriggerC2S::encode, TurretTriggerC2S::decode, TurretTriggerC2S::handle);
    }
}
