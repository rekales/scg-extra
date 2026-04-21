package net.zincstudios.scgextra.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.ai.AIGunEvent;

@Mixin(value = AIGunEvent.class, remap = false)
public class AIGunEventMixin {

    @Inject(method = "performGunAttack", at = @At(value="HEAD"))
    private static void beforePerformGunAttack(Mob shooter, LivingEntity target, ItemStack itemStack, Gun modifiedGun, float accuracyModifier, CallbackInfo ci) {
        if (shooter instanceof GunnerEntity gunnerEntity) {
            gunnerEntity.onGunAttack(target, itemStack);
        }
    }
}
