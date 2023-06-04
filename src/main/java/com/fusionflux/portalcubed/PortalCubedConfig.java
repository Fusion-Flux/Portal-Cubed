package com.fusionflux.portalcubed;

import com.fusionflux.portalcubed.client.render.portal.PortalRenderers;
import eu.midnightdust.lib.config.MidnightConfig;

@SuppressWarnings("CanBeFinal")
public class PortalCubedConfig extends MidnightConfig {

    @Entry @Client public static boolean enableRoundPortals = false;
    @Entry public static boolean enableAccurateMovement = true;
    @Entry public static int maxBridgeLength = 127;
    @Entry public static float fizzlerDamage = 35;
    @Entry public static float rocketDamage = 35;
    @Entry public static float pelletDamage = 35;
    @Entry public static float laserDamage = 3;
    @Entry @Client public static boolean portalHudMode = false;
    @Entry(min = 0, max = 100, isSlider = true) @Client public static int gelOverlayOpacity = 100;
    @Entry @Client public static boolean staticPortalItemDrops = true;
    @Entry @Client public static PortalRenderers renderer = PortalRenderers.DISABLED;
}
