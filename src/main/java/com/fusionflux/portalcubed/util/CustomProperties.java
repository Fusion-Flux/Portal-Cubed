package com.fusionflux.portalcubed.util;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public class CustomProperties {

    public static final BooleanProperty RUP;
    public static final BooleanProperty R_DOWN;
    public static final BooleanProperty R_NORTH;
    public static final BooleanProperty R_EAST;
    public static final BooleanProperty R_SOUTH;
    public static final BooleanProperty R_WEST;
    public static final BooleanProperty REVERSED;
    public static final BooleanProperty REFLECT;
    public static final DirectionProperty H_FACING_UP;
    public static final DirectionProperty H_FACING_DOWN;
    public static final DirectionProperty HORIZONTAL_FACING;

    static {
        RUP = BooleanProperty.of("rup");
        R_DOWN = BooleanProperty.of("rdown");
        R_NORTH = BooleanProperty.of("rnorth");
        R_EAST = BooleanProperty.of("reast");
        R_SOUTH = BooleanProperty.of("rsouth");
        R_WEST = BooleanProperty.of("rwest");
        REVERSED = BooleanProperty.of("reversed");
        REFLECT = BooleanProperty.of("reflect");
        H_FACING_UP = DirectionProperty.of("hfacingup", Direction.Type.HORIZONTAL);
        H_FACING_DOWN = DirectionProperty.of("hfacingdown", Direction.Type.HORIZONTAL);
        HORIZONTAL_FACING = DirectionProperty.of("horifacing", Direction.Type.HORIZONTAL);
    }

}
