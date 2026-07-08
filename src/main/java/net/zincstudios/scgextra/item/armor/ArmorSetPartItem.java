package net.zincstudios.scgextra.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;

public class ArmorSetPartItem extends ArmorItem implements ArmorSetPart{

    private final ArmorSet armorSet;

    public ArmorSetPartItem(ArmorMaterial material, Type type, ArmorSet armorSet) {
        this(material, type, armorSet, new Properties());
    }

    public ArmorSetPartItem(ArmorMaterial material, Type type, ArmorSet armorSet, Properties properties) {
        super(material, type, properties);
        this.armorSet = armorSet;
    }

    @Override
    public @NotNull ArmorSet getArmorSet() {
        return this.armorSet;
    }
}
