package net.zincstudios.scgextra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.zincstudios.scgextra.entity.ModEntities;
import net.zincstudios.scgextra.entity.guardian_statue.GuardianStatueEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces.MonumentBuilding;

@Mixin(value = MonumentBuilding.class)
public class OceanMonumentEntryRoomMixin {
    boolean spawned = false;
    // ArrayList<BlockPos> spawnedAt = new ArrayList<>();
    @Inject(method = "generateWing", at=@At("TAIL"))
    private void onGenerateWing(boolean pWing, int pX, WorldGenLevel pLevel, RandomSource pRandom, BoundingBox pBox, CallbackInfo ci){
        if(!spawned){
            BlockPos pos = pBox.getCenter();
            GuardianStatueEntity entity = new GuardianStatueEntity(ModEntities.GUARDIAN_STATUE.get(), pLevel.getLevel());
            entity.moveTo(pos.below(70), 0, 0);
            pLevel.addFreshEntity(entity);
            spawned = true;
        }else{
            spawned = false;
        }
    }
}