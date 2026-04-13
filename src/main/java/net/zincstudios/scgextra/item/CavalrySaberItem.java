package net.zincstudios.scgextra.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import top.ribs.scguns.init.ModEffects;
import top.ribs.scguns.init.ModTags;

public class CavalrySaberItem extends SwordItem implements HurtEffects {

    public CavalrySaberItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        this.hurtEffect(stack, target, attacker);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void hurtEffect(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide() && !target.getType().is(ModTags.Entities.CANNOT_BE_LACERATED)) {
            target.addEffect(new MobEffectInstance(ModEffects.LACERATED.get(), 100));
        }
    }
}
