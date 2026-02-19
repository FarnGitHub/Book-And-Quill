package farn.ender_pearl.item;

import farn.ender_pearl.mixin.ProjectileAccessor;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;

public class EnderPearlEntityRenderer extends ProjectileEntityRenderer {
    public EnderPearlEntityRenderer(int itemTextureId) {
        super(itemTextureId);
    }

    public void setTexture(int id) {
        ((ProjectileAccessor)this).farn_setTextureID(id);
    }
}
