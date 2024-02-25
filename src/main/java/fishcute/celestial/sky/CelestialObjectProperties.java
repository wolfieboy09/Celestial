package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.CelestialExpression;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.MultiCelestialExpression;
import fishcute.celestial.util.Util;

public class CelestialObjectProperties {
    public final boolean hasMoonPhases;
    public final CelestialExpression moonPhase;
    public final boolean isSolid;
    public final CelestialExpression alpha;
    public final boolean blend;
    public final ColorEntry color;

    public float getRed() {
        return this.color == null ? 1.0F : (color.getStoredRed());
    }
    public float getGreen() {
        return this.color == null ? 1.0F : (color.getStoredGreen());
    }
    public float getBlue() {
        return this.color == null ? 1.0F : (color.getStoredBlue());
    }

    public CelestialObjectProperties(boolean hasMoonPhases, String moonPhase, boolean isSolid, String alpha, boolean blend, ColorEntry color, String location, MultiCelestialExpression.MultiDataModule... module) {
        this.hasMoonPhases = hasMoonPhases;
        this.moonPhase = Util.compileMultiExpression(moonPhase, location + ".moon_phase", module);
        this.isSolid = isSolid;
        this.alpha = Util.compileMultiExpression(alpha, location + ".alpha", module);
        this.blend = blend;
        this.color = color;
    }
    public static CelestialObjectProperties createCelestialObjectPropertiesFromJson(JsonObject o, String dimension, String object, MultiCelestialExpression.MultiDataModule... module) {
        return new CelestialObjectProperties(
                Util.getOptionalBoolean(o, "has_moon_phases", false, Util.locationFormat(dimension, "properties")),
                Util.getOptionalString(o, "moon_phase", "moonPhase", Util.locationFormat(dimension, "properties")),
                Util.getOptionalBoolean(o, "is_solid", false, Util.locationFormat(dimension, "properties")),
                Util.getOptionalString(o, "alpha", "1", Util.locationFormat(dimension, "properties")),
                Util.getOptionalBoolean(o, "blend", true, Util.locationFormat(dimension, "properties")),
                ColorEntry.createColorEntry(o, Util.locationFormat(dimension, "objects/" + object, "properties"), "color", null, false, module),
                Util.locationFormat(dimension, "objects/" + object, "properties"),
                module
        );
    }
}
