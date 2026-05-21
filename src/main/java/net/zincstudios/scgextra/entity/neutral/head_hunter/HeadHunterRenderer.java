package net.zincstudios.scgextra.entity.neutral.head_hunter;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class HeadHunterRenderer extends GeoEntityRenderer<HeadHunterEntity> {
    private static final float HEAD_SWORD_TX = -0.04F;
    private static final float HEAD_SWORD_TY = -0.08F;
    private static final float HEAD_SWORD_TZ = 0.35F;
    private static final float HEAD_SWORD_RX = 0.0F;
    private static final float HEAD_SWORD_RY = -90.0F;
    private static final float HEAD_SWORD_RZ = -45.0F;
    private static final float HEAD_SWORD_SCALE = 0.9F;

    private static final float LEFT_WEAPON_TX = 0.0F;
    private static final float LEFT_WEAPON_TY = 0.0F;
    private static final float LEFT_WEAPON_TZ = -0.35F;
    private static final float LEFT_WEAPON_RX = 270.0F;
    private static final float LEFT_WEAPON_RY = 90.0F;
    private static final float LEFT_WEAPON_RZ = 45.0F;
    private static final float LEFT_WEAPON_SCALE = 0.9F;

    public HeadHunterRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("neutral/head_hunter"), false));
        this.shadowRadius = 0.65F;

        addRenderLayer(new BlockAndItemGeoLayer<>(this) {
            @Override
            protected ItemStack getStackForBone(GeoBone bone, HeadHunterEntity animatable) {
                String boneName = bone.getName();

                if ("head_sword".equals(boneName) || "left_weapon".equals(boneName)) {
                    return new ItemStack(Items.STONE_SWORD);
                }

                return null;
            }

            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, HeadHunterEntity animatable) {
                return ItemDisplayContext.NONE;
            }

            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, HeadHunterEntity animatable,
                                              MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                String boneName = bone.getName();

                if ("head_sword".equals(boneName)) {
                    poseStack.pushPose();
                    poseStack.translate(HEAD_SWORD_TX, HEAD_SWORD_TY, HEAD_SWORD_TZ);
                    poseStack.mulPose(Axis.XP.rotationDegrees(HEAD_SWORD_RX));
                    poseStack.mulPose(Axis.YP.rotationDegrees(HEAD_SWORD_RY));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(HEAD_SWORD_RZ));
                    poseStack.scale(HEAD_SWORD_SCALE, HEAD_SWORD_SCALE, HEAD_SWORD_SCALE);
                    super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                    poseStack.popPose();
                    return;
                }

                if ("left_weapon".equals(boneName)) {
                    poseStack.pushPose();
                    poseStack.translate(LEFT_WEAPON_TX, LEFT_WEAPON_TY, LEFT_WEAPON_TZ);
                    poseStack.mulPose(Axis.XP.rotationDegrees(LEFT_WEAPON_RX));
                    poseStack.mulPose(Axis.YP.rotationDegrees(LEFT_WEAPON_RY));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(LEFT_WEAPON_RZ));
                    poseStack.scale(LEFT_WEAPON_SCALE, LEFT_WEAPON_SCALE, LEFT_WEAPON_SCALE);
                    super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
                    poseStack.popPose();
                    return;
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    protected float getDeathMaxRotation(HeadHunterEntity animatable) {
        return 0.0F;
    }
}
