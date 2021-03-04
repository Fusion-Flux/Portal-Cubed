package com.fusionflux.fluxtech.compatability;

import com.fusionflux.fluxtech.config.FluxTechConfig2;


import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(FluxTechConfig2.class, parent).get();
    }
}