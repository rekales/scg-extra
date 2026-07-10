package net.zincstudios.scgextra.item.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.effects.ModEffects;

public class RitualArmorSet extends ArmorSet {

    public static final int COOLDOWN_DURATION = 20 * 60 * 20;

    public RitualArmorSet(ResourceLocation resLoc, Traits traits) {
        super(resLoc, traits);
    }

    @Override
    public void onTick(LivingEntity entity) {
        super.onTick(entity);
        int cooldownTicks = (int) (getReviveCooldownEnd(entity) - entity.level().getGameTime());
        MobEffectInstance effectInstance = entity.getEffect(ModEffects.MORTAL.get());

        if (cooldownTicks > 0) {
            if (effectInstance == null ||  effectInstance.getDuration() != cooldownTicks) {
                SCGExtra.LOGGER.debug("refreshed");
                entity.addEffect(new MobEffectInstance(ModEffects.MORTAL.get(), cooldownTicks));
            }
        } else if (effectInstance != null) {
            entity.removeEffect(ModEffects.MORTAL.get());
        }
    }

    // engraving cooldown nbt
    public static void setReviveCooldown(LivingEntity entity, long cooldownDuration) {
        CompoundTag tag = entity.getPersistentData();
        tag.putLong("ReviveCooldownEnd", entity.level().getGameTime() + cooldownDuration);
    }

    public static long getReviveCooldownEnd(LivingEntity entity) {
        CompoundTag tag = entity.getPersistentData();
        if (!tag.contains("ReviveCooldownEnd")) return 0;
        return tag.getLong("ReviveCooldownEnd");
    }
}
