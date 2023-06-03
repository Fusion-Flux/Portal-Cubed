package com.fusionflux.portalcubed.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CrowbarItem extends Item {
    private static final Multimap<Attribute, AttributeModifier> ATTRIBUTE_MODIFIERS =
        ImmutableMultimap.<Attribute, AttributeModifier>builder()
            .put(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                    BASE_ATTACK_DAMAGE_UUID,
                    "Weapon modifier",
                    ((SwordItem)Items.IRON_SWORD).getDamage(),
                    AttributeModifier.Operation.ADDITION
                )
            )
            .put(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                    BASE_ATTACK_SPEED_UUID,
                    "Weapon modifier",
                    -2.4f,
                    AttributeModifier.Operation.ADDITION
                )
            )
            .build();

    public CrowbarItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        // We spawn a decal instead of mining
        return false;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? ATTRIBUTE_MODIFIERS : super.getDefaultAttributeModifiers(slot);
    }
}
