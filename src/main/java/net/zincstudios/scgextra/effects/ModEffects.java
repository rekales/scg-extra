package net.zincstudios.scgextra.effects;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.zincstudios.scgextra.SCGExtra;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.zincstudios.scgextra.attributes.SCGEAttributes;
import top.ribs.scguns.attributes.SCAttributes;

public final class ModEffects {
    private static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, SCGExtra.MOD_ID);

    public static final RegistryObject<InkEffect> INK_EFFECT = MOB_EFFECTS.register("ink_effect", () -> new InkEffect(MobEffectCategory.HARMFUL, 0xFF0000));
    public static final RegistryObject<GlowingInkEffect> GLOWING_INK_EFFECT = MOB_EFFECTS.register("glowing_ink_effect", () -> new GlowingInkEffect(MobEffectCategory.HARMFUL, 0xFF0000));
    
    public static final RegistryObject<WhalerRegenEffect> WHALER_REGEN_EFFECT = MOB_EFFECTS.register("whaler_regen_effect", () -> new WhalerRegenEffect(MobEffectCategory.BENEFICIAL, 0x00FF00));

    public static final RegistryObject<StunnedEffect> STUNNED_EFFECT = MOB_EFFECTS.register("stunned", () -> new StunnedEffect(MobEffectCategory.HARMFUL, 0x000000));

    public static final RegistryObject<MobEffect> SURVIVOR_MEDAL_EFFECT = MOB_EFFECTS.register("survivor_medal", () -> new AttributeMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR, "C5E68250-B0F7-47A9-810A-5891E0ECA52D", 10, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> IRON_WILL_MEDAL_EFFECT = MOB_EFFECTS.register("iron_will_medal", () -> new AttributeMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(SCGEAttributes.BULLET_DAMAGE_TAKEN_MULT.get(), "F76E3688-487E-4E90-AFD7-00241D06E3F2", -0.2, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<MobEffect> DEFIANCE_MEDAL_EFFECT = MOB_EFFECTS.register("defiance_medal", () -> new AttributeMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "AA1F4B52-6840-46D5-ABD5-2D887904434C", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL)
            .addAttributeModifier(SCAttributes.RELOAD_SPEED.get(), "F545DF93-4857-4B2F-8769-82DCBCC5B372", 0.2, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> OBEDIENCE_MEDAL_EFFECT = MOB_EFFECTS.register("obedience_medal", () -> new AttributeMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "8EF994CC-7346-4756-926C-3D7ADF093ABD", 3, AttributeModifier.Operation.ADDITION)
            .addAttributeModifier(SCGEAttributes.RECOIL_MULT.get(), "E433E6CF-8E83-4798-AE32-037789D3EB19", -0.3, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> CRUELTY_MEDAL_EFFECT = MOB_EFFECTS.register("cruelty_medal", () -> new AttributeMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(SCAttributes.BULLET_DAMAGE_MULTIPLIER.get(), "5A5737FF-A94F-459E-B578-A7163904104E", 0.1, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<MobEffect> ENLIGHTENMENT_MEDAL_EFFECT = MOB_EFFECTS.register("enlightenment_medal", () -> new EnlightenmentHealthBoostMobEffect(MobEffectCategory.BENEFICIAL, 0x000000)
            .addAttributeModifier(Attributes.MAX_HEALTH, EnlightenmentHealthBoostMobEffect.EFFECT_ID, 4.0D, AttributeModifier.Operation.ADDITION));

//    public static final RegistryObject<MobEffect> CONQUEROR_MEDAL_EFFECT = MOB_EFFECTS.register("survivor_medal", () -> new MedalAttributeEffect(MobEffectCategory.BENEFICIAL, 0x000000)
//            .addAttributeModifier(Attributes.ARMOR, "C5E68250-B0F7-47A9-810A-5891E0ECA52D", 10, AttributeModifier.Operation.ADDITION));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
