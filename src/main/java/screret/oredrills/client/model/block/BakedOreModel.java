package screret.oredrills.client.model.block;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;
import screret.oredrills.OreDrills;
import screret.oredrills.block.entity.BlockEntityOre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BakedOreModel implements IDynamicBakedModel {

    private TextureAtlasSprite getTexture() {
        ResourceLocation name = new ResourceLocation(OreDrills.MODID, "block/ore");
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(name);
    }

    private BakedQuad createQuad(Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, TextureAtlasSprite sprite) {
        Vector3d normal = v3.sub(v2).cross(v1.sub(v2)).normalize();

        QuadBakingVertexConsumer.Buffered quadBaker = new QuadBakingVertexConsumer.Buffered();
        quadBaker.setSprite(sprite);
        quadBaker.vertex((float) v1.x, (float) v1.y, (float) v1.z, 1.0f, 1.0f, 1.0f, 1.0f,0.0f, 16.0f, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BLOCK, (float) normal.x, (float) normal.y,(float) normal.z);
        quadBaker.vertex((float) v2.x, (float) v2.y, (float) v2.z, 1.0f, 1.0f, 1.0f, 1.0f,0.0f, 16.0f, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BLOCK, (float) normal.x, (float) normal.y,(float) normal.z);
        quadBaker.vertex((float) v3.x, (float) v3.y, (float) v3.z, 1.0f, 1.0f, 1.0f, 1.0f,16.0f, 16.0f, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BLOCK, (float) normal.x, (float) normal.y,(float) normal.z);
        quadBaker.vertex((float) v4.x, (float) v4.y, (float) v4.z, 1.0f, 1.0f, 1.0f, 1.0f,16.0f, 0.0f, OverlayTexture.NO_OVERLAY, LightTexture.FULL_BLOCK, (float) normal.x, (float) normal.y,(float) normal.z);
        quadBaker.endVertex();
        return quadBaker.getQuad();
    }

    private static Vector3d v(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }


    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        BlockState blockToCopy = extraData.get(BlockEntityOre.BLOCK_TO_COPY);
        if (blockToCopy != null) {
            ResourceLocation location = ModelLocationUtils.getModelLocation(blockToCopy.getBlock());
            if (location != null) {
                BakedModel model = Minecraft.getInstance().getModelManager().getModel(location);
                if (model != null) {
                    return model.getQuads(blockToCopy, side, rand, extraData, RenderType.cutoutMipped());
                }
            }
        }

        if (side != null) {
            return Collections.emptyList();
        }

        TextureAtlasSprite texture = getTexture();
        List<BakedQuad> quads = new ArrayList<>();
        double l = .2;
        double r = 1-.2;
        quads.add(createQuad(v(l, r, l), v(l, r, r), v(r, r, r), v(r, r, l), texture));
        quads.add(createQuad(v(l, l, l), v(r, l, l), v(r, l, r), v(l, l, r), texture));
        quads.add(createQuad(v(r, r, r), v(r, l, r), v(r, l, l), v(r, r, l), texture));
        quads.add(createQuad(v(l, r, l), v(l, l, l), v(l, l, r), v(l, r, r), texture));
        quads.add(createQuad(v(r, r, l), v(r, l, l), v(l, l, l), v(l, r, l), texture));
        quads.add(createQuad(v(l, r, r), v(l, l, r), v(r, l, r), v(r, r, r), texture));

        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getTexture();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
