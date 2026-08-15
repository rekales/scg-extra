package net.zincstudios.scgextra.worldgen.structure.pieces;

import net.zincstudios.scgextra.SCGExtra;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.zincstudios.scgextra.worldgen.structure.ModPieces;

//only made so blocks don't get waterlogged
public class RRCWarshipPiece extends TemplateStructurePiece{

    public static final ResourceLocation TEMPLATE_BACK = ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "rrc_warship/rrc_warship_back");
    public static final ResourceLocation TEMPLATE_FRONT = ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "rrc_warship/rrc_warship_front");

    public RRCWarshipPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModPieces.RRC_WARSHIP.get(), tag, context.structureTemplateManager(), id -> makeSettings());
    }

    public RRCWarshipPiece(StructureTemplateManager manager, BlockPos pos, ResourceLocation TEMPLATE) {
        super(
            ModPieces.RRC_WARSHIP.get(),
            0,
            manager,
            TEMPLATE,
            "rrc_warship",
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
               ((ChestBlockEntity)blockentity).setLootTable(ResourceLocation.fromNamespaceAndPath(SCGExtra.MOD_ID, "chests/rrc"), 274625);
            }
         }
    }
    private static StructurePlaceSettings makeSettings() {
    return new StructurePlaceSettings()
            .setRotation(Rotation.NONE)
            .setMirror(Mirror.NONE)
            .setKeepLiquids(false);
    }
}
