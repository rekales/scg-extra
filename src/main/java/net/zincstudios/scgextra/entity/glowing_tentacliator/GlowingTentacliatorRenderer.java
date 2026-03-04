package net.zincstudios.scgextra.entity.glowing_tentacliator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import top.ribs.scguns.init.ModItems;

public class GlowingTentacliatorRenderer extends GeoEntityRenderer<GlowingTentacliatorEntity>{

    public GlowingTentacliatorRenderer(Context context) {
        super(context, new GlowingTentacliatorModel<>());
        this.shadowRadius = 0.5f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Override
            protected ItemStack getStackForBone(GeoBone bone, GlowingTentacliatorEntity animatable) {
                if (bone.getName().equals("left_arm")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }
            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack,
                    GlowingTentacliatorEntity animatable) {
                if(!animatable.getMainHandItem().is(Items.TRIDENT)){
                    if (bone.getName().equals("left_arm")) {
                        return ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
                    }
                }
                return ItemDisplayContext.NONE;
            }
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack,
                    GlowingTentacliatorEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight,
                    int packedOverlay) {
                if (bone.getName().equals("left_arm")) {
                    if(animatable.getMainHandItem().is(Items.TRIDENT)){
                        poseStack.translate(-0.35, -0.2, -1);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                    }else{
                        poseStack.translate(-0.8, -0.1, 0.8);
                        if(animatable.getMainHandItem().is(ModItems.HULLBREAKER.get())){
                            poseStack.mulPose(Axis.XP.rotationDegrees(90));
                        }else{
                            poseStack.mulPose(Axis.XP.rotationDegrees(180));
                        }
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}
