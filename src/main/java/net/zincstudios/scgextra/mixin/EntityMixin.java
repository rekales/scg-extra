package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.zincstudios.scgextra.item.ArmorSet;
import net.zincstudios.scgextra.item.ArmorSets;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Entity.class)
public class EntityMixin {

    @WrapMethod(method = "dampensVibrations")
    private boolean atDampensVibrations(Operation<Boolean> original) {
        if ((Object)this instanceof LivingEntity entity) {
            ArmorSet set = ArmorSet.getArmorSet(entity);
            if (set == ArmorSets.ENLIGHTENED) {
                return true;
            }
        }

        return original.call();
    }
}
