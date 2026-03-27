package net.zincstudios.scgextra.entity.rrc.oppressor;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

// TODO: better class name
public class ExpandedAnimationController<T extends GeoAnimatable> extends AnimationController<T> {

    public ExpandedAnimationController(T animatable, AnimationStateHandler<T> animationHandler) {
        super(animatable, animationHandler);
    }

    public ExpandedAnimationController(T animatable, String name, AnimationStateHandler<T> animationHandler) {
        super(animatable, name, animationHandler);
    }

    public ExpandedAnimationController(T animatable, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
        super(animatable, transitionTickTime, animationHandler);
    }

    public ExpandedAnimationController(T animatable, String name, int transitionTickTime, AnimationStateHandler<T> animationHandler) {
        super(animatable, name, transitionTickTime, animationHandler);
    }

    public void setTriggeredAnimation(RawAnimation anim) {
        super.stopTriggeredAnimation();

        // Copied from AnimationController#tryTriggeredAnimation
        if (anim == null) return;

        this.triggeredAnimation = anim;

        if (this.animationState == State.STOPPED) {
            this.animationState = State.TRANSITIONING;
            this.shouldResetTick = true;
            this.justStartedTransition = true;
        }
    }
}
