package net.zincstudios.scgextra.entity.whaler.fishfolk;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.entity.common.TextureVarEntityGeoModel;
import net.minecraft.world.item.Items;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import top.ribs.scguns.init.ModItems;

public class FishFolkModel<T extends FishFolkEntity> extends TextureVarEntityGeoModel<T> {

	public FishFolkModel() {
        super(SCGExtra.asResource("whaler/fish_folk"));
	}

    // TODO: redo by controller and re-animation
	@Override
	public void setCustomAnimations(T animatable, long instanceId,
			AnimationState<T> animationState) {
		super.setCustomAnimations(animatable, instanceId, animationState);
		CoreGeoBone rightArm = this.getAnimationProcessor().getBone("right_arm");
		CoreGeoBone leftArm = this.getAnimationProcessor().getBone("left_arm");
		if(!animatable.getMainHandItem().is(Items.TRIDENT)){
			if(animatable.getMainHandItem().is(ModItems.HYPERBARIA.get()) || animatable.getMainHandItem().is(ModItems.SEQUOIA.get())){
				if(rightArm != null) {
					rightArm.setRotX((float)Math.toRadians(90));
				}
			}else if(animatable.getMainHandItem().is(ModItems.FLOUNDERGAT.get()) || animatable.getMainHandItem().is(ModItems.SPIRULIDA.get())){
				if(rightArm != null && leftArm != null) {
					leftArm.setRotX((float)Math.toRadians(82));
					leftArm.setRotY((float)Math.toRadians(-37));
					leftArm.setRotZ((float)Math.toRadians(14));
					rightArm.setRotX((float)Math.toRadians(90));
					rightArm.setRotY((float)Math.toRadians(13));
					rightArm.setRotZ((float)Math.toRadians(-7));
					rightArm.setPosZ(3);
				}
			}
		}
	}
}