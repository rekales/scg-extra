package net.zincstudios.scgextra.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class WreckerSledgeHammerItem extends SwordItem implements ArmorPiercing {

    private static final UUID REACH_MODIFIER_UUID = UUID.fromString("4f3a58fe-2d90-4e83-9d71-1e2c9d3ef04a");

    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public WreckerSledgeHammerItem(Tier tier, int attackDamage, float attackSpeed, double reachModifier, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                attackDamage + tier.getAttackDamageBonus(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                attackSpeed, AttributeModifier.Operation.ADDITION));
        builder.put(ForgeMod.ENTITY_REACH.get(), new AttributeModifier(REACH_MODIFIER_UUID, "Weapon modifier",
                reachModifier, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }
}
