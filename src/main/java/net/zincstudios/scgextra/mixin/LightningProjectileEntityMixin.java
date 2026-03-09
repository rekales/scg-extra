package net.zincstudios.scgextra.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.ribs.scguns.entity.projectile.LightningProjectileEntity;

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
}
