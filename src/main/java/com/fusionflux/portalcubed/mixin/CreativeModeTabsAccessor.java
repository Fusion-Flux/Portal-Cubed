package com.fusionflux.portalcubed.mixin;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeTabs.class)
public interface CreativeModeTabsAccessor {
	@Accessor
	@SuppressWarnings("checkstyle:MethodName")
	static void setCACHED_PARAMETERS(CreativeModeTab.ItemDisplayParameters cache) {
		throw new AssertionError();
	}
}
