package net.zincstudios.scgextra.entity.raid_summoner;

import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.zincstudios.scgextra.SCGExtra;

public class RaidSummonerRenderer extends MobRenderer<RaidSummonerEntity, RaidSummonerModel>{

    public RaidSummonerRenderer(Context pContext) {
        super(pContext, new RaidSummonerModel(pContext.bakeLayer(RaidSummonerModel.LAYER_LOCATION)), 0.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(RaidSummonerEntity pEntity) {
        return SCGExtra.asResource("textures/entity/net_entity/net_entity.png");//net entity texture as dummy, doesn't matter, will only summon for one tick
    }
    
}