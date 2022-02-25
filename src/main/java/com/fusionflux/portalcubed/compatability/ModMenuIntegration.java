package com.fusionflux.portalcubed.compatability;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(PortalCubedConfig.class, parent).get();
    }
}