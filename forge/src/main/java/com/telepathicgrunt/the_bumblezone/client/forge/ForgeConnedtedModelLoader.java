package com.telepathicgrunt.the_bumblezone.client.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.telepathicgrunt.the_bumblezone.client.bakedmodel.ConnectedBlockModel;
import com.telepathicgrunt.the_bumblezone.client.bakedmodel.ConnectedBlockModel.Connection;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.EnumMap;
import java.util.function.Function;

public class ForgeConnedtedModelLoader implements IGeometryLoader<ForgeConnedtedModelLoader.GeometryModel> {


    @Override
    public GeometryModel read(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
        JsonObject textures = GsonHelper.getAsJsonObject(json, "textures");


        return null;
    }

    public record GeometryModel(EnumMap<Connection, Material> textures) implements IUnbakedGeometry<GeometryModel> {

        @Override
        public BakedModel bake(IGeometryBakingContext iGeometryBakingContext, ModelBaker arg, Function<Material, TextureAtlasSprite> function, ModelState arg2, ItemOverrides arg3, ResourceLocation arg4) {
            return null;
        }
    }

    private static EnumMap<Connection, Material> makeMaterials(JsonObject json) {
        EnumMap<Connection, Material> map = new EnumMap<>(Connection.class);
        for (String key : json.keySet()) {
            Connection.tryParse(key)
                    .ifPresent(connection -> map.put(connection, create(json.get(key).getAsString())));
        }
        return map;
    }

    private static Material create(String id) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(id));
    }
}
