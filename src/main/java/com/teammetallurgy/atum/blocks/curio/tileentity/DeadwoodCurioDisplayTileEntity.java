package com.teammetallurgy.atum.blocks.curio.tileentity;

import com.teammetallurgy.atum.init.AtumTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class DeadwoodCurioDisplayTileEntity extends CurioDisplayTileEntity {

    public DeadwoodCurioDisplayTileEntity(BlockPos pos, BlockState state) {
        super(AtumTileEntities.DEADWOOD_CURIO_DISPLAY.get(), pos, state);
    }
}