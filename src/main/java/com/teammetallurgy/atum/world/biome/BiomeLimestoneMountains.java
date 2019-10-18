package com.teammetallurgy.atum.world.biome;

import com.teammetallurgy.atum.entity.animal.DesertWolfEntity;
import com.teammetallurgy.atum.init.AtumBlocks;
import net.minecraft.entity.EntityClassification;

public class BiomeLimestoneMountains extends AtumBiome {

    public BiomeLimestoneMountains(AtumBiomeProperties properties) {
        super(properties);

        this.fillerBlock = AtumBlocks.LIMESTONE.getDefaultState();

        this.addDefaultSpawns();
    }

    @Override
    protected void addDefaultSpawns() {
        super.addDefaultSpawns();
        
        addSpawn(DesertWolfEntity.class, 5, 2, 4, EntityClassification.CREATURE);
    }
}