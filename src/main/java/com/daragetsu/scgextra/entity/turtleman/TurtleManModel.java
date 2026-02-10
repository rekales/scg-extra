package com.daragetsu.scgextra.entity.turtleman;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

// TODO: convert to geomodel
public class TurtleManModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("turtleman"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_leg;
    private final ModelPart left_leg;
    private final ModelPart tail;

    public TurtleManModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_leg = this.body.getChild("right_leg");
        this.left_leg = this.body.getChild("left_leg");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 35).addBox(-9.0F, -2.0F, -3.0F, 18.0F, 22.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(58, 0).addBox(-7.0F, -1.0F, -5.0F, 14.0F, 20.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-10.0F, -5.0F, 2.0F, 20.0F, 26.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(48, 35).addBox(-5.0F, -7.0F, -8.5F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(48, 51).addBox(-5.0F, -7.0F, -8.5F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.1F))
                .texOffs(88, 56).addBox(0.0F, -14.1F, -7.5F, 0.0F, 7.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 1.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 63).addBox(-5.0F, -2.5F, -3.0F, 5.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(66, 67).addBox(-5.0F, -2.5F, -3.0F, 5.0F, 24.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(-9.0F, 2.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(22, 63).addBox(0.0F, -2.5F, -3.0F, 5.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(44, 67).addBox(0.0F, -2.5F, -3.0F, 5.0F, 24.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offset(9.0F, 2.0F, 0.0F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(86, 22).addBox(-2.5F, -1.0F, -3.0F, 6.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 20.0F, 1.0F));

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(86, 39).addBox(-3.5F, -1.0F, -3.0F, 6.0F, 12.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 20.0F, 1.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(58, 22).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 20.0F, 3.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}