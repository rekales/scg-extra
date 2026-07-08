package net.zincstudios.scgextra.item;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class MedalItem extends Item {

    private static final int TICK_INTERVAL = 10;

    private static final Map<UUID, Attribute> ALL_MODIFIER_ATTRIBUTES = new HashMap<>();

    private final Map<Attribute, AttributeModifier> modifiers;
    private final Map<MobEffect, Integer> effects;

    public MedalItem(MedalItem.Traits traits) {
        this(new Item.Properties().stacksTo(1), traits);
    }

    public MedalItem(Item.Properties properties, MedalItem.Traits traits) {
        super(properties);
        this.modifiers = Map.copyOf(traits.modifiers);
        this.effects = Map.copyOf(traits.effects);
        for (Map.Entry<Attribute, AttributeModifier> e : traits.modifiers.entrySet()) {
            ALL_MODIFIER_ATTRIBUTES.put(e.getValue().getId(), e.getKey());
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        Player player = event.player;

        if (event.player.tickCount % TICK_INTERVAL != 0) return;

        Map<UUID, AttributeModifier> desiredModifiers = new HashMap<>();

        for (ItemStack stack : player.getInventory().items) { // .items is the main 27+9 slots
            if (!(stack.getItem() instanceof MedalItem medalItem)) continue;

            for (Map.Entry<MobEffect, Integer> entry : medalItem.effects.entrySet()) {
                player.addEffect(new MobEffectInstance(entry.getKey(), 115, entry.getValue(), true, true));
            }
            for (Map.Entry<Attribute, AttributeModifier> entry : medalItem.modifiers.entrySet()) {
                desiredModifiers.put(entry.getValue().getId(), entry.getValue());
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

    // for cleaner registration
    public static class Traits {
        private final Map<Attribute, AttributeModifier> modifiers = new HashMap<>();
        private final Map<MobEffect, Integer> effects = new HashMap<>();

        public MedalItem.Traits modifier(Attribute attribute, AttributeModifier modifier) {
            this.modifiers.put(attribute, modifier);
            return this;
        }

        public MedalItem.Traits effect(MobEffect effect, int amplifier) {
            this.effects.put(effect, amplifier);
            return this;
        }
    }
}
