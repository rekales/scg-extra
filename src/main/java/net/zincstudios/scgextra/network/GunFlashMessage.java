package net.zincstudios.scgextra.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.zincstudios.scgextra.entity.common.client.GunFlashHandler;

/**
 * Enchanted flash variant is just checked clientside instead
 * gunIndex == 0 is default and also means gunFlash from handheld gun
 * <br/>
 * I miss using record based packets
 */
public class GunFlashMessage extends PlayMessage<GunFlashMessage> {

    private int entityId;
    private int gunIndex;

    @SuppressWarnings("unused")  // Empty constructor needed by framework for some reason
    public GunFlashMessage() {
    }

    public GunFlashMessage(int entityId, int gunIndex) {
        this.entityId = entityId;
        this.gunIndex = gunIndex;
    }

    @Override
    public void encode(GunFlashMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.gunIndex);
    }

    @Override
    public GunFlashMessage decode(FriendlyByteBuf buf) {
        return new GunFlashMessage(buf.readInt(), buf.readInt());
    }

    @Override
    public void handle(GunFlashMessage message, MessageContext ctx) {
        ctx.execute(() -> GunFlashHandler.addToFlash(message.entityId, message.gunIndex));
        ctx.setHandled(true);
    }

}
