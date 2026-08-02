package net.zincstudios.scgextra.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.block.wreckerturret.WreckerTurretBlockEntity;
import net.zincstudios.scgextra.entity.turret.TurretSeatEntity;

public final class TurretTriggerMessage extends PlayMessage<TurretTriggerMessage> {

    private boolean held;

    public TurretTriggerMessage(boolean held) {
        this.held = held;
    }

    @SuppressWarnings("unused")  // Empty constructor needed by framework for some reason
    public TurretTriggerMessage() {
    }

    public void encode(TurretTriggerMessage msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.held);
    }

    public TurretTriggerMessage decode(FriendlyByteBuf buf) {
        return new TurretTriggerMessage(buf.readBoolean());
    }

    public void handle(TurretTriggerMessage msg, MessageContext ctx) {
        ctx.execute(() -> {
            ServerPlayer player = ctx.getPlayer();
            SCGExtra.LOGGER.debug("message handled");
            if (player == null || !(player.getVehicle() instanceof TurretSeatEntity seat)) {
                return;
            }
            if (player.level().getBlockEntity(seat.getTurretPos()) instanceof WreckerTurretBlockEntity turret) {
                turret.setTriggerHeld(player, msg.held);
            }
        });
        ctx.setHandled(true);
    }
}
