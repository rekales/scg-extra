package net.zincstudios.scgextra.worldgen.structure;

import net.zincstudios.scgextra.SCGExtra;
import net.zincstudios.scgextra.worldgen.structure.pieces.AsgharSoulForgePiece;
import net.zincstudios.scgextra.worldgen.structure.pieces.RRCWarshipPiece;
import net.zincstudios.scgextra.worldgen.structure.pieces.WarshipPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModPieces {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES = DeferredRegister
            .create(Registries.STRUCTURE_PIECE, SCGExtra.MOD_ID);

    public static final RegistryObject<StructurePieceType> WARSHIP =
        STRUCTURE_PIECES.register("fac_warship", ()->WarshipPiece::new);

    public static final RegistryObject<StructurePieceType> RRC_WARSHIP =
        STRUCTURE_PIECES.register("rrc_warship", ()->RRCWarshipPiece::new);

    public static final RegistryObject<StructurePieceType> ASGHAR_SOUL_FORGE =
        STRUCTURE_PIECES.register("asghar_soul_forge", ()->AsgharSoulForgePiece::new);

    public static void register(IEventBus modEventBus) {
        STRUCTURE_PIECES.register(modEventBus);
    }
}
