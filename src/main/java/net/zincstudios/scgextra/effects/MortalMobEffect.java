package net.zincstudios.scgextra.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.item.armor.ArmorSet;
import net.zincstudios.scgextra.item.armor.ArmorSets;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MortalMobEffect extends MobEffect {

    public MortalMobEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (ArmorSet.getArmorSet(livingEntity) != ArmorSets.RITUAL) {
            livingEntity.removeEffect(ModEffects.MORTAL.get());
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }
}
