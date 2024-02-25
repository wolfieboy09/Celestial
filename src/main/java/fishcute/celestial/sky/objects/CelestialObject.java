package fishcute.celestial.sky.objects;

import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialObjectProperties;
import fishcute.celestial.util.MultiCelestialExpression;
import fishcute.celestial.util.Util;
import fishcute.celestial.version.dependent.VRenderSystem;
import fishcute.celestial.version.dependent.util.BufferBuilderWrapper;
import fishcute.celestial.version.dependent.util.Matrix4fWrapper;
import fishcute.celestial.version.dependent.util.PoseStackWrapper;
import fishcute.celestial.version.dependent.util.ResourceLocationWrapper;

import java.util.ArrayList;

public class CelestialObject extends IBaseCelestialObject {
    public CelestialObject() {}
    public ResourceLocationWrapper texture;

    public CelestialObject(String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, CelestialObjectProperties properties, String parent, String dimension, String name, ArrayList<Util.VertexPoint> vertexList, MultiCelestialExpression.MultiDataModule multiDataModule) {
        super(scale, posX, posY, posZ, distance, degreesX, degreesY, degreesZ, baseDegreesX, baseDegreesY, baseDegreesZ, properties, parent, dimension, name, vertexList, multiDataModule);
        if (texturePath != null)
            this.texture = new ResourceLocationWrapper(texturePath);
    }

    @Override
    public CelestialObjectType getType() {
        return CelestialObjectType.DEFAULT;
    }

    @Override
    public void tick() {
        if (this.properties.color != null) {
            this.properties.color.updateColor();
        }
    }

    @Override
    public void renderObject(BufferBuilderWrapper bufferBuilder, PoseStackWrapper matrices, Matrix4fWrapper matrix4f2, float scale, float distance) {
        int moonPhase = this.properties.moonPhase.invokeInt();

        VRenderSystem.setShaderPositionTex();

        // Set texture
        if (this.texture != null)
            VRenderSystem.setShaderTexture(0, this.texture);

        float red = this.properties.getRed();
        float green = this.properties.getGreen();
        float blue = this.properties.getBlue();
        float alpha = this.properties.alpha.invoke();

        VRenderSystem.setShaderColor(red, green, blue, alpha);

        if (this.properties.hasMoonPhases) {
            int l = (moonPhase % 4);
            int i1 = (moonPhase / 4 % 2);
            float f13 = l / 4.0F;
            float f14 = i1 / 2.0F;
            float f15 = (l + 1) / 4.0F;
            float f16 = (i1 + 1) / 2.0F;
            bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                    f15, f16, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                    f13, f16, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                    f13, f14, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                    f15, f14, red, green, blue, alpha);
        } else if (this.vertexList != null && this.vertexList.size() > 0) {
            Util.VertexPointValue v;
            for (Util.VertexPoint vertexPoint : this.vertexList) {
                v = new Util.VertexPointValue(vertexPoint);
                bufferBuilder.vertexUv(matrix4f2, (float) v.pointX, (float) v.pointY, (float) v.pointZ,
                        (float) v.uvX, (float) v.uvY, red, green, blue, alpha);
            }
        } else {
            bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale),
                    0.0F, 0.0F, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? scale : -scale),
                    1.0F, 0.0F, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, scale, distance, (distance < 0 ? -scale : scale),
                    1.0F, 1.0F, red, green, blue, alpha);
            bufferBuilder.vertexUv(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale),
                    0.0F, 1.0F, red, green, blue, alpha);
        }
    }

    @Override
    public ICelestialObject createObjectFromJson(JsonObject o, String name, String dimension, PopulateObjectData.Module module) {
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        return new CelestialObject(
                Util.getOptionalTexture(o, "texture", null, Util.locationFormat(dimension, "objects/" + name, "")),
                Util.getOptionalString(display, "scale", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_x", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_y", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "pos_z", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(display, "distance", "0", Util.locationFormat(dimension, name, "display")),
                Util.getOptionalString(rotation, "degrees_x", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "degrees_z", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_x", "-90", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_y", "0", Util.locationFormat(dimension, name, "rotation")),
                Util.getOptionalString(rotation, "base_degrees_z", "-90", Util.locationFormat(dimension, name, "rotation")),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"), dimension, name, module),
                Util.getOptionalString(o, "parent", null, Util.locationFormat(dimension, name)),
                dimension,
                name,
                Util.convertToPointUvList(o, "vertex", Util.locationFormat(dimension, "objects/" + name, "vertex")),
                module
        );
    }

    @Override
    public void begin(BufferBuilderWrapper bufferBuilder) {
        bufferBuilder.beginObject();
    }

    @Override
    public void end(BufferBuilderWrapper bufferBuilder) {
        bufferBuilder.upload();
    }
}
