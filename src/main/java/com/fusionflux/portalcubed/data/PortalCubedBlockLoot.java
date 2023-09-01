package com.fusionflux.portalcubed.data;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;

public class PortalCubedBlockLoot extends PortalCubedLootProvider {
	public PortalCubedBlockLoot(FabricDataOutput output) {
		super(output, LootContextParamSets.BLOCK);
	}

	@Override
	public void buildLootTables() {
		dropSelfWhenProperty(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER, ExcursionFunnelEmitterBlock.QUADRANT, "1");
	}

	public void dropSelfWhenProperty(Block block, Property<?> property, String value) {
		tables.put(block.getLootTable(), LootTable.lootTable()
				.withPool(LootPool.lootPool()
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block)
								.setProperties(StatePropertiesPredicate.Builder.properties()
										.hasProperty(property, value)
								)
						)
						.add(LootItem.lootTableItem(block))
				)
		);
	}
}
