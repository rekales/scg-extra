package net.zincstudios.scgextra.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class MedalItem extends Item {

    private final Consumer<LivingEntity> effectApplicator;
    private final int interval;

    public MedalItem(Properties properties, Supplier<MobEffectInstance> effectInstance) {
        this(properties, entity -> entity.addEffect(effectInstance.get()), 10);
    }

    public MedalItem(Properties properties, Consumer<LivingEntity> effectApplicator) {
        this(properties, effectApplicator, 10);
    }

    public MedalItem(Properties properties, Consumer<LivingEntity> effectApplicator, int interval) {
        super(properties);
        this.effectApplicator = effectApplicator;
        this.interval = interval;
    }

    public void tickEffect(LivingEntity entity) {
        if (entity.level().getGameTime()%this.interval == 0) {
            this.applyEffect(entity);
        }
    }

    public void applyEffect(LivingEntity entity) {
        this.effectApplicator.accept(entity);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        this.tickEffect(player);
    }



}
