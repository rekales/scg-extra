package net.zincstudios.scgextra.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

import java.util.List;

public class GoldenIdolArmorSet extends ArmorSet {

    public static final float RADIUS = 5F;

    public GoldenIdolArmorSet(ResourceLocation resLoc, Traits traits) {
        super(resLoc, traits);
    }

    @Override
    public void onTick(LivingEntity entity) {
        super.onTick(entity);

        if (entity.tickCount % (TICK_INTERVAL*5) != 0) return;

        List<LivingEntity> entities = entity.level().getEntitiesOfClass(
                LivingEntity.class,
                entity.getBoundingBox().inflate(RADIUS, RADIUS/2, RADIUS),
                other -> entity.closerThan(other, RADIUS) && !(other instanceof Enemy)
        );

        for (LivingEntity livingEntity : entities) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 115, 0, true, true));
        }
    }
}
