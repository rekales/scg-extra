package com.daragetsu.scgextra.entity.fishfolk;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class FishFolkModel<T extends FishFolkEntity> extends HumanoidModel<FishFolkEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("fish_folk"), "main");

    private final ModelPart root;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart r_fin;
	private final ModelPart l_fin;
	private final ModelPart left_arm;
	private final ModelPart right_arm;
	private final ModelPart left_leg;
	private final ModelPart right_leg;

	public FishFolkModel(ModelPart root) {
		super(root);
		this.root = root;
		this.body = root.getChild("body");
		this.head = root.getChild("head");
		this.hat = root.getChild("hat");
		this.r_fin = this.head.getChild("r_fin");
		this.l_fin = this.head.getChild("l_fin");
		this.left_arm = root.getChild("left_arm");
		this.right_arm = root.getChild("right_arm");
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 0.0F, 0.0F));
		
		PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
		
		PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(89, 111).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.1F))
		.texOffs(78, 58).addBox(-2.0F, -10.0F, -5.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(91, 36).addBox(0.0F, -16.0F, -4.0F, 0.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition r_fin = head.addOrReplaceChild("r_fin", CubeListBuilder.create().texOffs(99, 0).addBox(-5.0F, -2.0F, -1.0F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, -2.0F, 0.0F));

		PartDefinition l_fin = head.addOrReplaceChild("l_fin", CubeListBuilder.create().texOffs(50, 104).addBox(0.0F, -2.0F, -1.0F, 5.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, -2.0F, 0.0F));

		PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(40, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, 2.0F, 0.0F));

		PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(48, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(FishFolkEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}
	
	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
		float red, float green, float blue, float alpha) {
			root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}