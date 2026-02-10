package com.daragetsu.scgextra.entity.guardian_statue;

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
public class GuardianStatueModel<T extends Entity> extends EntityModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("guardian_statue"), "main");

    private final ModelPart body;
    private final ModelPart bone;
    private final ModelPart eye;

    public GuardianStatueModel(ModelPart root) {
        this.body = root.getChild("body");
        this.bone = this.body.getChild("bone");
        this.eye = this.bone.getChild("eye");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-24.0F, 20.0F, -24.0F, 48.0F, 32.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(108, 80).addBox(-13.0F, -8.0F, -8.0F, 26.0F, 28.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(82, 124).addBox(-15.0F, 16.0F, -10.0F, 30.0F, 4.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(0, 80).addBox(-17.0F, -30.0F, -11.0F, 34.0F, 22.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(192, 0).addBox(-13.0F, -15.0F, -13.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(192, 5).addBox(10.0F, -15.0F, -13.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 174).addBox(-26.0F, -30.0F, -6.0F, 9.0F, 13.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(142, 148).addBox(-28.0F, -30.0F, -6.0F, 11.0F, 15.0F, 13.0F, new CubeDeformation(0.1F))
                .texOffs(0, 164).addBox(17.0F, -30.0F, -6.0F, 11.0F, 15.0F, 13.0F, new CubeDeformation(0.1F))
                .texOffs(82, 148).addBox(17.0F, -30.0F, -6.0F, 17.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -28.0F, 0.0F));

        PartDefinition bone = body.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(182, 124).addBox(6.0F, 27.0F, -15.0F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(154, 189).addBox(-8.0F, 27.0F, -15.0F, 2.0F, 8.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(0, 122).addBox(-11.0F, 12.0F, -8.0F, 22.0F, 23.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(92, 174).addBox(-1.0F, -2.0F, -4.0F, 2.0F, 15.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(190, 139).addBox(-1.0F, 6.0F, 10.0F, 2.0F, 7.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(124, 176).addBox(-1.0F, 13.0F, 10.0F, 2.0F, 14.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(180, 176).addBox(-1.0F, 27.0F, 17.0F, 2.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(190, 169).addBox(-15.0F, 23.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(172, 191).addBox(-15.0F, 23.0F, 6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 192).addBox(11.0F, 23.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(184, 191).addBox(11.0F, 23.0F, 6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(154, 176).addBox(-6.0F, 15.5F, -9.0F, 12.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -65.0F, 0.0F));

        PartDefinition spine12_rotation_r1 = bone.addOrReplaceChild("spine12_rotation_r1", CubeListBuilder.create().texOffs(190, 153).addBox(-1.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(190, 165).addBox(-1.0F, -2.0F, -13.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 33.0F, 7.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition spine4_rotation_r1 = bone.addOrReplaceChild("spine4_rotation_r1", CubeListBuilder.create().texOffs(190, 157).addBox(-1.0F, 0.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(190, 161).addBox(-1.0F, 0.0F, -13.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, 13.0F, 7.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition spine12_rotation_r2 = bone.addOrReplaceChild("spine12_rotation_r2", CubeListBuilder.create().texOffs(62, 168).addBox(-4.0F, -2.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(62, 164).addBox(-4.0F, -2.0F, -13.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 33.0F, 7.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition spine4_rotation_r2 = bone.addOrReplaceChild("spine4_rotation_r2", CubeListBuilder.create().texOffs(48, 168).addBox(-4.0F, 0.0F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 164).addBox(-4.0F, 0.0F, 11.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, 13.0F, -5.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition eye = bone.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(182, 139).addBox(-1.5F, 2.5F, 0.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 17.5F, -9.25F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}