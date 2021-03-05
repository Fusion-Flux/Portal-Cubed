package com.fusionflux.fluxtech.world;

import com.google.common.collect.Maps;
import com.qouteall.immersive_portals.portal.Portal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class PortalsManager extends PersistentState {
    public static Map<String, Portal> portals = Maps.newHashMap();
    private final ServerWorld world;

    public PortalsManager(ServerWorld world) {
        super(nameFor(world.getDimension()));
        this.world = world;
        this.markDirty();
    }

    public static String nameFor(DimensionType dimensionType) {
        return "portals" + dimensionType.getSuffix();
    }

    public static Map<String, Portal> getPortals() {
        return portals;
    }

    private Map<String, Portal> getPortalsFromTag(CompoundTag tag) {
        ListTag listTag = tag.getList("portals", 10);
        Map<String, Portal> newData = new HashMap<>();

        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            Portal portal = readPortalFromTag(compoundTag);
            newData.put(compoundTag.getString("key"), portal);
        }

        return newData;
    }

    private Portal readPortalFromTag(CompoundTag compoundTag) {
        Portal portal = Portal.entityType.create(this.world);
        portal.fromTag(compoundTag);
        return portal;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        portals = getPortalsFromTag(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        ListTag portalsListTag = new ListTag();

        for (String key : portals.keySet()) {
            Portal portal = portals.get(key);
            CompoundTag portalTag = new CompoundTag();
            if (portal != null) {
                portal.toTag(portalTag);
                portalTag.putString("key", key);
                portalsListTag.add(portalTag);
            }
        }
        tag.put("portals", portalsListTag);
        return tag;
    }
}
