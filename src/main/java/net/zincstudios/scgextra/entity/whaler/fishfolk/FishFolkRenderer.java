package net.zincstudios.scgextra.entity.whaler.fishfolk;

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

public class FishFolkRenderer extends GeoEntityRenderer<FishFolkEntity> {

    public FishFolkRenderer(Context context) {
        super(context, new FishFolkModel<>());
        this.shadowRadius = 0.5f;
        addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Override
            protected ItemStack getStackForBone(GeoBone bone, FishFolkEntity animatable) {
                if (bone.getName().equals("right_arm")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }
            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack,
                    FishFolkEntity animatable) {
                if(!animatable.getMainHandItem().is(Items.TRIDENT)){
                    if (bone.getName().equals("right_arm")) {
                        return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    }
                }
                return ItemDisplayContext.NONE;
            }
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack,
                    FishFolkEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight,
                    int packedOverlay) {
                if (bone.getName().equals("right_arm")) {
                    if(animatable.getMainHandItem().is(Items.TRIDENT)){
                        if(!animatable.isPassenger()){
                            poseStack.translate(0.5, 0, -1);
                            poseStack.mulPose(Axis.XP.rotationDegrees(-90));
                        }else{
                            poseStack.translate(0.5, -1, 0);
                            poseStack.mulPose(Axis.XP.rotationDegrees(180));
                        }
                    }else{
                        poseStack.translate(0.05, 0, 0.5);
                        if(animatable.getMainHandItem().is(ModItems.HULLBREAKER.get())){
                            poseStack.mulPose(Axis.XP.rotationDegrees(90));
                        }else{
                            poseStack.mulPose(Axis.XP.rotationDegrees(180));
                            poseStack.mulPose(Axis.YP.rotationDegrees(-15));
                        }
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}