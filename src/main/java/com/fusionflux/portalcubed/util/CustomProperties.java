package com.fusionflux.portalcubed.util;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class CustomProperties {

    public static final BooleanProperty RUP;
    public static final BooleanProperty RDOWN;
    public static final BooleanProperty RNORTH;
    public static final BooleanProperty REAST;
    public static final BooleanProperty RSOUTH;
    public static final BooleanProperty RWEST;
    public static final BooleanProperty REVERSED;
    public static final BooleanProperty REFLECT;
    public static final DirectionProperty HFACINGUP;
    public static final DirectionProperty HFACINGDOWN;
    public static final DirectionProperty HORIZIONTALFACING;

    static {
        RUP = BooleanProperty.of("rup");
        RDOWN = BooleanProperty.of("rdown");
        RNORTH = BooleanProperty.of("rnorth");
        REAST = BooleanProperty.of("reast");
        RSOUTH = BooleanProperty.of("rsouth");
        RWEST = BooleanProperty.of("rwest");
        REVERSED = BooleanProperty.of("reversed");
        REFLECT = BooleanProperty.of("reflect");
        HFACINGUP = DirectionProperty.of("hfacingup", Direction.Type.HORIZONTAL);
        HFACINGDOWN = DirectionProperty.of("hfacingdown", Direction.Type.HORIZONTAL);
        HORIZIONTALFACING = DirectionProperty.of("horizontalffacing", Direction.Type.HORIZONTAL);
    }

}
