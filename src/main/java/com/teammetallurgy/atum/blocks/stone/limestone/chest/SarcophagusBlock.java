package com.teammetallurgy.atum.blocks.stone.limestone.chest;

import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.blocks.QuandaryBlock;
import com.teammetallurgy.atum.blocks.base.ChestBaseBlock;
import com.teammetallurgy.atum.blocks.stone.limestone.chest.tileentity.SarcophagusTileEntity;
import com.teammetallurgy.atum.init.AtumBlocks;
import com.teammetallurgy.atum.init.AtumTileEntities;
import com.teammetallurgy.atum.network.NetworkHandler;
import com.teammetallurgy.atum.network.packet.SyncHandStackSizePacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.level.BlockEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = Atum.MOD_ID)
public class SarcophagusBlock extends ChestBaseBlock {

    public SarcophagusBlock() {
        super(AtumTileEntities.SARCOPHAGUS::get, BlockBehaviour.Properties.of().mapColor(MapColor.SAND).instrument(NoteBlockInstrument.BASEDRUM).strength(4.0F));
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return AtumTileEntities.SARCOPHAGUS.get().create(pos, state);
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (state.getBlock() instanceof SarcophagusBlock) {
            BlockEntity tileEntity = event.getLevel().getBlockEntity(event.getPos());
            if (tileEntity instanceof SarcophagusTileEntity && !((SarcophagusTileEntity) tileEntity).isOpenable) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof SarcophagusTileEntity && !((SarcophagusTileEntity) tileEntity).isOpenable) {
            return 6000000.0F;
        } else {
            return super.getExplosionResistance(state, level, pos, explosion);
        }
    }

    @Override
    @Nonnull
    public InteractionResult use(BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        Direction facing = state.getValue(FACING);

        //Right-Click left block, when right-clicking right block
        BlockPos posLeft = pos.relative(facing.getClockWise());
        BlockEntity tileLeft = level.getBlockEntity(posLeft);
        if (level.getBlockState(posLeft).getBlock() == this && tileLeft instanceof SarcophagusTileEntity sarcophagus) {
            if (level.getDifficulty() != Difficulty.PEACEFUL && !sarcophagus.hasSpawned) {
                this.use(state, level, pos.relative(facing.getClockWise()), player, hand, hit);
                return InteractionResult.PASS;
            }
        }

        if (tileEntity instanceof SarcophagusTileEntity sarcophagus) {
            if (level.getDifficulty() != Difficulty.PEACEFUL && !sarcophagus.hasSpawned) {
                if (QuandaryBlock.Helper.canSpawnPharaoh(level, pos, facing, player, level.random, sarcophagus)) {
                    return InteractionResult.PASS;
                } else if (!sarcophagus.isOpenable) {
                    player.displayClientMessage(Component.translatable("chat.atum.cannot_spawn_pharaoh").withStyle(ChatFormatting.RED), true);
                    level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 0.7F, 0.4F, false);
                    return InteractionResult.PASS;
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public MenuProvider getMenuProvider(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) { //Workaround so you can't see loot before pharaoh is beaten
        BlockEntity tileEntity = level.getBlockEntity(pos);
        return tileEntity instanceof SarcophagusTileEntity && ((SarcophagusTileEntity) tileEntity).isOpenable ? super.getMenuProvider(state, level, pos) : null;
    }

    @Override
    public void setPlacedBy(@Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull LivingEntity placer, @Nonnull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity tileEntity = level.getBlockEntity(pos);

        if (tileEntity instanceof SarcophagusTileEntity sarcophagus) {
            sarcophagus.hasSpawned = true;
            sarcophagus.setOpenable();
            sarcophagus.setChanged();

            for (Direction horizontal : Direction.Plane.HORIZONTAL) {
                BlockEntity tileEntityOffset = level.getBlockEntity(pos.relative(horizontal));
                if (tileEntityOffset instanceof SarcophagusTileEntity) {
                    ((SarcophagusTileEntity) tileEntityOffset).hasSpawned = true;
                    ((SarcophagusTileEntity) tileEntityOffset).setOpenable();
                    tileEntityOffset.setChanged();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlaced(BlockEvent.EntityPlaceEvent event) { //Prevent placement, 1 block left of another block
        BlockState placedState = event.getPlacedBlock();
        if (placedState.getBlock() instanceof SarcophagusBlock) {
            if (!canPlaceRightSac(event.getLevel(), event.getPos(), placedState.getValue(FACING))) {
                event.setCanceled(true);
                if (event.getEntity() instanceof ServerPlayer player) {
                    ItemStack placedStack = new ItemStack(placedState.getBlock().asItem());
                    InteractionHand hand = player.getMainHandItem().getItem() == placedStack.getItem() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                    NetworkHandler.sendTo(player, new SyncHandStackSizePacket(placedStack, hand == InteractionHand.MAIN_HAND ? 1 : 0));
                }
            }
        }
    }

    private static boolean canPlaceRightSac(LevelAccessor level, BlockPos pos, Direction facing) {
        BlockPos posOffset = pos.relative(facing.getCounterClockWise());
        BlockState offsetState = level.getBlockState(posOffset);
        if (offsetState.getBlock() instanceof SarcophagusBlock) {
            return offsetState.getValue(SarcophagusBlock.TYPE) == ChestType.LEFT && offsetState.getValue(SarcophagusBlock.FACING) == facing;
        }
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getCloneItemStack(@Nonnull BlockState state, @Nonnull HitResult target, @Nonnull LevelReader level, @Nonnull BlockPos pos, @Nonnull Player player) {
        return new ItemStack(AtumBlocks.SARCOPHAGUS.get());
    }
}