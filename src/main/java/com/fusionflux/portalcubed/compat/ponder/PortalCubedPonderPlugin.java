package com.fusionflux.portalcubed.compat.ponder;

import com.fusionflux.portalcubed.PortalCubed;
import com.fusionflux.portalcubed.items.PortalCubedItems;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.PonderPlugin;
import net.createmod.ponder.foundation.PonderRegistrationHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class PortalCubedPonderPlugin implements PonderPlugin {
	@Override
	@NotNull
	public String getModID() {
		return PortalCubed.MOD_ID;
	}

	@Override
	public void registerScenes() {
		PonderRegistrationHelper helper = new PonderRegistrationHelper(getModID());
		ResourceLocation portalGun = BuiltInRegistries.ITEM.getKey(PortalCubedItems.PORTAL_GUN);
		helper.addStoryBoard(portalGun, "test_ponder", TestScene::test);
	}

	public static void init() {
		PonderIndex.addPlugin(new PortalCubedPonderPlugin());
	}
}
