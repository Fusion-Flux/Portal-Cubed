package com.fusionflux.portalcubed.data;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import com.fusionflux.portalcubed.entity.PortalCubedEntities;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction.NameSource;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class PortalCubedEntityLoot extends PortalCubedLootProvider {
    public PortalCubedEntityLoot(FabricDataOutput output) {
        super(output, LootContextParamSets.ENTITY);
    }

    @Override
    public void buildLootTables() {
        keepName(PortalCubedEntities.STORAGE_CUBE, PortalCubedItems.STORAGE_CUBE);
        keepName(PortalCubedEntities.COMPANION_CUBE, PortalCubedItems.COMPANION_CUBE);
        keepName(PortalCubedEntities.REDIRECTION_CUBE, PortalCubedItems.REDIRECTION_CUBE);
        keepName(PortalCubedEntities.SCHRODINGER_CUBE, PortalCubedItems.SCHRODINGER_CUBE);
        keepName(PortalCubedEntities.RADIO, PortalCubedItems.RADIO);
        keepName(PortalCubedEntities.OLD_AP_CUBE, PortalCubedItems.OLD_AP_CUBE);
        keepName(PortalCubedEntities.PORTAL_1_COMPANION_CUBE, PortalCubedItems.PORTAL_1_COMPANION_CUBE);
        keepName(PortalCubedEntities.PORTAL_1_STORAGE_CUBE, PortalCubedItems.PORTAL_1_STORAGE_CUBE);
        keepName(PortalCubedEntities.BEANS, PortalCubedItems.BEANS);
        keepName(PortalCubedEntities.MUG, PortalCubedItems.MUG);
        keepName(PortalCubedEntities.JUG, PortalCubedItems.JUG);
        keepName(PortalCubedEntities.COMPUTER, PortalCubedItems.COMPUTER);
        keepName(PortalCubedEntities.CHAIR, PortalCubedItems.CHAIR);
        keepName(PortalCubedEntities.LIL_PINEAPPLE, PortalCubedItems.LIL_PINEAPPLE);
        keepName(PortalCubedEntities.HOOPY, PortalCubedItems.HOOPY);
        keepName(PortalCubedEntities.CORE_FRAME, PortalCubedItems.CORE_FRAME);
        keepName(PortalCubedEntities.ANGER_CORE, PortalCubedItems.ANGER_CORE);
        keepName(PortalCubedEntities.MORALITY_CORE, PortalCubedItems.MORALITY_CORE);
        keepName(PortalCubedEntities.CAKE_CORE, PortalCubedItems.CAKE_CORE);
        keepName(PortalCubedEntities.CURIOSITY_CORE, PortalCubedItems.CURIOSITY_CORE);
        keepName(PortalCubedEntities.SPACE_CORE, PortalCubedItems.SPACE_CORE);
        keepName(PortalCubedEntities.FACT_CORE, PortalCubedItems.FACT_CORE);
        keepName(PortalCubedEntities.ADVENTURE_CORE, PortalCubedItems.ADVENTURE_CORE);
        keepName(PortalCubedEntities.ENERGY_PELLET, PortalCubedItems.ENERGY_PELLET);
        keepName(PortalCubedEntities.TURRET, PortalCubedItems.TURRET);
    }

    public void add(EntityType<?> type, Builder builder) {
        tables.put(type.getDefaultLootTable(), builder);
    }

    public void keepName(EntityType<?> type, ItemLike item) {
        add(type, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(item)
                                .apply(CopyNameFunction.copyName(NameSource.THIS))
                        )
                )
        );
    }
}
