package com.daragetsu.scgextra.effects;

import com.daragetsu.scgextra.SCGExtra;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SCGExtra.MOD_ID);

    public static final RegistryObject<InkEffect> INK_EFFECT = MOB_EFFECTS.register("ink_effect", () -> new InkEffect(MobEffectCategory.HARMFUL, 0xFF0000));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
