package com.fusionflux.portalcubed.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrowbarItem extends Item {
    private static final Multimap<EntityAttribute, EntityAttributeModifier> ATTRIBUTE_MODIFIERS =
        ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
            .put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(
                    ATTACK_DAMAGE_MODIFIER_ID,
                    "Weapon modifier",
                    ((SwordItem)Items.IRON_SWORD).getAttackDamage(),
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
            .put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID,
                    "Weapon modifier",
                    -2.4f,
                    EntityAttributeModifier.Operation.ADDITION
                )
            )
            .build();

    public CrowbarItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        // We spawn a decal instead of mining
        return false;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? ATTRIBUTE_MODIFIERS : super.getAttributeModifiers(slot);
    }
}
