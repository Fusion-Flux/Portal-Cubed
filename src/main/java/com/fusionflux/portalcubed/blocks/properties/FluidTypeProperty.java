package com.fusionflux.portalcubed.blocks.properties;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class FluidTypeProperty extends Property<ResourceLocation> {
    public static final ResourceLocation EMPTY = new ResourceLocation("empty");

    private final BiMap<ResourceLocation, String> values = BuiltInRegistries.FLUID.keySet().stream()
        .collect(ImmutableBiMap.toImmutableBiMap(Function.identity(), this::getName));

    private FluidTypeProperty(String name) {
        super(name, ResourceLocation.class);
    }

    public static FluidTypeProperty create(String name) {
        return new FluidTypeProperty(name);
    }

    @NotNull
    @Override
    public Collection<ResourceLocation> getPossibleValues() {
        return values.keySet();
    }

    @NotNull
    @Override
    public String getName(ResourceLocation value) {
        return value.getNamespace() + "__" + value.getPath();
    }

    @NotNull
    @Override
    public Optional<ResourceLocation> getValue(String value) {
        return Optional.ofNullable(values.inverse().get(value));
    }

    @NotNull
    public static Fluid getFluid(ResourceLocation id) {
        return BuiltInRegistries.FLUID.get(id);
    }

    @NotNull
    public Fluid getFluid(BlockState state) {
        return getFluid(state.getValue(this));
    }
}
