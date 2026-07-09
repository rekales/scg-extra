package net.zincstudios.scgextra.item.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.zincstudios.scgextra.SCGExtra;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class GeoArmorSetPartItem extends ArmorSetPartItem implements GeoItem {

    private final AnimatableInstanceCache geocache = GeckoLibUtil.createInstanceCache(this);

    public GeoArmorSetPartItem(ArmorMaterial material, Type type, ArmorSet armorSet) {
        super(material, type, armorSet);
    }

    public GeoArmorSetPartItem(ArmorMaterial material, Type type, ArmorSet armorSet, Properties properties) {
        super(material, type, armorSet, properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geocache;
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null && itemStack.getItem() instanceof GeoArmorSetPartItem item) {
                    this.renderer = new BaseArmorSetPartRenderer(new DefaultedGeoModel<>(SCGExtra.asResource("oppressor")) {  // TODO: temp, remove after
                        @Override
                        protected String subtype() {
                            return "armor";
                        }
                    });
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
}
