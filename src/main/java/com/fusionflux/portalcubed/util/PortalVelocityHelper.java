package com.fusionflux.portalcubed.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PortalVelocityHelper {
    public static Vec3d rotateVelocity(Vec3d Velocity, Direction EntryDirection, Direction ExitDirection) {
        if(EntryDirection == Direction.NORTH){
            if(ExitDirection == Direction.NORTH){
                return Velocity.multiply(1,1,-1);
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(-Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getX(),-Velocity.getZ(),Velocity.getY());
            }

        }
        if(EntryDirection == Direction.SOUTH){
            if(ExitDirection == Direction.SOUTH){
                return Velocity.multiply(1,1,-1);
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(-Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getX(),-Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
        }
        if(EntryDirection == Direction.EAST){
            if(ExitDirection == Direction.EAST){
                return Velocity.multiply(-1,1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),-Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getY(),-Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.WEST){
            if(ExitDirection == Direction.WEST){
                return Velocity.multiply(-1,1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),-Velocity.getX());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getY(),-Velocity.getX(),Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.UP){
            if(ExitDirection == Direction.UP){
                return Velocity.multiply(1,-1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),-Velocity.getY());
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(-Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.DOWN){
            if(ExitDirection == Direction.DOWN){
                return Velocity.multiply(1,-1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),-Velocity.getY());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(-Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
        }
        return Velocity;
    }


    public static Vec3d rotatePosition(Vec3d Velocity,double EntityHeight , Direction EntryDirection, Direction ExitDirection) {
        if(EntryDirection == Direction.NORTH){
            if(ExitDirection == Direction.NORTH){
                return Velocity.multiply(1,1,-1);
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(-Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getX(),-Velocity.getZ(),Velocity.getY());
            }

        }
        if(EntryDirection == Direction.SOUTH){
            if(ExitDirection == Direction.SOUTH){
                return Velocity.multiply(1,1,-1);
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(-Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getX(),-Velocity.getZ(),Velocity.getY());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getX(),Velocity.getZ(),Velocity.getY());
            }
        }
        if(EntryDirection == Direction.EAST){
            if(ExitDirection == Direction.EAST){
                return Velocity.multiply(-1,1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),-Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getY(),-Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.WEST){
            if(ExitDirection == Direction.WEST){
                return Velocity.multiply(-1,1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),-Velocity.getX());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getZ(),Velocity.getY(),Velocity.getX());
            }
            if(ExitDirection == Direction.UP){
                return new Vec3d(Velocity.getY(),Velocity.getX(),Velocity.getZ());
            }
            if(ExitDirection == Direction.DOWN){
                return new Vec3d(Velocity.getY(),-Velocity.getX(),Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.UP){
            if(ExitDirection == Direction.UP){
                return Velocity.multiply(1,-1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getX(),EntityHeight,Velocity.getY());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getX(),EntityHeight,-Velocity.getY());
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(-Velocity.getY(),EntityHeight,Velocity.getZ());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(Velocity.getY(),EntityHeight,Velocity.getZ());
            }
        }
        if(EntryDirection == Direction.DOWN){
            if(ExitDirection == Direction.DOWN){
                return Velocity.multiply(1,-1,1);
            }
            if(ExitDirection == Direction.NORTH){
                return new Vec3d(Velocity.getX(),EntityHeight,-Velocity.getY());
            }
            if(ExitDirection == Direction.SOUTH){
                return new Vec3d(Velocity.getX(),EntityHeight,Velocity.getY());
            }
            if(ExitDirection == Direction.EAST){
                return new Vec3d(Velocity.getY(),EntityHeight,Velocity.getZ());
            }
            if(ExitDirection == Direction.WEST){
                return new Vec3d(-Velocity.getY(),EntityHeight,Velocity.getZ());
            }
        }
        return Velocity;
    }


    public static float yawAddition(Direction EntryDirection, Direction ExitDirection) {
        if(EntryDirection == Direction.NORTH){
            if(ExitDirection == Direction.NORTH){
                return 180;
            }
            if(ExitDirection == Direction.EAST){
                return -90;
            }
            if(ExitDirection == Direction.WEST){
                return 90;
            }

        }
        if(EntryDirection == Direction.SOUTH){
            if(ExitDirection == Direction.SOUTH){
                return 180;
            }
            if(ExitDirection == Direction.EAST){
                return 90;
            }
            if(ExitDirection == Direction.WEST){
                return -90;
            }
        }
        if(EntryDirection == Direction.EAST){
            if(ExitDirection == Direction.EAST){
                return 180;
            }
            if(ExitDirection == Direction.NORTH){
                return 90;
            }
            if(ExitDirection == Direction.SOUTH){
                return -90;
            }
        }
        if(EntryDirection == Direction.WEST){
            if(ExitDirection == Direction.WEST){
                return 180;
            }
            if(ExitDirection == Direction.NORTH){
                return -90;
            }
            if(ExitDirection == Direction.SOUTH){
                return 90;
            }
        }
        return 0;
    }

}
