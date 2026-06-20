package net.zincstudios.scgextra.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// Mostly for entity state checks for red outline. Its so that I don't have to write synced data boilerplate
public class StunnedEffect extends MobEffect {

    public StunnedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
