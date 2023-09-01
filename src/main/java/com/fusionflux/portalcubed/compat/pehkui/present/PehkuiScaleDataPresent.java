package com.fusionflux.portalcubed.compat.pehkui.present;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleData;
import virtuoel.pehkui.api.ScaleData;

record PehkuiScaleDataPresent(ScaleData inner) implements PehkuiScaleData {
	@Override
	public double getScale() {
		return inner.getScale();
	}
}
