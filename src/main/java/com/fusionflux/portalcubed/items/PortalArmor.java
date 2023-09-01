package com.fusionflux.portalcubed.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
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
	public int getDurabilityForType(ArmorItem.Type type) {
		return BASE_DURABILITY[type.getSlot().getIndex()] * 37;
	}

	@Override
	public int getDefenseForType(ArmorItem.Type type) {
		return PROTECTION_VALUES[type.getSlot().getIndex()];
	}

	@Override
	public int getEnchantmentValue() {
		return 15;
	}

	@NotNull
	@Override
	public SoundEvent getEquipSound() {
		return SoundEvents.ARMOR_EQUIP_NETHERITE;
	}

	@NotNull
	@Override
	public Ingredient getRepairIngredient() {
		return Ingredient.of(Items.NETHERITE_INGOT);
	}

	@NotNull
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
