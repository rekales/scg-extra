package net.zincstudios.scgextra.entity.common.client;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class ExpandedAnimationController<T extends GeoAnimatable> extends AnimationController<T> {

    private final Map<String, AnimationSupplier> triggerableAnimSuppliers =
            new Object2ObjectOpenHashMap<>(0);

    public ExpandedAnimationController(T animatable, AnimationController.AnimationStateHandler<T> animationHandler) {
        super(animatable, animationHandler);
    }

    public ExpandedAnimationController(T animatable, String name, AnimationController.AnimationStateHandler<T> animationHandler) {
        super(animatable, name, animationHandler);
    }

    public ExpandedAnimationController(T animatable, int transitionTickTime, AnimationController.AnimationStateHandler<T> animationHandler) {
        super(animatable, transitionTickTime, animationHandler);
    }

    public ExpandedAnimationController(T animatable, String name, int transitionTickTime, AnimationController.AnimationStateHandler<T> animationHandler) {
        super(animatable, name, transitionTickTime, animationHandler);
    }

    public boolean isCurrentAnimation(RawAnimation animation) {
        return Objects.equals(this.currentRawAnimation, animation);
    }

    @Override
    public boolean tryTriggerAnimation(String animName) {
        AnimationSupplier animSupplier = this.triggerableAnimSuppliers.get(animName);

        // I can't get an AnimationState object so the controller will have to do.
        if (animSupplier != null) {
            this.triggeredAnimation = animSupplier.get(this);

            if (this.animationState == State.STOPPED) {
                this.animationState = State.TRANSITIONING;
                this.shouldResetTick = true;
                this.justStartedTransition = true;
            }

            return true;
        }

        return super.tryTriggerAnimation(animName);
    }

    public ExpandedAnimationController<T> triggerableAnim(String name, AnimationSupplier animSupplier) {
        this.triggerableAnimSuppliers.put(name, animSupplier);

        return this;
    }

    @Override
    public ExpandedAnimationController<T> triggerableAnim(String name, RawAnimation animation) {
        return (ExpandedAnimationController<T>) super.triggerableAnim(name, animation);
    }

    public T getAnimatable() {
        return this.animatable;
    }

    @FunctionalInterface
    public interface AnimationSupplier {
        RawAnimation get(ExpandedAnimationController<?> controller);
    }
}