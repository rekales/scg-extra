package net.zincstudios.scgextra.entity.cog.juggernaut;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.common.client.BaseEntityRenderer;
import net.zincstudios.scgextra.entity.common.client.GunFlashHandler;
import net.zincstudios.scgextra.entity.common.client.HeldGunGeoLayer;
import net.zincstudios.scgextra.entity.common.client.MobRenderUtils;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.util.RenderUtils;
import top.ribs.scguns.ScorchedGuns;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.init.ModItems;
import top.ribs.scguns.item.GunItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CogJuggernautRenderer extends BaseEntityRenderer<CogJuggernautEntity> {

    public CogJuggernautRenderer(EntityRendererProvider.Context context, GeoModel<CogJuggernautEntity> model) {
        super(context, model);
        this.addRenderLayer(new HeldGunGeoLayer<>(this, -60));
    }

    @Override
    public void renderRecursively(PoseStack poseStack, CogJuggernautEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        renderMuzzleFlash(poseStack, animatable, bone, renderType, bufferSource);
    }

    private void renderMuzzleFlash(PoseStack poseStack, CogJuggernautEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource) {
        if (!animatable.isLeftHanded() && !bone.getName().equals("right_hand")) return;
        if (animatable.isLeftHanded() && !bone.getName().equals("left_hand")) return;
        if (!GunFlashHandler.hasFlashToRender(animatable.getId(), 0)) return;

        ItemStack stack = animatable.getMainHandItem();
        if (!(stack.getItem() instanceof GunItem gunItem)) return;
        Gun gun = gunItem.getModifiedGun(stack);
        Gun.Display.Flash flash = gun.getDisplay().getFlash();
        if (flash == null) return;

        poseStack.pushPose();

        RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);
        poseStack.mulPose(Axis.XP.rotationDegrees(-80));

        if (gunItem == ModItems.GATTALER.get()) {
            poseStack.translate(-0.5, -0.8, -1.62);
        } else if (gunItem == ModItems.THUNDERHEAD.get()) {
            poseStack.translate(-0.5, -0.8, -1.92);
        } else if (gunItem == ModItems.SPITFIRE.get()) {
            poseStack.translate(-0.5, -0.65, -1.22);
        } else {
            poseStack.popPose();
            return;
        }

        // TODO: redo to match with vulture renderer
        RandomSource rand = RandomSource.create(animatable.level().getGameTime()/2 * animatable.getId());
        rand.nextFloat();  // because it's the same value on first get
        poseStack.translate((rand.nextFloat()-0.5)*0.07, (rand.nextFloat()-0.5)*0.07, 0);

        ResourceLocation flashTexture = ResourceLocation.fromNamespaceAndPath(ScorchedGuns.MODID,
                "textures/effect/" + flash.getTextureLocation() + ".png");
        MobRenderUtils.drawMuzzleFlash(poseStack, renderType, bufferSource, flashTexture, stack.isEnchanted());

        poseStack.popPose();
    }
}
