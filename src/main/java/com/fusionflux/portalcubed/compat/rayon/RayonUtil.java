package com.fusionflux.portalcubed.compat.rayon;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.Triangle;

import java.util.ArrayList;
import java.util.List;

public class RayonUtil {
	public static List<Triangle> getMeshOf(BoundingBox box, Vector3f offset) {
		final var x = box.getXExtent() * 0.5f;
		final var y = box.getYExtent() * 0.5f;
		final var z = box.getZExtent() * 0.5f;

		final var points = new Vector3f[] {
			// south
			new Vector3f(x, y, z), new Vector3f(-x, y, z), new Vector3f(0, 0, z),
			new Vector3f(-x, y, z), new Vector3f(-x, -y, z), new Vector3f(0, 0, z),
			new Vector3f(-x, -y, z), new Vector3f(x, -y, z), new Vector3f(0, 0, z),
			new Vector3f(x, -y, z), new Vector3f(x, y, z), new Vector3f(0, 0, z),

			// north
			new Vector3f(-x, y, -z), new Vector3f(x, y, -z), new Vector3f(0, 0, -z),
			new Vector3f(x, y, -z), new Vector3f(x, -y, -z), new Vector3f(0, 0, -z),
			new Vector3f(x, -y, -z), new Vector3f(-x, -y, -z), new Vector3f(0, 0, -z),
			new Vector3f(-x, -y, -z), new Vector3f(-x, y, -z), new Vector3f(0, 0, -z),

			// east
			new Vector3f(x, y, -z), new Vector3f(x, y, z), new Vector3f(x, 0, 0),
			new Vector3f(x, y, z), new Vector3f(x, -y, z), new Vector3f(x, 0, 0),
			new Vector3f(x, -y, z), new Vector3f(x, -y, -z), new Vector3f(x, 0, 0),
			new Vector3f(x, -y, -z), new Vector3f(x, y, -z), new Vector3f(x, 0, 0),

			// west
			new Vector3f(-x, y, z), new Vector3f(-x, y, -z), new Vector3f(-x, 0, 0),
			new Vector3f(-x, y, -z), new Vector3f(-x, -y, -z), new Vector3f(-x, 0, 0),
			new Vector3f(-x, -y, -z), new Vector3f(-x, -y, z), new Vector3f(-x, 0, 0),
			new Vector3f(-x, -y, z), new Vector3f(-x, y, z), new Vector3f(-x, 0, 0),

			// up
			new Vector3f(x, y, -z), new Vector3f(-x, y, -z), new Vector3f(0, y, 0),
			new Vector3f(-x, y, -z), new Vector3f(-x, y, z), new Vector3f(0, y, 0),
			new Vector3f(-x, y, z), new Vector3f(x, y, z), new Vector3f(0, y, 0),
			new Vector3f(x, y, z), new Vector3f(x, y, -z), new Vector3f(0, y, 0),

			// down
			new Vector3f(x, -y, z), new Vector3f(-x, -y, z), new Vector3f(0, -y, 0),
			new Vector3f(-x, -y, z), new Vector3f(-x, -y, -z), new Vector3f(0, -y, 0),
			new Vector3f(-x, -y, -z), new Vector3f(x, -y, -z), new Vector3f(0, -y, 0),
			new Vector3f(x, -y, -z), new Vector3f(x, -y, z), new Vector3f(0, -y, 0)
		};

		final var triangles = new ArrayList<Triangle>();

		for (int i = 0; i < points.length; i += 3) {
			triangles.add(new Triangle(
				points[i].add(offset),
				points[i + 1].add(offset),
				points[i + 2].add(offset)
			));
		}

		return triangles;
	}

	public static List<Triangle> getShiftedMeshOf(BoundingBox box) {
		return getMeshOf(box, new Vector3f(0, box.getYExtent() / 2, 0));
	}
}
