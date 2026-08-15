package net.zincstudios.scgextra.item;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.zincstudios.scgextra.SCGExtra;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.item.GunItem;

import java.util.List;
import java.util.function.Consumer;

// TODO: proper gun implementation
@MethodsReturnNonnullByDefault
public class WreckerRpgItem extends GunItem implements GeoItem {

    private static final RawAnimation NO_AMMO = RawAnimation.begin().thenPlayAndHold("no_ammo");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public WreckerRpgItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, worldIn, tooltip, flag);
        tooltip.add(Component.translatable("item." + SCGExtra.MOD_ID + ".single_use_desc").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("SingleUseFired")) {
            tag.putBoolean("SingleUseFired", true);
            tag.putInt("AmmoCount", 1);
            stack.setDamageValue(0);
        }

        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 0,
                state -> {
                    ItemStack stack = state.getData(DataTickets.ITEMSTACK);
                    if (Gun.hasAmmo(stack)) return PlayState.STOP;
                    return state.setAndContinue(NO_AMMO);
                })
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @OnlyIn(value = Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SimpleCustomRenderer(new GeoItemRenderer<>(
                new DefaultedItemGeoModel<>(SCGExtra.asResource("wrecker_rpg"))
        )));
    }
}
