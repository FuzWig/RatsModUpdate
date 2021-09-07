package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.client.model.ModelRatGolemMount;
import com.github.alexthe666.rats.server.entity.EntityRatGolemMount;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class RenderRatGolemMount extends MobRenderer<EntityRatGolemMount, ModelRatGolemMount<EntityRatGolemMount>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("rats:textures/entity/rat/mount/golem_mount.png");

    public RenderRatGolemMount() {
        super(Minecraft.getInstance().getRenderManager(), new ModelRatGolemMount(), 0.75F);
        this.addLayer(new LayerRatGolemMountCracks(this));

    }

    public ResourceLocation getEntityTexture(EntityRatGolemMount entity) {
        return TEXTURE;
    }

    protected void preRenderCallback(EntityRatGolemMount rat, MatrixStack matrixStackIn, float partialTickTime) {
        super.preRenderCallback(rat, matrixStackIn, partialTickTime);

    }
}