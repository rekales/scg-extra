package net.zincstudios.scgextra.item.curios;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Quaternionf;

public class MedKitRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource, int light, float limbSwing,float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,float headPitch) {
        matrixStack.pushPose();

        if (slotContext.index() == 0) {
            matrixStack.translate(-0.18D, 0.65D, 0.17D);
            matrixStack.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(180)));
            matrixStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(90)));
        } else if (slotContext.index() == 1) {
            matrixStack.translate(0.18D, 0.65D, 0.17D);
            matrixStack.mulPose(new Quaternionf().rotationZ((float) Math.toRadians(180)));
            matrixStack.mulPose(new Quaternionf().rotationX((float) Math.toRadians(90)));
        }

        matrixStack.scale(0.8f, 0.8f, 0.8f);

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);

        itemRenderer.render(stack, ItemDisplayContext.GROUND, false, matrixStack, bufferSource, light, OverlayTexture.NO_OVERLAY, bakedModel);

        matrixStack.popPose();
    }
}
