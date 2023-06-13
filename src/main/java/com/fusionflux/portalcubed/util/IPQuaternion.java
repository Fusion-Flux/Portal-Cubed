package com.fusionflux.portalcubed.util;


import net.minecraft.util.Tuple;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

//This is from https://github.com/qouteall/ImmersivePortalsMod/blob/1.18/q_misc_util/src/main/java/qouteall/q_misc_util/my_util/DQuaternion.java,
public class IPQuaternion {
    public final double x;
    public final double y;
    public final double z;
    public final double w;



    public IPQuaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getW() {
        return w;
    }

    public static IPQuaternion rotationByRadians(
            Vec3 axis,
            double rotationAngle
    ) {
        double s = Math.sin(rotationAngle / 2.0F);
        return new IPQuaternion(
                axis.x * s,
                axis.y * s,
                axis.z * s,
                Math.cos(rotationAngle / 2.0F)
        );
    }

    /**
     * @return the axis that the rotation is being performed along
     */
    public Vec3 getRotatingAxis() {
        return new Vec3(x, y, z).normalize();
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
        Vec3 rotatingAxis = getRotatingAxis();
        return String.format("Rotates %.3f degrees along (%.3f %.3f %.3f) Quaternion:(%.3f %.3f %.3f %.3f)",
                             getRotatingAngleDegrees(), rotatingAxis.x, rotatingAxis.y, rotatingAxis.z, x, y, z, w
        );
    }

    public IPQuaternion multiply(double val) {
        return new IPQuaternion(
                x * val, y * val, z * val, w * val
        );
    }

    /**
     * vector add
     */
    public IPQuaternion add(IPQuaternion q) {
        return new IPQuaternion(
                x + q.x, y + q.y, z + q.z, w + q.w
        );
    }

    public Vec3 rotate(Vec3 vec) {
        IPQuaternion result = this.hamiltonProduct(new IPQuaternion(vec.x, vec.y, vec.z, 0)).hamiltonProduct(getConjugated());

        return new Vec3(result.x, result.y, result.z);
    }

    public IPQuaternion getConjugated() {
        return new IPQuaternion(
                -x, -y, -z, w
        );
    }

    public IPQuaternion hamiltonProduct(IPQuaternion other) {
        double x1 = this.getX();
        double y1 = this.getY();
        double z1 = this.getZ();
        double w1 = this.getW();
        double x2 = other.getX();
        double y2 = other.getY();
        double z2 = other.getZ();
        double w2 = other.getW();
        return new IPQuaternion(
                w1 * x2 + x1 * w2 + y1 * z2 - z1 * y2,
                w1 * y2 - x1 * z2 + y1 * w2 + z1 * x2,
                w1 * z2 + x1 * y2 - y1 * x2 + z1 * w2,
                w1 * w2 - x1 * x2 - y1 * y2 - z1 * z2
        );
    }

    public static Tuple<Double, Double> getPitchYawFromRotation(IPQuaternion quaternion) {
        double x = quaternion.getX();
        double y = quaternion.getY();
        double z = quaternion.getZ();
        double w = quaternion.getW();

        double cosYaw = 2 * (y * y + z * z) - 1;
        double sinYaw = -(x * z + y * w) * 2;

        double cosPitch = 1 - 2 * (x * x + z * z);
        double sinPitch = (x * w + y * z) * 2;

        return new Tuple<>(
            Math.toDegrees(Math.atan2(sinPitch, cosPitch)),
            Math.toDegrees(Math.atan2(sinYaw, cosYaw))
        );
    }


    // x, y, z are the 3 rows of the matrix
    // http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
    // only works if the matrix is rotation only
    public static IPQuaternion matrixToQuaternion(
        Vec3 x, Vec3 y, Vec3 z
    ) {
        double m00 = x.x();
        double m11 = y.y();
        double m22 = z.z();

        double m12 = z.y();
        double m21 = y.z();

        double m20 = x.z();
        double m02 = z.x();

        double m01 = y.x();
        double m10 = x.y();

        double tr = m00 + m11 + m22;

        double qx, qy, qz, qw;

        if (tr > 0) {
            double s = Math.sqrt(tr + 1.0) * 2; // S=4*qw
            qw = 0.25 * s;
            qx = (m21 - m12) / s;
            qy = (m02 - m20) / s;
            qz = (m10 - m01) / s;
        } else if ((m00 > m11) && (m00 > m22)) {
            double s = Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx
            qw = (m21 - m12) / s;
            qx = 0.25 * s;
            qy = (m01 + m10) / s;
            qz = (m02 + m20) / s;
        } else if (m11 > m22) {
            double s = Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
            qw = (m02 - m20) / s;
            qx = (m01 + m10) / s;
            qy = 0.25 * s;
            qz = (m12 + m21) / s;
        } else {
            double s = Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
            qw = (m10 - m01) / s;
            qx = (m02 + m20) / s;
            qy = (m12 + m21) / s;
            qz = 0.25 * s;
        }

        return new IPQuaternion(qx, qy, qz, qw);
    }

    public static IPQuaternion getRotationBetween(Vec3 from, Vec3 to, Vec3 backup) {
        from = from.normalize();
        to = to.normalize();
        Vec3 axis = from.cross(to).normalize();
        double cos = from.dot(to);
        double angle = Math.acos(cos);

        if (Math.toDegrees(angle) == 180) {
            axis = backup;
        }
        return IPQuaternion.rotationByRadians(axis, angle);
    }

    public Quaternionf toQuaternionf() {
        return new Quaternionf(x, y, z, w);
    }
}
