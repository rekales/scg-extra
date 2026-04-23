package net.zincstudios.scgextra.entity.asgharian;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;

public class HeldGunGeoLayer<T extends Mob & GeoAnimatable> extends HeldItemGeoLayer<T> {

    private final float gunTilt;

    public HeldGunGeoLayer(GeoRenderer<T> renderer, float gunTilt) {
        super(renderer);
        this.gunTilt = gunTilt;
    }

    public HeldGunGeoLayer(GeoRenderer<T> renderer) {
        this(renderer, 0);
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-90 + this.gunTilt));
        Minecraft.getInstance().getItemRenderer().renderStatic(animatable, stack,
                getTransformTypeForStack(bone, stack, animatable), false, poseStack, bufferSource, animatable.level(),
                packedLight, packedOverlay, animatable.getId());
        poseStack.popPose();
    }

}