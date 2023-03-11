package com.fusionflux.portalcubed.compat.pehkui.present;

import com.fusionflux.portalcubed.compat.pehkui.PehkuiScaleModifier;
import virtuoel.pehkui.api.ScaleModifier;

record PehkuiScaleModifierPresent(ScaleModifier inner) implements PehkuiScaleModifier {
}
