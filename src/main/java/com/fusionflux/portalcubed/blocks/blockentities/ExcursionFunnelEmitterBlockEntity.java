package com.fusionflux.portalcubed.blocks.blockentities;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExcursionFunnelEmitterBlockEntity extends BlockEntity {
    private ToggleMode toggleMode = ToggleMode.FORWARD;

    public ExcursionFunnelEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(PortalCubedBlocks.EXCURSION_FUNNEL_EMITTER_ENTITY, pos, state);
    }

    public ToggleMode getToggleMode() {
        return toggleMode;
    }

    public void setToggleMode(ToggleMode mode) {
        this.toggleMode = mode;
        setChanged();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        tag.putString("toggleMode", toggleMode.name());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        this.toggleMode = NbtHelper.readEnum(tag, "toggleMode", toggleMode);
    }

    public enum ToggleMode {
        FORWARD(Mode.FORWARD_OFF, Mode.FORWARD_ON),
        REVERSE(Mode.REVERSED_OFF, Mode.REVERSED_ON),
        SWITCH(Mode.FORWARD_ON, Mode.REVERSED_ON);

        public final Mode off, on;

        ToggleMode(Mode off, Mode on) {
            this.off = off;
            this.on = on;
        }

        public ToggleMode next() {
            return switch (this) {
                case FORWARD -> REVERSE;
                case REVERSE -> SWITCH;
                case SWITCH -> FORWARD;
            };
        }
    }
}
