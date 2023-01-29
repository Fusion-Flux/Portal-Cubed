package com.fusionflux.portalcubed.compatability;

import com.fusionflux.portalcubed.config.PortalCubedConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> PortalCubedConfig.getScreen(parent, "portalcubed");
    }
}