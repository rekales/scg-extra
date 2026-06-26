package net.zincstudios.scgextra.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// Not sure why MobEffect constructor is protected, this just turns it public
public class AttributeMobEffect extends MobEffect {

    public AttributeMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
