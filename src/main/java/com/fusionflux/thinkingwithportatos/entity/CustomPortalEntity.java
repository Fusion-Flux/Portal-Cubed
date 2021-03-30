package com.fusionflux.thinkingwithportatos.entity;

import com.fusionflux.thinkingwithportatos.blocks.ThinkingWithPortatosBlocks;
import com.fusionflux.thinkingwithportatos.sound.ThinkingWithPortatosSounds;
import com.qouteall.immersive_portals.Helper;
import com.qouteall.immersive_portals.api.PortalAPI;
import com.qouteall.immersive_portals.my_util.SignalArged;
import com.qouteall.immersive_portals.my_util.SignalBiArged;
import com.qouteall.immersive_portals.portal.Portal;
import com.qouteall.immersive_portals.portal.PortalManipulation;
import com.qouteall.immersive_portals.teleportation.CollisionHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.World;

import java.util.UUID;

public class CustomPortalEntity extends Portal {

    public static EntityType<CustomPortalEntity> entityType;
    public static final SignalArged<CustomPortalEntity> clientPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> serverPortalTickSignal;
    public static final SignalArged<CustomPortalEntity> portalCacheUpdateSignal;
    public static final SignalArged<CustomPortalEntity> portalDisposeSignal;
    public static final SignalBiArged<CustomPortalEntity, CompoundTag> readPortalDataSignal;
    public static final SignalBiArged<CustomPortalEntity, CompoundTag> writePortalDataSignal;
    public static final TrackedData<String> STOREDSTRING = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.STRING);
    public static final TrackedData<Boolean> ISACTIVE = DataTracker.registerData(CustomPortalEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public CustomPortalEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STOREDSTRING, "null");
        this.getDataTracker().startTracking(ISACTIVE, false);
    }


    public String getString() {
        return getDataTracker().get(STOREDSTRING);
    }

    public void setString(String string) {
        this.getDataTracker().set(STOREDSTRING, string);
    }

    public Boolean getActive() {
        return getDataTracker().get(ISACTIVE);
    }

    public void setActive(Boolean active) {
        this.getDataTracker().set(ISACTIVE, active);
    }

    @Override
    public void tick() {
        if (this.world.isClient) {
            clientPortalTickSignal.emit(this);
        } else {
            if (!this.isPortalValid()) {
                Helper.log("removed invalid portal" + this);
                this.remove();
                return;
            }

            serverPortalTickSignal.emit(this);
        }

        CollisionHelper.notifyCollidingPortals(this);
        if (!this.world.isClient) {
            if ((this.world.getBlockState(this.getBlockPos()) != Blocks.AIR.getDefaultState())||(this.world.getBlockState(new BlockPos(
                    this.getPos().getX()-Math.abs(this.axisH.getX()),
                    this.getPos().getY()+this.axisH.getY(),
                    this.getPos().getZ()-Math.abs(this.axisH.getZ())))
                    != Blocks.AIR.getDefaultState())) {
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                System.out.println("killed");
                if(!this.getString().equals("null")){
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    otherPortal.setDestination(otherPortal.getPos());
                    PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                    otherPortal.setActive(false);
                    otherPortal.reloadAndSyncToClient();
                }
            }
            if (this.world.getBlockState(new BlockPos(
                    this.getPos().getX()-this.axisW.crossProduct(this.axisH).getX(),
                    this.getPos().getY()+this.axisW.crossProduct(this.axisH).getY(),
                    this.getPos().getZ()-this.axisW.crossProduct(this.axisH).getZ()))
                    == Blocks.AIR.getDefaultState()) {
                this.kill();
                world.playSound(null, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(), ThinkingWithPortatosSounds.ENTITY_PORTAL_CLOSE, SoundCategory.NEUTRAL, .1F, 1F);
                System.out.println("killed");
                if(!this.getString().equals("null")){
                    CustomPortalEntity otherPortal;
                    otherPortal = (CustomPortalEntity) ((ServerWorld) world).getEntity(UUID.fromString(this.getString()));
                    assert otherPortal != null;
                    otherPortal.setDestination(otherPortal.getPos());
                    PortalManipulation.adjustRotationToConnect(PortalAPI.createFlippedPortal(otherPortal), otherPortal);
                    otherPortal.setActive(false);
                    otherPortal.reloadAndSyncToClient();
                }
            }
        }
    }
    static {
        clientPortalTickSignal = new SignalArged();
        serverPortalTickSignal = new SignalArged();
        portalCacheUpdateSignal = new SignalArged();
        portalDisposeSignal = new SignalArged();
        readPortalDataSignal = new SignalBiArged();
        writePortalDataSignal = new SignalBiArged();
    }
}
