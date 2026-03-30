package net.zincstudios.scgextra.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface HurtEffects {

    void hurtEffect(ItemStack stack, LivingEntity target, LivingEntity attacker);
}
