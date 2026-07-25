package net.zincstudios.scgextra.item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Consumer;

public class JuggernautChestplateItem extends GeoArmorSetPartItem {

    public JuggernautChestplateItem(ArmorMaterial material, Type type, ArmorSet armorSet) {
        super(material, type, armorSet);
    }

    public JuggernautChestplateItem(ArmorMaterial material, Type type, ArmorSet armorSet, Properties properties) {
        super(material, type, armorSet, properties);
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                   EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null && itemStack.getItem() instanceof GeoArmorSetPartItem item) {
                    this.renderer = new BaseArmorSetPartRenderer(
                            new JuggernautChestplateModel<>(item.getArmorSet().getResourceLocation()));
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
}
