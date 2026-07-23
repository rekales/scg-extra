package net.zincstudios.scgextra.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.zincstudios.scgextra.block.WreckerTurretBlockEntity;
import net.zincstudios.scgextra.entity.turret.TurretSeatEntity;

import java.util.function.Supplier;

public final class TurretTriggerC2S {

    private final boolean held;

    public TurretTriggerC2S(boolean held) {
        this.held = held;
    }

    public static void encode(TurretTriggerC2S msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.held);
    }

    public static TurretTriggerC2S decode(FriendlyByteBuf buf) {
        return new TurretTriggerC2S(buf.readBoolean());
    }

    public static void handle(TurretTriggerC2S msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !(player.getVehicle() instanceof TurretSeatEntity seat)) {
                return;
            }
            if (player.level().getBlockEntity(seat.getTurretPos()) instanceof WreckerTurretBlockEntity turret) {
                turret.setTriggerHeld(player, msg.held);
            }
        });
        context.setPacketHandled(true);
    }
}
