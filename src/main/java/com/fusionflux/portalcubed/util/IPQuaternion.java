package com.fusionflux.portalcubed.util;


import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

//This is from https://github.com/qouteall/ImmersivePortalsMod/blob/1.18/q_misc_util/src/main/java/qouteall/q_misc_util/my_util/DQuaternion.java,
public record IPQuaternion(double x, double y, double z, double w) {

    /**
     * @return the axis that the rotation is being performed along
     */
    public Vec3d getRotatingAxis() {
        return new Vec3d(x, y, z).normalize();
    }

    public double getRotatingAngleRadians() {
        return Math.acos(w) * 2;
    }

    public double getRotatingAngleDegrees() {
        return Math.toDegrees(getRotatingAngleRadians());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IPQuaternion that = (IPQuaternion)o;
        return Double.compare(that.x, x) == 0 &&
            Double.compare(that.y, y) == 0 &&
            Double.compare(that.z, z) == 0 &&
            Double.compare(that.w, w) == 0;
    }

    @Override
    public String toString() {
        Vec3d rotatingAxis = getRotatingAxis();
        return String.format("Rotates %.3f degrees along (%.3f %.3f %.3f) Quaternion:(%.3f %.3f %.3f %.3f)",
                             getRotatingAngleDegrees(), rotatingAxis.x, rotatingAxis.y, rotatingAxis.z, x, y, z, w
        );
    }

    public static Pair<Double, Double> getPitchYawFromRotation(IPQuaternion quaternion) {
        double x = quaternion.x();
        double y = quaternion.y();
        double z = quaternion.z();
        double w = quaternion.w();

        double cosYaw = 2 * (y * y + z * z) - 1;
        double sinYaw = -(x * z + y * w) * 2;

        double cosPitch = 1 - 2 * (x * x + z * z);
        double sinPitch = (x * w + y * z) * 2;

        return new Pair<>(
            Math.toDegrees(Math.atan2(sinPitch, cosPitch)),
            Math.toDegrees(Math.atan2(sinYaw, cosYaw))
        );
    }

    // x, y, z are the 3 rows of the matrix
    // http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
    // only works if the matrix is rotation only
    public static IPQuaternion matrixToQuaternion(
        Vec3d x, Vec3d y, Vec3d z
    ) {
        double m00 = x.getX();
        double m11 = y.getY();
        double m22 = z.getZ();

        double m12 = z.getY();
        double m21 = y.getZ();

        double m20 = x.getZ();
        double m02 = z.getX();

        double m01 = y.getX();
        double m10 = x.getY();

        double tr = m00 + m11 + m22;

        double qx, qy, qz, qw;

        if (tr > 0) {
            double S = Math.sqrt(tr + 1.0) * 2; // S=4*qw
            qw = 0.25 * S;
            qx = (m21 - m12) / S;
            qy = (m02 - m20) / S;
            qz = (m10 - m01) / S;
        } else if ((m00 > m11) && (m00 > m22)) {
            double S = Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx
            qw = (m21 - m12) / S;
            qx = 0.25 * S;
            qy = (m01 + m10) / S;
            qz = (m02 + m20) / S;
        } else if (m11 > m22) {
            double S = Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
            qw = (m02 - m20) / S;
            qx = (m01 + m10) / S;
            qy = 0.25 * S;
            qz = (m12 + m21) / S;
        } else {
            double S = Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
            qw = (m10 - m01) / S;
            qx = (m02 + m20) / S;
            qy = (m12 + m21) / S;
            qz = 0.25 * S;
        }

        return new IPQuaternion(qx, qy, qz, qw);
    }

}
