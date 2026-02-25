package com.daragetsu.scgextra.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.VariantHolder;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.HashMap;
import java.util.Map;

// Uses a simple "_*" integer appended variant
// NOTE: will cause texture errors if provided invalid number
@SuppressWarnings("unused")
public class TextureVarEntityGeoModel<T extends GeoAnimatable & VariantHolder<Integer>> extends DefaultedEntityGeoModel<T> {

    private final ResourceLocation assetSubpath;
    private final Map<Integer, ResourceLocation> texturePathCache = new HashMap<>();

    public TextureVarEntityGeoModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
        this.assetSubpath = assetSubpath;
    }

    public TextureVarEntityGeoModel(ResourceLocation assetSubpath, boolean turnsHead) {
        super(assetSubpath, turnsHead);
        this.assetSubpath = assetSubpath;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        ResourceLocation resLoc = texturePathCache.get(animatable.getVariant());
        if (resLoc == null) {
            resLoc = ResourceLocation.fromNamespaceAndPath(assetSubpath.getNamespace(),
                    "textures/" + subtype() + "/" + assetSubpath.getPath() + "_" + animatable.getVariant() + ".png");
            texturePathCache.put(animatable.getVariant(), resLoc);
        }
        return resLoc;
    }
}