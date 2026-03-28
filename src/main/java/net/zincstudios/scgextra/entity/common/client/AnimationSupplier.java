package net.zincstudios.scgextra.entity.common.client;

import software.bernie.geckolib.core.animation.RawAnimation;

@FunctionalInterface
public interface AnimationSupplier {

    RawAnimation get(ExpandedAnimationController<?> controller);
}