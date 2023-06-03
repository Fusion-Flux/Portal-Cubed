package com.fusionflux.portalcubed.fog;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FogPreset implements StringRepresentable {
    PORTAL_ESCAPE(64, 3500, 58, 82, 101),
    PORTAL_2_FAN(128, 3600, 16, 19, 22),
    PORTAL_2_GLADOS_INTRO(0, 3500, 39, 54, 63),
    PORTAL_2_TESTCHAMBER(128, 5000, 40, 53, 64),
    PORTAL_2_DESTROYED(128, 2500, 50, 70, 80),
    PORTAL_2_DESTROYED_B(128, 5000, 50, 70, 80),
    PORTAL_2_BTS(1, 5000, 56, 95, 141),
    PORTAL_2_MINES(0, 6000, 70, 85, 100),
    PORTAL_2_BOTTOMLESS_PIT_FALLING(0, 3000, 3, 6, 8),
    PORTAL_2_BOTTOMLESS_PIT(0, 6000, 70, 85, 100),
    PORTAL_2_UNDERGROUND(0, 4500, 37, 35, 33),
    PORTAL_2_LAKE_B(0, 4500, 70, 85, 100),
    PORTAL_2_TUBERIDE(128, 5500, 120, 155, 170),
    PORTAL_2_WHEATLEY_Z(10000, 11000, 0, 0, 0),
    PORTAL_2_LAKE(0, 10000, 70, 85, 100),
    PORTAL_2_DARKNESS(1, 2500, 14, 20, 22),
    PORTAL_2_JAILBREAK(128, 4000, 100, 140, 160),
    PORTAL_2_ACT4_01(128, 2500, 50, 70, 80),
    PORTAL_2_ACT4_02(128, 3500, 50, 70, 80),
    PORTAL_2_ACT4_03(64, 6000, 50, 70, 80);

    private final String id = name().toLowerCase(Locale.ROOT);
    private final FogSettings settings;

    FogPreset(int start, int end, int r, int g, int b) {
        this.settings = new FogSettings(start / 64f, end / 64f, new FogSettings.Color(r, g, b), FogSettings.Shape.SPHERE);
    }

    @Override
    public String getSerializedName() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    public FogSettings getSettings() {
        return settings;
    }
}
