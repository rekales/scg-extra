package net.zincstudios.scgextra.item.armor;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class PioneerChestplateItem extends ArmorSetPartItem {

    public PioneerChestplateItem(ArmorMaterial material, Type type, ArmorSet armorSet) {
        super(material, type, armorSet);
    }

    public PioneerChestplateItem(ArmorMaterial material, Type type, ArmorSet armorSet, Properties properties) {
        super(material, type, armorSet, properties);
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return this.getArmorSet() == ArmorSet.getArmorSet(entity);
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (!entity.level().isClientSide) {
            int nextFlightTick = flightTicks + 1;
            if (nextFlightTick % 10 == 0) {
                entity.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_GLIDE);
            }
        }
        return true;
    }
}
