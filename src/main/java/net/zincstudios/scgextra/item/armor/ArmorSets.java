package net.zincstudios.scgextra.item.armor;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.zincstudios.scgextra.attributes.SCGEAttributes;
import top.ribs.scguns.init.ModEffects;

public final class ArmorSets {

    public static final ArmorSet OPPRESSOR = new ArmorSet(new ArmorSet.Traits()
            .modifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier("oppressor_set_knockback_res", 1, AttributeModifier.Operation.ADDITION))
    );

    public static final ArmorSet COMMISSAR = new ArmorSet(new ArmorSet.Traits()
            .modifier(SCGEAttributes.BULLET_DAMAGE_TAKEN_MULT.get(), new AttributeModifier("commissar_set_bullet_damage_taken", -0.1, AttributeModifier.Operation.ADDITION))
    );

    public static final ArmorSet TREATED_IRON = new ArmorSet(new ArmorSet.Traits());

    public static final ArmorSet LEVIATHAN = new ArmorSet(new ArmorSet.Traits()
            .effect(MobEffects.WATER_BREATHING, 0)
            .effect(MobEffects.DOLPHINS_GRACE, 0)
    );

    public static final ArmorSet GOLDEN_IDOL = new GoldenIdolArmorSet(new ArmorSet.Traits()
            .effect(MobEffects.FIRE_RESISTANCE, 0)
    );

    public static final ArmorSet ENLIGHTENED = new ArmorSet(new ArmorSet.Traits()
            .effect(MobEffects.REGENERATION, 0)
    );

    public static final ArmorSet JUGGERNAUT = new ArmorSet(new ArmorSet.Traits()
            .resistance(ModEffects.LACERATED.get())
    );

    public static final ArmorSet RITUAL = new RitualArmorSet(new ArmorSet.Traits());

    public static final ArmorSet PIONEER = new ArmorSet(new ArmorSet.Traits()
            .resistance(MobEffects.LEVITATION)
    );

    public static void onEntityHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(event.getSource().getDirectEntity() instanceof LivingEntity hurter)) return;

        if (ArmorSet.getArmorSet(hurter) == TREATED_IRON) {
            entity.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 120));
        }

        if (ArmorSet.getArmorSet(entity) == TREATED_IRON) {
            hurter.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 120));
        }
    }
}
