package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.HardLightBridgeEmitterBlock;
import com.fusionflux.portalcubed.PortalCubedConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author sailKite
 * @author FusionFlux
 * <p>
 * Handles the operating logic for the {@link HardLightBridgeEmitterBlock} and their associated bridges.
 */
public abstract class AbstractExcursionFunnelEmitterBlockEntity extends BlockEntity {

    public final int maxRange = PortalCubedConfig.maxBridgeLength;

    public Set<BlockPos> funnels;
    public Set<BlockPos> portalFunnels;

    public AbstractExcursionFunnelEmitterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.funnels = new HashSet<>();
        this.portalFunnels = new HashSet<>();
    }


    public void playSound(SoundEvent soundEvent) {
        assert this.level != null;
        this.level.playSound(null, this.worldPosition, soundEvent, SoundSource.BLOCKS, 0.1F, 3.0F);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        List<Integer> posXList = new ArrayList<>();
        List<Integer> posYList = new ArrayList<>();
        List<Integer> posZList = new ArrayList<>();

        for (BlockPos pos : funnels) {
            posXList.add(pos.getX());
            posYList.add(pos.getY());
            posZList.add(pos.getZ());
        }

        tag.putIntArray("xList", posXList);
        tag.putIntArray("yList", posYList);
        tag.putIntArray("zList", posZList);

        List<Integer> portalXList = new ArrayList<>();
        List<Integer> portalYList = new ArrayList<>();
        List<Integer> portalZList = new ArrayList<>();

        for (BlockPos pos : portalFunnels) {
            portalXList.add(pos.getX());
            portalYList.add(pos.getY());
            portalZList.add(pos.getZ());
        }

        tag.putIntArray("portalxList", portalXList);
        tag.putIntArray("portalyList", portalYList);
        tag.putIntArray("portalzList", portalZList);

        tag.putInt("pSize", portalFunnels.size());
        tag.putInt("size", funnels.size());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        List<Integer> posXList;
        List<Integer> posYList;
        List<Integer> posZList;

        posXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("xList")));
        posYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("yList")));
        posZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("zList")));

        int size = tag.getInt("size");

        if (!funnels.isEmpty())
            funnels.clear();

        for (int i = 0; i < size; i++) {
            funnels.add(new BlockPos.MutableBlockPos(posXList.get(i), posYList.get(i), posZList.get(i)));
        }

        List<Integer> portalXList;
        List<Integer> portalYList;
        List<Integer> portalZList;

        portalXList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalxList")));
        portalYList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalyList")));
        portalZList = Arrays.asList(ArrayUtils.toObject(tag.getIntArray("portalzList")));

        int pSize = tag.getInt("pSize");

        if (!portalFunnels.isEmpty())
            portalFunnels.clear();

        for (int i = 0; i < pSize; i++) {
            portalFunnels.add(new BlockPos.MutableBlockPos(portalXList.get(i), portalYList.get(i), portalZList.get(i)));
        }
    }

    public void togglePowered(BlockState state) {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, state.cycle(BlockStateProperties.POWERED));
        if (level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED)) {
            this.playSound(SoundEvents.BEACON_ACTIVATE);
        }
        if (!level.getBlockState(worldPosition).getValue(BlockStateProperties.POWERED)) {
            this.playSound(SoundEvents.BEACON_DEACTIVATE);
        }
    }
}
