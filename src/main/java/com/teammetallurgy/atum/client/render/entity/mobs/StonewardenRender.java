package com.teammetallurgy.atum.client.render.entity.mobs;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.teammetallurgy.atum.Atum;
import com.teammetallurgy.atum.client.ClientHandler;
import com.teammetallurgy.atum.client.model.entity.StonewardenModel;
import com.teammetallurgy.atum.entity.stone.StonewardenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class StonewardenRender extends MobRenderer<StonewardenEntity, StonewardenModel<StonewardenEntity>> {
    private static final Map<Integer, ResourceLocation> CACHE = Maps.newHashMap();

    public StonewardenRender(EntityRendererProvider.Context context) {
        super(context, new StonewardenModel<>(context.bakeLayer(ClientHandler.STONEWARDEN)), 0.5F);
    }

    @Override
    protected void setupRotations(@Nonnull StonewardenEntity stonewarden, @Nonnull PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(stonewarden, poseStack, ageInTicks, rotationYaw, partialTicks);
        if (!((double) stonewarden.walkAnimation.speed() >= 0.01D)) {
            float f1 = stonewarden.walkAnimation.position(partialTicks) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }
    }

    @Override
    @Nonnull
    public ResourceLocation getTextureLocation(@Nonnull StonewardenEntity stonewarden) {
        ResourceLocation location = CACHE.get(stonewarden.getVariant());

        if (location == null) {
            location = new ResourceLocation(Atum.MOD_ID, "textures/entity/stonewarden_" + stonewarden.getVariant() + ".png");
            CACHE.put(stonewarden.getVariant(), location);
        }
        return location;
    }
}