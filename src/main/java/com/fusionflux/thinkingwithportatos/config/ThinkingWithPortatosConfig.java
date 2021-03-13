package com.fusionflux.thinkingwithportatos.config;

import com.fusionflux.thinkingwithportatos.ThinkingWithPortatos;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;


@Config(name = ThinkingWithPortatos.MOD_ID)
public class ThinkingWithPortatosConfig implements ConfigData {
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("enabled")
    public Enabled enabled = new Enabled();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("numbers")
    public Numbers numbers = new Numbers();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("numbersblock")
    public NumbersBlock numbersblock = new NumbersBlock();

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
        public boolean enableLongFallBoots = true;
        public boolean enableGels = true;
        public boolean enablePortal2Blocks = true;
    }

    public static class Numbers {

    }

    public static class NumbersBlock {
        public int maxBridgeLength = 127;
    }
}

