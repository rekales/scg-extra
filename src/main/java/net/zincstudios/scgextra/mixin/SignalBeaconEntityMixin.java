package net.zincstudios.scgextra.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.zincstudios.scgextra.entity.cog.COGEntities;
import net.zincstudios.scgextra.entity.cog.vulture.CogVultureEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import top.ribs.scguns.entity.monster.SignalBeaconEntity;

@Mixin(value = SignalBeaconEntity.class, remap = false)
public class SignalBeaconEntityMixin {

    @WrapOperation(
            method = "spawnSkyCarriers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"
            )
    )
    private Entity wrapSkyCarrierCreate(EntityType<?> entityType, Level level, Operation<Entity> original, @Local(name="spawnPos") Vec3 spawnPos,
                                        @Local(name="successfulSpawns") LocalIntRef successfulSpawns, @Local(name="beaconPosition") Vec3 beaconPosition) {
        if (level.getRandom().nextBoolean()) return original.call(entityType, level);

        CogVultureEntity vulture = (COGEntities.VULTURE.get()).create(level);
        if (vulture == null) return null;

        BlockHitResult result = level.clip(new ClipContext(
                spawnPos, spawnPos.add(0, -16, 0),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                null
        ));

        if (result.getType() == HitResult.Type.MISS) return null;
        Vec3 newPos = result.getBlockPos().above().getCenter();

        vulture.moveTo(newPos.x, newPos.y, newPos.z, level.getRandom().nextFloat() * 360.0F, 0.0F);
        vulture.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(beaconPosition, 1.0f, 6));
        level.addFreshEntity(vulture);
        ((ServerLevel)level).sendParticles(ParticleTypes.CLOUD, vulture.getX(), vulture.getY(), vulture.getZ(), 10, 0.5F, 0.2, 0.2, 0.1);
        level.playSound(null, vulture.getX(), vulture.getY(), vulture.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
        successfulSpawns.set(successfulSpawns.get()+1);

        return null;
    }
}
