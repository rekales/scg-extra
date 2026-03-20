package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.zincstudios.scgextra.entity.common.ai.StunnedGoal;
import org.jetbrains.annotations.Nullable;
import top.ribs.scguns.init.ModEffects;


// I'll coin this "behaviour augmentation pattern" since this idea seems to be unique.
/**
 * Interface to be paired with the use of StunnedGoal.
 * Feel free to override the default methods if custom functionality is needed.
 * @see StunnedGoal
 */
@SuppressWarnings("unused")
public interface Stunnable {

    /**
     * Provide the StunnedGoal here by returning it, it will be used by the default methods for handling logic.
     * Provide it by however you like, saving as a variable or maybe filtering through the behaviour goals.
     * <p>
     * Nullable handling added if for some fucking reason it gets a null object.
     */
    @Nullable StunnedGoal<?> getStunnedGoal();

    /**
     * @return the duration of the stun when called by the checks.
     * Override as you see fit.
     */
    default int getDefaultStunDuration() {
        return 60;  // TODO: configs
    }

    default boolean isStunned() {
        StunnedGoal<?> stunnedGoal = this.getStunnedGoal();
        return stunnedGoal != null && stunnedGoal.getStunTicksLeft() > 0;
    }

    /**
     * To be called on the mob's hurt() method to check if the damage will cause a stun. Stuns the mob if so.
     * @return if damage should be applied.
     */
    default boolean handleHurtStun(DamageSource source, float amount) {
        // TODO: shock cells and headshot checks.
        return true;
    }

    /**
     * To be called on the mob's addEffect() method to check if the added effects will cause a stun. Stuns the mob if so.
     * maybe invoked after super so it does it's initial check first.
     * @return if the effect should be applied.
     */
    default boolean handleAddEffectStun(MobEffectInstance effectInstance, @Nullable Entity entity) {
        if (effectInstance.getEffect() == ModEffects.BLINDED.get()
                || effectInstance.getEffect() == ModEffects.DEAFENED.get()) {
            stun(getDefaultStunDuration());
        }
        return true;
    }

    /**
     * Invoked when the entity gets headshotted. Called before the hurt method.
     * @see top.ribs.scguns.entity.projectile.ProjectileEntity
     */
    default void handleHeadshot(DamageSource source, float amount) {
        StunnedGoal<?> stunnedGoal = this.getStunnedGoal();
        if (stunnedGoal != null) {
            stunnedGoal.handleHeadshot(source, amount);
        }
    }

    /**
     * The method to be called for stunning the mob. Normally called by checks but can also be invoked manually.
     */
    default void stun(int stunTicks) {
        // TODO: configs if can stun
        StunnedGoal<?> stunnedGoal = this.getStunnedGoal();
        if (stunnedGoal == null) return;
        stunnedGoal.stun(stunTicks);
    }
}
