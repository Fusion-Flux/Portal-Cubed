package com.fusionflux.thinkingwithportatos.config;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;


@Config(name = ThinkingWithPortatos.MODID)
public class ThinkingWithPortatosConfig implements ConfigData {
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
        AutoConfig.register(ThinkingWithPortatosConfig.class, JanksonConfigSerializer::new);
    }

    public static ThinkingWithPortatosConfig get() {
        return AutoConfig.getConfigHolder(ThinkingWithPortatosConfig.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(ThinkingWithPortatosConfig.class).save();
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

