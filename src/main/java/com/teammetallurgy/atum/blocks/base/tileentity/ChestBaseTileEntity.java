package com.teammetallurgy.atum.blocks.base.tileentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class ChestBaseTileEntity extends ChestBlockEntity {
    public boolean canBeSingle;
    public boolean canBeDouble;
    private final Block chestBlock;

    public ChestBaseTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean canBeSingle, boolean canBeDouble, Block chestBlock) {
        super(type, pos, state);
        this.canBeSingle = canBeSingle;
        this.canBeDouble = canBeDouble;
        this.chestBlock = chestBlock;
    }

    @Override
    @Nonnull
    protected Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    private boolean isChestAt(BlockPos pos) {
        if (level == null) {
            return false;
        } else {
            Block block = level.getBlockState(pos).getBlock();
            BlockEntity tileEntity = level.getBlockEntity(pos);
            return tileEntity instanceof ChestBaseTileEntity && block == this.chestBlock;
        }
    }

    /*@Override
    @Nonnull
    public AABB getRenderBoundingBox() { //TODO
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1, worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
    }*/
}