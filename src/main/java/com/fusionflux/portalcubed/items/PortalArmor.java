package com.fusionflux.portalcubed.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.minecraft.ClientOnly;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalArmor implements ArmorMaterial {
    private static final ResourceLocation TEXTURE = id("textures/models/armor/portal_armor");
    private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[]{3, 6, 8, 3};

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getIndex()] * 37;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 15;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.NETHERITE_INGOT);
    }

    @Override
    public String getName() {
        return "portal_armor";
    }

    @Override
    public float getToughness() {
        return 3;
    }

    @Override
    public float getKnockbackResistance() {
        return 0F;
    }

    @NotNull
    @Override
    @ClientOnly
    public ResourceLocation getTexture() {
        return TEXTURE;
    }
}
