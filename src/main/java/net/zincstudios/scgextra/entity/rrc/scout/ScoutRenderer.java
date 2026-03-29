package net.zincstudios.scgextra.entity.rrc.scout;

import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.client.GunnerRenderer;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ScoutRenderer<T extends ScoutEntity> extends GunnerRenderer<ScoutEntity>{
    public ScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(SCGExtra.asResource("rrc/scout")), true);
        addRenderLayer(new BlockAndItemGeoLayer<>(this){
            @Override
            protected ItemStack getStackForBone(GeoBone bone, ScoutEntity animatable) {
                if (bone.getName().equals("RightArm")) {
                    return animatable.getMainHandItem();
                }
                return null;
            }
            @Override
            protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, ScoutEntity animatable) {
                    if (bone.getName().equals("RightArm")) {
                        return ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
                    }
                return ItemDisplayContext.NONE;
            }
            @Override
            protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, ScoutEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
                if (bone.getName().equals("RightArm")) {
                    if(animatable.isAiming()){
                        poseStack.translate(0.0, -0.1, 0.2);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-145));
                    }else{
                        poseStack.translate(-0.1, -0.4, 0.0);
                        poseStack.mulPose(Axis.XP.rotationDegrees(-45));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }
}