package net.zincstudios.scgextra.worldgen.structure.pieces;

import net.zincstudios.scgextra.SCGExtra;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.zincstudios.scgextra.worldgen.structure.ModPieces;

//only made so blocks don't get waterlogged
public class AsgharSoulForgePiece extends TemplateStructurePiece{

    private static final ResourceLocation TEMPLATE = ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "asghar_soul_forge/asghar_soul_forge");

    public AsgharSoulForgePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModPieces.ASGHAR_SOUL_FORGE.get(), tag, context.structureTemplateManager(), id -> makeSettings());
    }

    public AsgharSoulForgePiece(StructureTemplateManager manager, BlockPos pos) {
        super(
            ModPieces.ASGHAR_SOUL_FORGE.get(),
            0,
            manager,
            TEMPLATE,
            "asghar_soul_forge",
            makeSettings(),
            pos
        );
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random,
            BoundingBox bb) {
                if ("chest".equals(name)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            BlockEntity blockentity = level.getBlockEntity(pos.below());
        if (blockentity instanceof ChestBlockEntity) {
               ((ChestBlockEntity)blockentity).setLootTable(ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "chests/asghar"), 654534);
            }
         }
    }
    private static StructurePlaceSettings makeSettings() {
    return new StructurePlaceSettings()
            .setRotation(Rotation.NONE)
            .setMirror(Mirror.NONE)
            .setKeepLiquids(false);
    }
    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);
    }
}
