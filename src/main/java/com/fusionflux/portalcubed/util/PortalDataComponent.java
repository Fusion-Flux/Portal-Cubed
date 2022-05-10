package com.fusionflux.portalcubed.util;

import com.fusionflux.portalcubed.entity.ExperimentalPortal;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.andrew.gravitychanger.util.GravityChangerComponents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PortalDataComponent implements CustomPortalDataComponent, AutoSyncedComponent {
    Vec3d axisW=null;
    Vec3d axisH=null;
    Vec3d destination=null;
    Vec3d facing=Vec3d.ZERO;
    private final ExperimentalPortal entity;

    public PortalDataComponent(ExperimentalPortal entity) {
        this.entity = entity;
    }

    @Override
    public Vec3d getAxisW() {
        return axisW;
    }

    @Override
    public Vec3d getAxisH() {
        return axisH;
    }

    @Override
    public Vec3d getDestination() {
        return destination;
    }

    @Override
    public void setDestination(Vec3d Destination) {
        destination = Destination;
        PortalCubedComponents.PORTAL_DATA.sync(entity);
    }

    @Override
    public Vec3d getOtherFacing() {
        return facing;
    }

    @Override
    public void setOtherFacing(Vec3d Facing) {
        facing = Facing;
        PortalCubedComponents.PORTAL_DATA.sync(entity);
    }

    @Override
    public void teleportEntity(Vec3d TeleportTo, Entity TeleportedEntity, ExperimentalPortal OtherPortal) {
        //if(entity.getFacingDirection() != Direction.UP && entity.getFacingDirection() != Direction.DOWN) {
        Vec3d TeleportLocation = OtherPortal.getPos();
        if(OtherPortal.getFacingDirection() == Direction.NORTH){
            TeleportLocation.subtract(0,0,3);
        }
        if(OtherPortal.getFacingDirection() == Direction.SOUTH){
            TeleportLocation.add(0,0,3);
        }
        if(OtherPortal.getFacingDirection() == Direction.WEST){
            TeleportLocation.subtract(3,0,0);
        }
        if(OtherPortal.getFacingDirection() == Direction.EAST){
            TeleportLocation.add(3,0,0);
        }
            TeleportedEntity.teleport(TeleportLocation.getX(), TeleportLocation.getY() - 1, TeleportLocation.getZ());
       // }
        //if(entity.getFacingDirection() == Direction.UP && entity.getFacingDirection() == Direction.DOWN) {
        //    TeleportTo.add(OtherPortal.getFacingDirection().getOpposite().getVector().getX(), OtherPortal.getFacingDirection().getOpposite().getVector().getY(),OtherPortal.getFacingDirection().getOpposite().getVector().getZ());
        //    TeleportedEntity.teleport(TeleportTo.getX(), TeleportTo.getY() - 1, TeleportTo.getZ());
        //}
        //entity.teleport();
        PortalCubedComponents.PORTAL_DATA.sync(entity);
    }


    @Override
    public void setOrientation(Vec3d AxisW,Vec3d AxisH) {
        axisW = AxisW;
        axisH = AxisH;
        entity.syncRotations();
        PortalCubedComponents.PORTAL_DATA.sync(entity);
    }




    @Override
    public void readFromNbt(NbtCompound tag) {
        this.setOrientation(IPHelperDuplicate.getVec3d(tag, "axisW").normalize(),IPHelperDuplicate.getVec3d(tag, "axisH").normalize());
        this.setDestination(IPHelperDuplicate.getVec3d(tag, "destination"));
        this.setOtherFacing(IPHelperDuplicate.getVec3d(tag, "facing"));
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        IPHelperDuplicate.putVec3d(tag, "axisW", this.getAxisW());
        IPHelperDuplicate.putVec3d(tag, "axisH", this.getAxisH());
        IPHelperDuplicate.putVec3d(tag, "destination", this.getDestination());
        IPHelperDuplicate.putVec3d(tag, "facing", this.getOtherFacing());
    }
}
