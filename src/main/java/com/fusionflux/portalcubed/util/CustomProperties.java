package com.fusionflux.portalcubed.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

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
    public static final DirectionProperty HORIFACING;

    static {
        RUP = BooleanProperty.create("rup");
        RDOWN = BooleanProperty.create("rdown");
        RNORTH = BooleanProperty.create("rnorth");
        REAST = BooleanProperty.create("reast");
        RSOUTH = BooleanProperty.create("rsouth");
        RWEST = BooleanProperty.create("rwest");
        REVERSED = BooleanProperty.create("reversed");
        REFLECT = BooleanProperty.create("reflect");
        HFACINGUP = DirectionProperty.create("hfacingup", Direction.Plane.HORIZONTAL);
        HFACINGDOWN = DirectionProperty.create("hfacingdown", Direction.Plane.HORIZONTAL);
        HORIFACING = DirectionProperty.create("horifacing", Direction.Plane.HORIZONTAL);
    }

}
