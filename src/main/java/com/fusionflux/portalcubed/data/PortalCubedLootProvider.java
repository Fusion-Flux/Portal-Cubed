package com.fusionflux.portalcubed.data;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

public abstract class PortalCubedLootProvider extends SimpleFabricLootTableProvider {
    protected final Map<ResourceLocation, Builder> tables = new HashMap<>();

    public PortalCubedLootProvider(FabricDataOutput output, LootContextParamSet lootContextType) {
        super(output, lootContextType);
    }

    @Override
    public void generate(BiConsumer<ResourceLocation, Builder> out) {
        buildLootTables();
        tables.forEach(out);
    }

    public abstract void buildLootTables();
}
