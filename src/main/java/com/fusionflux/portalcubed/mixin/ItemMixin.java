package com.fusionflux.portalcubed.mixin;

import com.fusionflux.portalcubed.PortalCubed;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Shadow public abstract String getDescriptionId();

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    private void portalCubedTooltip(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced, CallbackInfo ci) {
        //noinspection ConstantValue
        if (
            !((Object)this instanceof BlockItem) &&
                BuiltInRegistries.ITEM.getKey((Item)(Object)this).getNamespace().equals(PortalCubed.MOD_ID)
        ) {
            final String key = getDescriptionId() + ".tooltip";
            if (Language.getInstance().has(key)) {
                tooltipComponents.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
