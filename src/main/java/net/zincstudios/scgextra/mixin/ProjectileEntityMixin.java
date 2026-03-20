package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.Stunnable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

@Mixin(value = ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private void beforeEntityHurt(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot,
                                  CallbackInfo ci, @Local(name = "source") DamageSource source, @Local(name = "damage") float damage) {
        if (headshot && entity instanceof Stunnable stunnable) {
            stunnable.handleHeadshotStun(source, damage);
        }
    }
}
