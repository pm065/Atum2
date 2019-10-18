/*package com.teammetallurgy.atum.world;

import com.teammetallurgy.atum.blocks.BlockSandLayers;
import com.teammetallurgy.atum.init.AtumBiomes;
import com.teammetallurgy.atum.init.AtumBlocks;
import com.teammetallurgy.atum.network.NetworkHandler;
import com.teammetallurgy.atum.network.packet.PacketStormStrength;
import com.teammetallurgy.atum.network.packet.PacketWeather;
import com.teammetallurgy.atum.utils.AtumConfig;
import com.teammetallurgy.atum.utils.Constants;
import com.teammetallurgy.atum.world.biome.base.AtumBiomeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class WorldProviderAtum extends Dimension {
    private static BlockPos usePos;
    public boolean hasStartStructureSpawned;

    @Override
    @Nonnull
    public DimensionType getDimensionType() {
        return AtumDimensionRegistration.ATUM;
    }

    @Override
    protected void init() {
        this.hasSkyLight = true;
        this.biomeProvider = new AtumBiomeProvider(world.getWorldInfo());
        CompoundNBT tagCompound = this.world.getWorldInfo().getDimensionData(this.world.dimension.getDimension());
        this.hasStartStructureSpawned = this.world instanceof WorldServer && tagCompound.getBoolean("HasStartStructureSpawned");
        this.isStorming = this.world instanceof WorldServer && tagCompound.getBoolean("IsStorming");
    }

    @Override
    @Nonnull
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorAtum(world, world.getSeed(), true, world.getWorldInfo().getGeneratorOptions());
    }

    @Override
    public void setWorldTime(long time) {
        if (time == 24000L && this.world.getWorldInfo() instanceof DerivedWorldInfo) {
            ((DerivedWorldInfo) this.world.getWorldInfo()).delegate.setWorldTime(time); //Workaround for making sleeping work in Atum
        }
        super.setWorldTime(time);
    }

    @SubscribeEvent
    public static void onUseBucket(PlayerInteractEvent.RightClickBlock event) {
        if (AtumConfig.WORLD_GEN.waterLevel.get() > 0) {
            usePos = event.getPos();
        } else {
            usePos = null;
        }
    }

    @Override
    public boolean doesWaterVaporize() {
        if (usePos != null) {
            return world.getBiome(usePos) != AtumBiomes.OASIS && usePos.getY() >= AtumConfig.WORLD_GEN.waterLevel.get();
        } else {
            return false;
        }
    }

    @Override
    public boolean canCoordinateBeSpawn(int x, int z) {
        BlockPos pos = new BlockPos(x, 0, z);

        if (this.world.getBiome(pos).ignorePlayerSpawnSuitability()) {
            return true;
        } else {
            Block spawnBlock = this.world.getGroundAboveSeaLevel(pos).getBlock();
            return spawnBlock == AtumBlocks.SAND || spawnBlock == AtumBlocks.FERTILE_SOIL || spawnBlock == AtumBlocks.LIMESTONE_GRAVEL;
        }
    }

    @Override
    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public Vec3d getFogColor(float par1, float par2) {
        float f = MathHelper.cos(par1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
        if (f < 0.2F) {
            f = 0.2F;
        }

        if (f > 1.0F) {
            f = 1.0F;
        }

        // Darken fog as sandstorm builds
        // f *= (1 - this.stormStrength) * 0.8 + 0.2;

        float f1 = 0.9F * f;
        float f2 = 0.75F * f;
        float f3 = 0.6F * f;
        return new Vec3d((double) f1, (double) f2, (double) f3);
    }

    @Override
    public void onWorldSave() {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putBoolean("HasStartStructureSpawned", hasStartStructureSpawned);
        tagCompound.putBoolean("IsStorming", isStorming);
        world.getWorldInfo().setDimensionData(this.world.dimension.getDimension(), tagCompound);
    }

    public boolean isStorming;
    public int stormTime;
    public float prevStormStrength;
    public float stormStrength;
    private int updateLCG = (new Random()).nextInt();
    private long lastUpdateTime;

    @Override
    public void calculateInitialWeather() {
        super.calculateInitialWeather();
        if (isStorming) {
            stormStrength = 1;
        }
    }

    private boolean canPlaceSandAt(BlockPos pos, Biome biome) {
        BlockState state = world.getBlockState(pos.down());
        if (state.getBlock() == AtumBlocks.SAND || state.getBlock() == AtumBlocks.LIMESTONE_GRAVEL || !ChunkGeneratorAtum.canPlaceSandLayer(world, pos, biome)) {
            return false;
        }
        state = world.getBlockState(pos);
        if (state.getBlock().isReplaceable(world, pos)) {
            state = world.getBlockState(pos.down());
            BlockFaceShape blockFaceShape = state.getBlockFaceShape(world, pos.down(), Direction.UP);
            return blockFaceShape == BlockFaceShape.SOLID || state.getBlock().isLeaves(state, world, pos.down());
        }
        return false;
    }

    @Override
    public void updateWeather() {
        int cleanWeatherTime = world.getWorldInfo().getCleanWeatherTime();

        if (cleanWeatherTime > 0) {
            --cleanWeatherTime;
            world.getWorldInfo().setCleanWeatherTime(cleanWeatherTime);
            this.stormTime = isStorming ? 1 : 2;
        }

        if (stormTime <= 0) {
            if (isStorming) {
                stormTime = this.world.rand.nextInt(6000) + 6000;
            } else {
                stormTime = this.world.rand.nextInt(168000) + 12000;
            }
            NetworkHandler.WRAPPER.sendToDimension(new PacketWeather(isStorming, stormTime), this.getDimension());
        } else {
            stormTime--;
            if (stormTime <= 0) {
                isStorming = !isStorming;
            }
        }

        prevStormStrength = stormStrength;
        if (isStorming) {
            stormStrength += 1 / (float) (20 * AtumConfig.SANDSTORM.sandstormTransitionTime.get());
        } else {
            stormStrength -= 1 / (float) (20 * AtumConfig.SANDSTORM.sandstormTransitionTime.get());
        }
        stormStrength = MathHelper.clamp(stormStrength, 0, 1);

        if (stormStrength != prevStormStrength || lastUpdateTime < System.currentTimeMillis() - 1000) {
            NetworkHandler.WRAPPER.sendToDimension(new PacketStormStrength(stormStrength), this.getDimension());
            lastUpdateTime = System.currentTimeMillis();
        }

        if (!world.isRemote) {
            Iterator<Chunk> iterator = world.getPersistentChunkIterable(((WorldServer) world).getPlayerChunkMap().getChunkIterator());
            while (iterator.hasNext()) {
                Chunk chunk = iterator.next();
                int x = chunk.x * 16;
                int z = chunk.z * 16;

                if (world.rand.nextInt(40) == 0) {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    int j2 = this.updateLCG >> 2;
                    BlockPos pos = world.getPrecipitationHeight(new BlockPos(x + (j2 & 15), 0, z + (j2 >> 8 & 15)));
                    BlockPos posDown = pos.down();

                    if (world.isAreaLoaded(posDown, 1)) {// Forge: check area to avoid loading neighbors in unloaded chunks
                        BlockState sandState = world.getBlockState(pos);
                        if (stormStrength > 0.9f) {
                            if (sandState.getBlock() == AtumBlocks.SAND_LAYERED) {
                                int layers = sandState.getValue(BlockSandLayers.LAYERS);
                                if (layers < 3) {
                                    world.setBlockState(pos, sandState.with(BlockSandLayers.LAYERS, ++layers));
                                }
                            } else if (canPlaceSandAt(pos, world.getBiome(pos))) {
                                world.setBlockState(pos, AtumBlocks.SAND_LAYERED.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }
}*/