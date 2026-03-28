package net.zincstudios.scgextra.entity.common.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import javax.annotation.Nullable;

// Integrating bone checks and caching for performance
// Edit in case there's dual wielding mobs, or an alternate geo layer

/**
 * For integrating bone checks and caching for performance.
 * Renders the held gun or left_hand or right_hand bones.
 * <p>
 * Edit in case there's dual wielding mobs, or an alternate geo layer
 *
 * @param <T>
 */
public class GunGeoLayer<T extends GunnerEntity & GeoAnimatable> extends BlockAndItemGeoLayer<T> {

    private GeoBone cachedBone;
    private GeoBone rightHandBone;
    private final float gunTilt;

    public GunGeoLayer(GeoRenderer<T> renderer, float gunTilt) {
        super(renderer);
        this.gunTilt = gunTilt;
    }

    public GunGeoLayer(GeoRenderer<T> renderer) {
        this(renderer, 0);
    }

    @Override
    protected @Nullable ItemStack getStackForBone(GeoBone bone, T animatable) {
        if (animatable.isLeftHanded() && this.cachedBone == null && bone.getName().equals("left_hand"))
            this.cachedBone = bone;
        if (!animatable.isLeftHanded() && this.cachedBone == null && bone.getName().equals("right_hand"))
            this.cachedBone = bone;

        if (bone == this.cachedBone) {
            return animatable.getMainHandItem();
        }
        return null;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, T animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-90 + this.gunTilt));
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, T animatable) {
        return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
    }
}
