package net.zincstudios.scgextra.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedGeoModel;

public class JuggernautChestplateModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {

    public JuggernautChestplateModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    @Override
    protected String subtype() {
        return "armor";
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        CoreGeoBone cogwheel = getAnimationProcessor().getBone("cogwheel");
        Entity entity = animationState.getData(DataTickets.ENTITY);
        if (cogwheel == null || entity == null) return;

        cogwheel.setRotZ(-entity.tickCount * Mth.DEG_TO_RAD * 0.75F);
    }
}
