package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.asgharian.BulletSpawnOffset;
import net.zincstudios.scgextra.entity.common.GunnerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.common.Gun;
import top.ribs.scguns.entity.ai.AIGunEvent;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

@Mixin(value = AIGunEvent.class, remap = false)
public class AIGunEventMixin {

    @Inject(method = "performGunAttack", at = @At(value="HEAD"))
    private static void beforePerformGunAttack(Mob shooter, LivingEntity target, ItemStack itemStack, Gun modifiedGun, float accuracyModifier, CallbackInfo ci) {
        if (shooter instanceof GunnerEntity gunnerEntity) {
            gunnerEntity.onGunAttack(target, itemStack);
        }
    }

    @WrapOperation(
            method = "performGunAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/ribs/scguns/entity/projectile/ProjectileEntity;setPos(DDD)V"
            )
    )
    private static void wrapSetPos(ProjectileEntity instance, double x, double y, double z, Operation<Void> original, @Local(argsOnly = true) Mob shooter) {
        if (shooter instanceof BulletSpawnOffset bulletSpawnOffset) {
            Vec3 offset =  bulletSpawnOffset.getBulletSpawnOffset();
            original.call(instance, x+offset.x, y+offset.y-shooter.getEyeHeight(), z+offset.z);
        } else {
            original.call(instance, x, y, z);
        }
    }
}
