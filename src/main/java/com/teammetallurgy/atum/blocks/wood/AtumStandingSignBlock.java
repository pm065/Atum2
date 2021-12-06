package com.teammetallurgy.atum.blocks.wood;

import com.teammetallurgy.atum.init.AtumTileEntities;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;

import javax.annotation.Nonnull;

public class AtumStandingSignBlock extends StandingSignBlock {

    public AtumStandingSignBlock(Properties properties, WoodType type) {
        super(properties, type);
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockGetter world) {
        return AtumTileEntities.SIGN.create();
    }
}
