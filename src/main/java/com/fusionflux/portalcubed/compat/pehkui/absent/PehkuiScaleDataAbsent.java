package com.fusionflux.portalcubed.compat.pehkui.absent;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleData;

enum PehkuiScaleDataAbsent implements PehkuiScaleData {
	INSTANCE;

	@Override
	public double getScale() {
		return 1;
	}
}
