package com.teammetallurgy.atum.blocks;

import com.mojang.serialization.MapCodec;
import com.teammetallurgy.atum.init.AtumBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IPlantable;
import net.neoforged.neoforge.common.PlantType;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class StrangeSandBlock extends FallingBlock {
    public static final MapCodec<StrangeSandBlock> CODEC = simpleCodec(StrangeSandBlock::new);

    public StrangeSandBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    @Nonnull
    protected MapCodec<? extends StrangeSandBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean canSustainPlant(@Nonnull BlockState state, @Nonnull BlockGetter level, BlockPos pos, @Nonnull Direction direction, IPlantable plantable) {
        BlockState plant = plantable.getPlant(level, pos.relative(direction));
        PlantType plantType = plantable.getPlantType(level, pos.above());
        boolean hasWater = (level.getBlockState(pos.east()).getFluidState().is(FluidTags.WATER) ||
                level.getBlockState(pos.west()).getFluidState().is(FluidTags.WATER)||
                level.getBlockState(pos.north()).getFluidState().is(FluidTags.WATER) ||
                level.getBlockState(pos.south()).getFluidState().is(FluidTags.WATER));

        if (plant.getBlock() instanceof CactusBlock || plant.getBlock() == AtumBlocks.ANPUTS_FINGERS.get()) {
            return true;
        }

        if (plantType.equals(PlantType.DESERT)) {
            return true;
        } else if (plantType.equals(PlantType.BEACH)) {
            return hasWater;
        } else {
            return super.canSustainPlant(state, level, pos, direction, plantable);
        }
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
        return toolAction == ToolActions.SHOVEL_FLATTEN ? AtumBlocks.STRANGE_SAND_PATH.get().defaultBlockState() : super.getToolModifiedState(state, context, toolAction, simulate);
    }
}