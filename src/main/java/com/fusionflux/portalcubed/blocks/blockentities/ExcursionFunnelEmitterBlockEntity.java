package com.fusionflux.portalcubed.blocks.blockentities;

import java.util.UUID;

import com.fusionflux.portalcubed.blocks.PortalCubedBlocks;
import com.fusionflux.portalcubed.blocks.funnel.ExcursionFunnelEmitterBlock.Mode;
import com.fusionflux.portalcubed.entity.beams.EmittedEntity;
import com.fusionflux.portalcubed.util.NbtHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ExcursionFunnelEmitterBlockEntity extends BlockEntity {
	private ToggleMode toggleMode = ToggleMode.FORWARD;
	private UUID funnelEntityId = null;
	@Nullable
	private Float maxLength = null;

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

	@Nullable
	public UUID getFunnelEntityId() {
		return funnelEntityId;
	}

	public void setFunnelEntityId(UUID funnelEntityId) {
		this.funnelEntityId = funnelEntityId;
		setChanged();
	}

	public float getMaxLength() {
		return maxLength == null ? EmittedEntity.DEFAULT_MAX_LENGTH : maxLength;
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag tag) {
		tag.putString("toggleMode", toggleMode.getSerializedName());
		if (funnelEntityId != null)
			tag.putUUID("funnelEntityId", funnelEntityId);
		if (maxLength != null)
			tag.putFloat("maxLength", maxLength);
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		this.toggleMode = NbtHelper.readEnum(tag, "toggleMode", toggleMode);
		if (tag.contains("funnelEntityId", Tag.TAG_INT_ARRAY))
			this.funnelEntityId = tag.getUUID("funnelEntityId");
		if (tag.contains("maxLength", Tag.TAG_FLOAT))
			this.maxLength = tag.getFloat("maxLength");
	}

	public enum ToggleMode implements StringRepresentable {
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

		@Override
		@NotNull
		public String getSerializedName() {
			return name();
		}
	}
}
