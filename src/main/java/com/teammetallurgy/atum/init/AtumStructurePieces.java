package com.teammetallurgy.atum.init;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.world.gen.structure.girafitomb.GirafiTombPieces;
import com.teammetallurgy.atum.world.gen.structure.pyramid.PyramidPieces;
import com.teammetallurgy.atum.world.gen.structure.ruins.RuinPieces;
import com.teammetallurgy.atum.world.gen.structure.tomb.TombPieces;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AtumStructurePieces {
    public static final DeferredRegister<StructurePieceType> STRUCTURE_PIECES_DEFERRED = DeferredRegister.create(Registries.STRUCTURE_PIECE, Atum.MOD_ID);
    public static final DeferredHolder<StructurePieceType, StructurePieceType> GIRAFI_TOMB = register(GirafiTombPieces.GirafiTombTemplate::new, "girafi_tomb");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> TOMB = register(TombPieces.TombTemplate::new, "tomb");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> RUIN = register(RuinPieces.RuinTemplate::new, "ruin");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> PYRAMID = register(PyramidPieces.PyramidTemplate::new, "pyramid");
    public static final DeferredHolder<StructurePieceType, StructurePieceType> PYRAMID_MAZE = register((manager, nbt) -> new PyramidPieces.Maze(nbt), "pyramid_maze");
    //public static final StructurePieceType MINESHAFT_CORRIDOR = register(AtumMineshaftPieces.Corridor::new, "mineshaft_corridor");
    //public static final StructurePieceType MINESHAFT_CROSSING = register(AtumMineshaftPieces.Cross::new, "mineshaft_crossing");
    //public static final StructurePieceType MINESHAFT_ROOM = register(AtumMineshaftPieces.Room::new, "mineshaft_room");
    //public static final StructurePieceType MINESHAFT_STAIRS = register(AtumMineshaftPieces.Stairs::new, "mineshaft_stairs");

    private static DeferredHolder<StructurePieceType, StructurePieceType> register(StructurePieceType.StructureTemplateType type, String name) {
        return STRUCTURE_PIECES_DEFERRED.register(name, () -> type);
    }
}