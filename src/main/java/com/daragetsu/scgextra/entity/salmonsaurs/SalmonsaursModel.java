package com.daragetsu.scgextra.entity.salmonsaurs;

import com.daragetsu.scgextra.SCGExtra;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class SalmonsaursModel<T extends SalmonsaursEntity> extends HierarchicalModel<SalmonsaursEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("salmonsaurs"), "main");

    private final ModelPart dragon;
	private final ModelPart body;
	private final ModelPart tail;
	private final ModelPart tail2;
	private final ModelPart neck;
	private final ModelPart bone;
	private final ModelPart bone8;
	private final ModelPart backlegs;
	private final ModelPart r_b_l;
	private final ModelPart bone3;
	private final ModelPart bone5;
	private final ModelPart l_b_l;
	private final ModelPart bone2;
	private final ModelPart bone4;

	public SalmonsaursModel(ModelPart root) {
		this.dragon = root.getChild("dragon");
		this.body = this.dragon.getChild("body");
		this.tail = this.body.getChild("tail");
		this.tail2 = this.tail.getChild("tail2");
		this.neck = this.body.getChild("neck");
		this.bone = this.neck.getChild("bone");
		this.bone8 = this.bone.getChild("bone8");
		this.backlegs = this.dragon.getChild("backlegs");
		this.r_b_l = this.backlegs.getChild("r_b_l");
		this.bone3 = this.r_b_l.getChild("bone3");
		this.bone5 = this.bone3.getChild("bone5");
		this.l_b_l = this.backlegs.getChild("l_b_l");
		this.bone2 = this.l_b_l.getChild("bone2");
		this.bone4 = this.bone2.getChild("bone4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition dragon = partdefinition.addOrReplaceChild("dragon", CubeListBuilder.create(), PartPose.offset(0.0F, 2.0F, 0.0F));

		PartDefinition body = dragon.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -14.0F, -26.0F, 18.0F, 21.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 14.0F));

		PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(110, 221).addBox(-5.0F, -0.99F, -2.0F, 10.0F, 11.0F, 24.0F, new CubeDeformation(0.0F))
		.texOffs(17, 117).addBox(0.0F, -0.99F, -2.0F, 0.0F, 21.0F, 53.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -13.0F, 6.0F));

		PartDefinition tail2 = tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(34, 224).addBox(-3.0F, 0.01F, 0.0F, 6.0F, 8.0F, 24.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -1.0F, 22.0F));

		PartDefinition neck = body.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(66, 53).addBox(-7.0F, -22.0F, -10.0F, 14.0F, 32.0F, 14.0F, new CubeDeformation(0.0F))
		.texOffs(173, 0).addBox(-7.0F, -22.0F, -18.0F, 14.0F, 14.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(122, 53).addBox(0.0F, -35.0F, -17.0F, 0.0F, 13.0F, 16.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-1.0F, -35.0F, -17.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, -25.0F));

		PartDefinition bone = neck.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(70, 103).addBox(-5.0F, -4.001F, -11.0F, 10.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
		.texOffs(122, 82).addBox(-7.0F, 3.0F, -5.99F, 14.0F, 6.0F, 6.0F, new CubeDeformation(0.01F))
		.texOffs(54, 103).addBox(7.0F, 9.0F, -6.0F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(112, 131).addBox(-7.0F, 9.0F, -6.0F, 0.0F, 9.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -17.0F, -18.0F));

		PartDefinition cube_r1 = bone.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(132, 131).addBox(2.0F, 6.999F, -10.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(128, 94).addBox(-4.0F, 6.999F, -10.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(192, 175).addBox(-5.0F, -0.001F, -10.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, -4.0F, -11.0F, 0.48F, 0.0F, 0.0F));

		PartDefinition bone8 = bone.addOrReplaceChild("bone8", CubeListBuilder.create().texOffs(35, 123).addBox(-6.0F, 0.0F, -9.0F, 12.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(54, 118).addBox(5.01F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(124, 131).addBox(-5.01F, -2.0F, -8.0F, 0.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(112, 124).addBox(5.0F, -3.0F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(122, 94).addBox(-6.0F, -3.0F, -3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, -6.0F));

		PartDefinition backlegs = dragon.addOrReplaceChild("backlegs", CubeListBuilder.create(), PartPose.offset(0.0F, -4.0F, 11.0F));

		PartDefinition r_b_l = backlegs.addOrReplaceChild("r_b_l", CubeListBuilder.create().texOffs(212, 88).addBox(-2.0F, -4.0F, -3.99F, 8.0F, 13.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, -3.0F, -3.0F));

		PartDefinition bone3 = r_b_l.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(119, 100).addBox(-4.0F, 1.0F, -4.99F, 7.0F, 15.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(218, 40).addBox(-5.0F, 16.0F, -5.99F, 9.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(94, 124).addBox(-5.0F, 18.0F, -12.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(76, 124).addBox(1.0F, 18.0F, -12.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 8.0F, 6.0F));

		PartDefinition bone5 = bone3.addOrReplaceChild("bone5", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, -4.0F));

		PartDefinition l_b_l = backlegs.addOrReplaceChild("l_b_l", CubeListBuilder.create().texOffs(14, 152).addBox(-6.0F, -4.0F, -3.99F, 8.0F, 13.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, -3.0F, -3.0F));

		PartDefinition bone2 = l_b_l.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(2, 104).addBox(-3.0F, 1.0F, -4.99F, 7.0F, 15.0F, 9.0F, new CubeDeformation(0.0F))
		.texOffs(218, 241).addBox(-4.0F, 16.0F, -5.99F, 9.0F, 5.0F, 10.0F, new CubeDeformation(0.0F))
		.texOffs(36, 103).addBox(2.0F, 18.0F, -12.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(36, 112).addBox(-4.0F, 18.0F, -12.0F, 3.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 8.0F, 6.0F));

		PartDefinition bone4 = bone2.addOrReplaceChild("bone4", CubeListBuilder.create(), PartPose.offset(0.0F, 20.0F, -4.0F));

		return LayerDefinition.create(meshdefinition, 256, 256);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		dragon.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

    @Override
    public ModelPart root() {
        return this.dragon;
    }

    @Override
    public void setupAnim(SalmonsaursEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks,
            float pNetHeadYaw, float pHeadPitch) {
    }
    
}
