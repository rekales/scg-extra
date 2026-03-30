package net.zincstudios.scgextra.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.zincstudios.scgextra.SCGExtra;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.ribs.scguns.item.CogMaceItem;

import java.util.function.Consumer;

// Needed to be a geomodel because of multiple rotations with the spikes (1.20 vanilla limitation)
public class AtlanticMaceItem extends SwordItem implements GeoItem {

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public AtlanticMaceItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.resetFallDistance();
        stack.hurtAndBreak(1, attacker, (entity) -> entity.broadcastBreakEvent(entity.getUsedItemHand()));
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new SimpleCustomRenderer(new GeoItemRenderer<>(
                new DefaultedItemGeoModel<>(SCGExtra.asResource("atlantic_mace"))
        )));
    }
}
