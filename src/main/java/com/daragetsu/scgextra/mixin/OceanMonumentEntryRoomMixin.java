package com.daragetsu.scgextra.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.daragetsu.scgextra.entity.ModEntities;
import com.daragetsu.scgextra.entity.guardian_statue.GuardianStatueEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces.MonumentBuilding;

@Mixin(value = MonumentBuilding.class)
public class OceanMonumentEntryRoomMixin {
    //TODO: this spawns 2 at one place, force it to only spawn 1
    ArrayList<BlockPos> spawnedAt = new ArrayList<>();
    @Inject(method = "generateWing", at=@At("TAIL"))
    private void onGenerateWing(boolean pWing, int pX, WorldGenLevel pLevel, RandomSource pRandom, BoundingBox pBox, CallbackInfo ci){
        BlockPos pos = pBox.getCenter();
        if(!spawnedAt.contains(pos)){
            GuardianStatueEntity entity = new GuardianStatueEntity(ModEntities.GUARDIAN_STATUE.get(), pLevel.getLevel());
            entity.moveTo(pos.below(60), 0, 0);
            pLevel.addFreshEntity(entity);
            spawnedAt.add(pos.below(60));
        }
    }
}