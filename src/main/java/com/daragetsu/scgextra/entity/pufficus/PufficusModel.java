package com.daragetsu.scgextra.entity.pufficus;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

// TODO: replace with geomodel
public class PufficusModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("pufficus"), "main");

    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart right_leg;
    private final ModelPart left_arm;
    private final ModelPart left_leg;
    private final ModelPart bone2;

    public PufficusModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.right_arm = this.bone.getChild("right_arm");
        this.right_leg = this.bone.getChild("right_leg");
        this.left_arm = this.bone.getChild("left_arm");
        this.left_leg = this.bone.getChild("left_leg");
        this.bone2 = this.bone.getChild("bone2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 18).addBox(-10.0F, -29.0F, -1.0F, 20.0F, 20.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-10.0F, -25.0F, -9.0F, 20.0F, 16.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-10.0F, -11.0F, -9.0F, 20.0F, 2.0F, 16.0F, new CubeDeformation(0.1F))
                .texOffs(0, 0).addBox(7.0F, -34.0F, 4.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 121).addBox(-9.0F, -34.11F, 4.0F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(76, 102).addBox(-10.0F, -29.01F, -1.0F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 23.0F, 0.0F));

        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(56, 18).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(78, 81).addBox(-2.0F, -14.0F, -6.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(30, 70).addBox(-1.0F, -15.0F, -5.0F, 2.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(78, 56).addBox(0.0F, -20.0F, -5.0F, 0.0F, 5.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(78, 43).addBox(-6.0F, -10.0F, -2.0F, 12.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(78, 49).addBox(-6.0F, -12.0F, -5.0F, 12.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 70).addBox(5.0F, -8.0F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(30, 81).addBox(-6.0F, -8.0F, -2.0F, 1.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(48, 70).addBox(-6.0F, -7.0F, -5.0F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 79).addBox(5.0F, -7.0F, -5.0F, 1.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 34).addBox(-5.0F, -11.0F, -2.0F, 10.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(78, 87).addBox(-2.0F, -6.0F, -5.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -28.0F, 0.0F));

        PartDefinition spikes_front_bottom_r1 = body.addOrReplaceChild("spikes_front_bottom_r1", CubeListBuilder.create().texOffs(78, 68).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -4.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition right_arm = bone.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(56, 63).addBox(-5.0F, -2.0F, -3.0F, 5.0F, 23.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(46, 113).addBox(-5.0F, -2.0F, -3.0F, 5.0F, 9.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(-10.0F, -27.0F, 3.0F));

        PartDefinition cube_r1 = right_arm.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(26, 117).addBox(0.0F, -5.0F, -4.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1F, -2.11F, 1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition right_leg = bone.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 70).addBox(-2.0F, 0.0F, -5.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-7.0F, -9.0F, 1.0F));

        PartDefinition left_arm = bone.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(56, 34).addBox(0.0F, -2.0F, -3.0F, 5.0F, 23.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(106, 18).addBox(0.0F, 14.0F, -3.0F, 5.0F, 2.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(10.0F, -27.0F, 3.0F));

        PartDefinition cube_r2 = left_arm.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(118, 81).addBox(0.0F, -5.0F, -3.0F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition left_leg = bone.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(72, 0).addBox(-5.0F, 0.0F, -5.0F, 7.0F, 10.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -9.0F, 1.0F));

        PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 98).addBox(-5.0F, 0.0F, -0.1F, 10.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.9F, -9.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}