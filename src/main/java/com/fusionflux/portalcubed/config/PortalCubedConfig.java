package com.fusionflux.portalcubed.config;

import com.fusionflux.portalcubed.PortalCubed;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;


@Config(name = PortalCubed.MODID)
public class PortalCubedConfig implements ConfigData {
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("enabled")
    public final Enabled enabled = new Enabled();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("numbersblock")
    public final NumbersBlock numbersblock = new NumbersBlock();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("numbers")
    public Numbers numbers = new Numbers();

    public static void register() {
        AutoConfig.register(PortalCubedConfig.class, JanksonConfigSerializer::new);
    }

    public static PortalCubedConfig get() {
        return AutoConfig.getConfigHolder(PortalCubedConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(PortalCubedConfig.class).save();
    }

    public static class Enabled {
        public final boolean enableLongFallBoots = true;
        public final boolean enableGels = true;
        public final boolean enablePortal2Blocks = true;
    }

    public static class Numbers {

    }

    public static class NumbersBlock {
        public final int maxBridgeLength = 127;
    }
}

