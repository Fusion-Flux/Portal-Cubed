package com.fusionflux.portalcubed.blocks.properties;

import com.fusionflux.portalcubed.fluids.PortalCubedFluids;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum FluidType implements StringRepresentable {
    EMPTY(Fluids.EMPTY),
    WATER(Fluids.WATER),
    LAVA(Fluids.LAVA),
    TOXIC_GOO(PortalCubedFluids.TOXIC_GOO.still);

    private final String id = name().toLowerCase(Locale.ROOT);
    public final Fluid fluid;

    FluidType(Fluid fluid) {
        this.fluid = fluid;
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return id;
    }

    public static FluidType getByFluid(Fluid fluid) {
        for (final FluidType type : values()) {
            if (type.fluid == fluid) {
                return type;
            }
        }
        return null;
    }
}
