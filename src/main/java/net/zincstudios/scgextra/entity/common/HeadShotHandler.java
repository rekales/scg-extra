package net.zincstudios.scgextra.entity.common;

import net.minecraft.world.damagesource.DamageSource;

/**
 * For adding additional effects when the entity gets headshot
 */
@SuppressWarnings("UnusedReturnValue")
public interface HeadShotHandler {

    boolean headshot(DamageSource source, float amount);
}
