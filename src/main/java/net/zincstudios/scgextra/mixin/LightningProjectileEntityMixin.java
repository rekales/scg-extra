package net.zincstudios.scgextra.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.entity.PartEntity;
import net.zincstudios.scgextra.block.wreckerturret.WreckerTurretBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.ribs.scguns.entity.projectile.LightningProjectileEntity;
import top.ribs.scguns.entity.projectile.ProjectileEntity;

@Mixin(value = LightningProjectileEntity.class, remap = false)
public class LightningProjectileEntityMixin {

    @SuppressWarnings("ModifyVariableMayUseName")
    @ModifyVariable(
            method = "onHitEntity",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0)
    private Entity modifyEntity(Entity value) {
        if (value instanceof PartEntity<?> part)
            return part.getParent();
        return value;
    }

    @Inject(method = "onHitBlock", at = @At("TAIL"))
    private void afterOnHitBlock(BlockState state, BlockPos pos, Direction face, double x, double y, double z, CallbackInfo ci) {
        ProjectileEntity self = (ProjectileEntity) (Object) this;
        if (self.level().getBlockEntity(pos) instanceof WreckerTurretBlockEntity turret) {
            turret.onHitByLightningProjectile();
        }
    }
}
