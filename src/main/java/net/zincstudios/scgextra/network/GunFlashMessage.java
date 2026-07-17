package net.zincstudios.scgextra.network;

import com.mrcrayfish.framework.api.network.MessageContext;
import com.mrcrayfish.framework.api.network.message.PlayMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
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
    private ResourceLocation flashLoc;
    private boolean enchanted;
    private float scale;

    @SuppressWarnings("unused")  // Empty constructor needed by framework for some reason
    public GunFlashMessage() {
    }

    public GunFlashMessage(int entityId, int gunIndex, ResourceLocation flashLoc, boolean enchanted, float scale) {
        this.entityId = entityId;
        this.gunIndex = gunIndex;
        this.flashLoc = flashLoc;
        this.enchanted = enchanted;
        this.scale = scale;
    }

    public GunFlashMessage(int entityId, int gunIndex, ResourceLocation flashLoc) {
        this(entityId, gunIndex, flashLoc, false, 1F);
    }

    @Override
    public void encode(GunFlashMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeInt(message.gunIndex);
        buf.writeResourceLocation(message.flashLoc);
        buf.writeBoolean(message.enchanted);
        buf.writeFloat(message.scale);
    }

    @Override
    public GunFlashMessage decode(FriendlyByteBuf buf) {
        return new GunFlashMessage(buf.readInt(), buf.readInt(), buf.readResourceLocation(), buf.readBoolean(), buf.readFloat());
    }

    @Override
    public void handle(GunFlashMessage message, MessageContext ctx) {
        ctx.execute(() -> GunFlashHandler.addToFlash(message.entityId,
                new GunFlashHandler.FlashData(message.gunIndex, message.flashLoc, message.enchanted, message.scale)));
        ctx.setHandled(true);
    }

}
