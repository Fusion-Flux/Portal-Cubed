package com.fusionflux.portalcubed.util;

import net.minecraft.state.property.BooleanProperty;

public class CustomProperties {

    public static final BooleanProperty RUP;
    public static final BooleanProperty RDOWN;
    public static final BooleanProperty RNORTH;
    public static final BooleanProperty REAST;
    public static final BooleanProperty RSOUTH;
    public static final BooleanProperty RWEST;
    public static final BooleanProperty REVERSED;


    static {
        RUP = BooleanProperty.of("rup");
        RDOWN = BooleanProperty.of("rdown");
        RNORTH = BooleanProperty.of("rnorth");
        REAST = BooleanProperty.of("reast");
        RSOUTH = BooleanProperty.of("rsouth");
        RWEST = BooleanProperty.of("rwest");
        REVERSED = BooleanProperty.of("reversed");
    }

}
