package com.fusionflux.portalcubed.fluids;

import com.fusionflux.portalcubed.mixin.DispenserBlockAccessor;
import com.google.common.base.Suppliers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.fusionflux.portalcubed.PortalCubed.id;

public class PortalCubedFluids {
    public static final FluidRegistryContainer TOXIC_GOO = createFluid(
        "toxic_goo",
        new ToxicGooFluid.Flowing(), new ToxicGooFluid.Still(),
        still -> new ToxicGooFluid.Block(still, QuiltBlockSettings.copy(Blocks.WATER).mapColor(MapColor.TERRACOTTA_GREEN))
    );

    private static FluidRegistryContainer createFluid(String name, FlowingFluid flowing, FlowingFluid still, Function<FlowingFluid, LiquidBlock> blockSupplier) {
        return new FluidRegistryContainer(name, flowing, still, blockSupplier, new BucketItem(still, new QuiltItemSettings().craftRemainder(Items.BUCKET).stacksTo(1)));
    }

    public static void registerFluids() {
        TOXIC_GOO.register();
    }

    public static class FluidRegistryContainer {
        public final String name;
        public final FlowingFluid flowing;
        public final FlowingFluid still;
        public final Item bucket;

        private final Supplier<LiquidBlock> block;

        private FluidRegistryContainer(String name, FlowingFluid flowing, FlowingFluid still, Function<FlowingFluid, LiquidBlock> blockSupplier, Item bucket) {
            this.name = name;
            this.flowing = flowing;
            this.still = still;
            this.bucket = bucket;
            block = Suppliers.memoize(() -> blockSupplier.apply(still));
        }

        private void register() {
            Registry.register(BuiltInRegistries.FLUID, id("flowing_" + name), flowing);
            Registry.register(BuiltInRegistries.FLUID, id(name), still);
            Registry.register(BuiltInRegistries.BLOCK, id(name), block.get());
            if (bucket != null) {
                Registry.register(BuiltInRegistries.ITEM, id(name + "_bucket"), bucket);
                DispenserBlock.registerBehavior(
                    bucket, ((DispenserBlockAccessor)Blocks.DISPENSER).invokeGetDispenseMethod(new ItemStack(Items.WATER_BUCKET))
                );
            }
        }

        public LiquidBlock getBlock() {
            return block.get();
        }
    }
}
