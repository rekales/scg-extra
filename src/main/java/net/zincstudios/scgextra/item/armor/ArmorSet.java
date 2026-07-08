package net.zincstudios.scgextra.item.armor;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;
import java.util.*;

public class ArmorSet {

    private static final int TICK_INTERVAL = 10;
    private static final Map<UUID, Attribute> ALL_MODIFIER_ATTRIBUTES = new HashMap<>();

    private final Map<Attribute, AttributeModifier> modifiers;
    private final Map<MobEffect, Integer> effects;
    private final Set<MobEffect> resistances;

    public ArmorSet(ArmorSet.Traits traits) {
        this.modifiers = Map.copyOf(traits.modifiers);
        this.effects = Map.copyOf(traits.effects);
        this.resistances = Set.copyOf(traits.resistances);
        for (Map.Entry<Attribute, AttributeModifier> e : traits.modifiers.entrySet()) {
            ALL_MODIFIER_ATTRIBUTES.put(e.getValue().getId(), e.getKey());
        }
    }

    @Nullable
    public static ArmorSet getArmorSet(LivingEntity entity) {
        if (!(entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorSetPart headPart)) return null;
        if (!(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorSetPart chestPart)) return null;
        if (!(entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorSetPart legPart)) return null;
        if (!(entity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorSetPart feetPart)) return null;

        ArmorSet set = headPart.getArmorSet();
        if (set == chestPart.getArmorSet() && set == legPart.getArmorSet() && set == feetPart.getArmorSet()) return set;
        return null;
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        Player player = event.player;

        if (event.player.tickCount % TICK_INTERVAL != 0) return;

        Map<UUID, AttributeModifier> desiredModifiers = new HashMap<>();

        ArmorSet armorSet = getArmorSet(player);
        if (armorSet != null) {
            for (Map.Entry<Attribute, AttributeModifier> entry : armorSet.modifiers.entrySet()) {
                desiredModifiers.put(entry.getValue().getId(), entry.getValue());
            }
            for (Map.Entry<MobEffect, Integer> entry : armorSet.effects.entrySet()) {
                player.addEffect(new MobEffectInstance(entry.getKey(), 115, entry.getValue(), true, true));
            }
            for (MobEffect effect : armorSet.resistances) {
                player.removeEffect(effect);
            }
        }

        for (Map.Entry<UUID, Attribute> entry : ALL_MODIFIER_ATTRIBUTES.entrySet()) {
            UUID uuid = entry.getKey();
            AttributeInstance attr = player.getAttribute(entry.getValue());
            if (attr == null) continue;

            boolean shouldHave = desiredModifiers.containsKey(uuid);
            boolean hasModifier = attr.getModifier(uuid) != null;

            if (shouldHave && !hasModifier) {
                attr.addTransientModifier(desiredModifiers.get(uuid));
            } else if (!shouldHave && hasModifier) {
                attr.removeModifier(uuid);
            }
        }
    }

    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ArmorSet armorSet = getArmorSet(player);
        if (armorSet == null) return;

        if (armorSet.resistances.contains(event.getEffectInstance().getEffect())) {
            event.setResult(Event.Result.DENY);
        }
    }

    // TODO: Consider combining with MedalItem.Traits to a separate class along with handling
    public static class Traits {
        private final Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
        private final Map<MobEffect, Integer> effects = new HashMap<>();
        private final Set<MobEffect> resistances = new HashSet<>();

        public ArmorSet.Traits modifier(Attribute attribute, AttributeModifier modifier) {
            this.modifiers.put(attribute, modifier);
            return this;
        }

        public ArmorSet.Traits effect(MobEffect effect, int amplifier) {
            this.effects.put(effect, amplifier);
            return this;
        }

        public ArmorSet.Traits resistance(MobEffect effect) {
            this.resistances.add(effect);
            return this;
        }
    }
}
