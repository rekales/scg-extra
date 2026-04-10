package net.zincstudios.scgextra.entity.fac.trench_goblin;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class TrenchGoblinRenderer extends GunnerRenderer<TrenchGoblinEntity> {

    public TrenchGoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("fac/fac_trench_goblin")), true);
        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected ItemStack getStackForBone(GeoBone bone, TrenchGoblinEntity animatable) {
                if ("weapon".equals(bone.getName())) {
                    return animatable.getMainHandItem();
                }
                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, TrenchGoblinEntity animatable) {
                return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, TrenchGoblinEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if ("weapon".equals(bone.getName())) {
                    poseStack.translate(0.0D, -0.22D, -0.06D);
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                }
                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}
