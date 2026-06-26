package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.zincstudios.scgextra.attributes.SCGEAttributes;
import org.spongepowered.asm.mixin.Mixin;
import top.ribs.scguns.init.ModDamageTypes;

@Mixin(value = LivingEntity.class)
public class LivingEntityMixin {

    @SuppressWarnings("ConstantValue")
    @WrapMethod(method = "getDamageAfterMagicAbsorb")
    private float beforeGetDamageAfterMagicAbsorb(DamageSource damageSource, float damageAmount, Operation<Float> original) {
        if (damageSource.is(ModDamageTypes.BULLET)
                && !damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)
                && (Object)this instanceof Player player) {
            float damageMult = (float) player.getAttributeValue(SCGEAttributes.BULLET_DAMAGE_TAKEN_MULT.get());
            return original.call(damageSource, damageAmount * damageMult);
        }

        return original.call(damageSource, damageAmount);
    }
}
