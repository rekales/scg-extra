package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.PathfinderMob;
import net.zincstudios.scgextra.entity.asgharian.SimpleGunAttackGoal;
import net.zincstudios.scgextra.entity.common.Gunner;
import org.spongepowered.asm.mixin.Mixin;
import top.ribs.scguns.config.GunnerMobSpawner;

@Mixin(value = GunnerMobSpawner.class, remap = false)
public class GunnerMobSpawnerMixin {

    @WrapMethod(method = "hasGunAttackGoal")
    private static boolean hasSimpleGunAttackGoal(PathfinderMob mob, Operation<Boolean> original) {
        return original.call(mob)
                || mob instanceof Gunner
                || mob.goalSelector.getAvailableGoals().stream()
                        .anyMatch((goal) -> goal.getGoal() instanceof SimpleGunAttackGoal);
    }
}
