package net.zincstudios.scgextra.entity.common.raid_summoner;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.zincstudios.scgextra.SCGExtra;

public class RaidSummonerModel extends EntityModel<RaidSummonerEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(SCGExtra.asResource("net_entity"), "main");//net entity texture as dummy, doesn't matter, will only summon for one tick

    public RaidSummonerModel(ModelPart root){
    }

    @Override
    public void setupAnim(RaidSummonerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks,
            float pNetHeadYaw, float pHeadPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay,
            float pRed, float pGreen, float pBlue, float pAlpha) {
    }
}
