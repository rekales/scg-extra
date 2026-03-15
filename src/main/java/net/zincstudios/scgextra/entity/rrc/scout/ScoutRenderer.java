package net.zincstudios.scgextra.entity.rrc.scout;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;
import top.ribs.scguns.init.ModItems;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class ScoutRenderer extends GeoEntityRenderer<ScoutEntity>{
    public ScoutRenderer(EntityRendererProvider.Context context) {
        super(context, new ScoutModel());
        this.shadowRadius = 0.5F;
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
                    bone.setRotX(-80);
                    poseStack.translate(0.05, 0, 0.5);
                    if(animatable.getMainHandItem().is(ModItems.HULLBREAKER.get())){
                        poseStack.mulPose(Axis.XP.rotationDegrees(90));
                    }else{
                        poseStack.mulPose(Axis.XP.rotationDegrees(175));
                    }
                }

                super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(ScoutEntity pEntity) {
        return SCGExtra.asResource("textures/entity/rrc/scout.png");
    }
}