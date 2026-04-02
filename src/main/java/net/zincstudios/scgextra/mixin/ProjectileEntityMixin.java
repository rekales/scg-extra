package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.common.HeadShotHandler;
import net.zincstudios.scgextra.entity.common.Stunnable;
import net.zincstudios.scgextra.entity.common.WeakPointBox;
import net.zincstudios.scgextra.entity.common.WeakPointBoxManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.ribs.scguns.Config;
import top.ribs.scguns.entity.projectile.ProjectileEntity;
import top.ribs.scguns.init.ModDamageTypes;
import top.ribs.scguns.util.math.ExtendedEntityRayTraceResult;

import java.util.List;
import java.util.Optional;

@Mixin(value = ProjectileEntity.class, remap = false)
public class ProjectileEntityMixin {

    @Inject(
            method = "onHit",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/ribs/scguns/entity/projectile/ProjectileEntity;onHitEntity(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Z)V"
            )
    )
    private void beforeOnHit(HitResult result, Vec3 startVec, Vec3 endVec, CallbackInfo ci, @Local(name = "entityHitResult") ExtendedEntityRayTraceResult entityHitResult) {
        ProjectileEntity self = (ProjectileEntity) (Object) this;
        DamageSource source = ModDamageTypes.Sources.projectile(self.level().registryAccess(), self, self.getShooter());

        if (entityHitResult.isHeadshot() && entityHitResult.getEntity() instanceof HeadShotHandler headShotHandler) {
            headShotHandler.headshot(source, self.getDamage());
        }
    }

    // TODO: Requires fix on ScorchedGuns to use this proper method
//    @Inject(
//            method = "onHitEntity",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
//            )
//    )
//    private void beforeEntityHurt(Entity entity, Vec3 hitVec, Vec3 startVec, Vec3 endVec, boolean headshot,
//                                  CallbackInfo ci, @Local(name = "source") DamageSource source, @Local(name = "damage") float damage) {
//        if (headshot && entity instanceof HeadShotHandler headShotHandler) {
//            headShotHandler.headshot(source, damage);
//        }
//    }

    @Inject(
            method = "getHitResult",
            at = @At(
                    value = "INVOKE",
                    target = "Ltop/ribs/scguns/common/BoundingBoxManager;getHeadshotBoxes(Lnet/minecraft/world/entity/EntityType;)Ltop/ribs/scguns/interfaces/IHeadshotBox;"
            ),
            remap = false
    )
    private void beforeGetHeadshotBoxes(Entity entity, Vec3 startVec, Vec3 endVec, CallbackInfoReturnable<ProjectileEntity.EntityResult> cir,
                                        @Local(name = "boundingBox") AABB boundingBox, @Local(name = "hitPos") Vec3 hitPos, @Local(name = "headshot") LocalBooleanRef headshot) {
        List<WeakPointBox<LivingEntity>> weakPointBoxList = WeakPointBoxManager.getWeakPointBoxesList(entity.getType());

        if (weakPointBoxList != null) {
            for (WeakPointBox<LivingEntity> weakPointBox : weakPointBoxList) {
                AABB box = weakPointBox.getBoundingBox((LivingEntity)entity);
                if (box != null) {
                    box = box.move(boundingBox.getCenter().x, boundingBox.minY, boundingBox.getCenter().z);
                    Optional<Vec3> headshotHitPos = box.clip(startVec, endVec);
                    if (headshotHitPos.isEmpty()) {
                        box = box.inflate(Config.COMMON.gameplay.growBoundingBoxAmount.get(), 0.0, Config.COMMON.gameplay.growBoundingBoxAmount.get());
                        headshotHitPos = box.clip(startVec, endVec);
                    }

                    if (headshotHitPos.isPresent() && (hitPos == null || headshotHitPos.get().distanceTo(hitPos) < 0.5)) {
                        hitPos = headshotHitPos.get();
                        headshot.set(true);
//                        headshot = true;
                    }
                }
            }
        }
    }
}
