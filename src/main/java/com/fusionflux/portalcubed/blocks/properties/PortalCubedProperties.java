package com.fusionflux.portalcubed.blocks.properties;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class PortalCubedProperties {

    public static final BooleanProperty RUP = BooleanProperty.create("rup");
    public static final BooleanProperty RDOWN = BooleanProperty.create("rdown");
    public static final BooleanProperty RNORTH = BooleanProperty.create("rnorth");
    public static final BooleanProperty REAST = BooleanProperty.create("reast");
    public static final BooleanProperty RSOUTH = BooleanProperty.create("rsouth");
    public static final BooleanProperty RWEST = BooleanProperty.create("rwest");
    public static final BooleanProperty REVERSED = BooleanProperty.create("reversed");
    public static final BooleanProperty REFLECT = BooleanProperty.create("reflect");
    public static final DirectionProperty HFACINGUP = DirectionProperty.create("hfacingup", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty HFACINGDOWN = DirectionProperty.create("hfacingdown", Direction.Plane.HORIZONTAL);
    public static final DirectionProperty HORIFACING = DirectionProperty.create("horifacing", Direction.Plane.HORIZONTAL);
    public static final FluidTypeProperty LOGGING = FluidTypeProperty.create("logging");

}
