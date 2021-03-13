package com.fusionflux.thinkingwithportatos.compatability;

import com.fusionflux.thinkingwithportatos.config.ThinkingWithPortatosConfig;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ThinkingWithPortatosConfig.class, parent).get();
    }
}