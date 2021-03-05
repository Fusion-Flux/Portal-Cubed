package com.fusionflux.fluxtech.config;

import com.fusionflux.fluxtech.FluxTech;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;


@Config(name = FluxTech.MOD_ID)
public class FluxTechConfig2 implements ConfigData {
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
        AutoConfig.register(FluxTechConfig2.class, JanksonConfigSerializer::new);
    }

    public static FluxTechConfig2 get() {
        return AutoConfig.getConfigHolder(FluxTechConfig2.class).getConfig();
    }

    public static void save() {
        AutoConfig.getConfigHolder(FluxTechConfig2.class).save();
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

