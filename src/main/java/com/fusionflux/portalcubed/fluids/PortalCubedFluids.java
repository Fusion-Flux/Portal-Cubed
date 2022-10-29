package com.fusionflux.portalcubed.fluids;

import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import com.fusionflux.portalcubed.PortalCubed;

import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;

import static com.fusionflux.portalcubed.PortalCubed.id;

import java.util.function.Function;

public class PortalCubedFluids {
    public static final FluidRegistryContainer TOXIC_GOO = createFluid(
        "toxic_goo",
        new ToxicGooFluid.Flowing(), new ToxicGooFluid.Still(),
        still -> new ToxicGooFluid.Block(still, QuiltBlockSettings.copy(Blocks.WATER))
    );

    private static FluidRegistryContainer createFluid(String name, FlowableFluid flowing, FlowableFluid still, Function<FlowableFluid, FluidBlock> blockSupplier) {
        return new FluidRegistryContainer(name, flowing, still, blockSupplier, new BucketItem(still, new QuiltItemSettings().group(PortalCubed.TestingElementsGroup).recipeRemainder(Items.BUCKET).maxCount(1)));
    }

    public static void registerFluids() {
        TOXIC_GOO.register();
    }

    public static class FluidRegistryContainer {
        public final String name;
        public final FlowableFluid flowing;
        public final FlowableFluid still;
        public final Item bucket;

        private final Function<FlowableFluid, FluidBlock> blockSupplier;
        private FluidBlock block;

        private FluidRegistryContainer(String name, FlowableFluid flowing, FlowableFluid still, Function<FlowableFluid, FluidBlock> blockSupplier, Item bucket) {
            this.name = name;
            this.flowing = flowing;
            this.still = still;
            this.blockSupplier = blockSupplier;
            this.bucket = bucket;
        }

        private void register() {
            Registry.register(Registry.FLUID, id("flowing_" + name), flowing);
            Registry.register(Registry.FLUID, id(name), still);
            Registry.register(Registry.BLOCK, id(name), block = blockSupplier.apply(still));
            if (bucket != null) Registry.register(Registry.ITEM, id(name + "_bucket"), bucket);
        }

        FluidBlock getBlock() {
            return block;
        }
    }
}
