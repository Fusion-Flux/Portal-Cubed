package com.fusionflux.portalcubed.client;

// Redirection class for accessing client classes from common mixins
public class MixinPCClientAccessor {
    public static boolean allowCfg() {
        return PortalCubedClient.allowCfg;
    }
}
