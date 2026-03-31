package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.zincstudios.scgextra.CommonConfig;
import net.zincstudios.scgextra.entity.common.ai.StunnedGoal;
import org.jetbrains.annotations.Nullable;
import top.ribs.scguns.init.ModEffects;

/**
 * Interface to be paired with the use of StunnedGoal.
 * @see StunnedGoal
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface Stunnable {

    /**
     * Will be checked by the StunnedGoal every tick while it's not stunned.
     * @return amount of ticks the entity should be stunned, return 0 or less to not stun.
     */
    int shouldStun();

    /**
     * To be primarily be invoked by the StunnedGoal
     */
    void setStunned(boolean stunned);

    /**
     * Will be invoked by the StunnedGoal while the entity is being stunned.
     * @return true to interrupt the stun
     */
    default boolean updateStunned(int ticksLeft) {
        return false;
    }

    boolean isStunned();
}
