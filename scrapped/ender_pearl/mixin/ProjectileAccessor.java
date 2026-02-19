package farn.ender_pearl.mixin;

import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ProjectileEntityRenderer.class)
public interface ProjectileAccessor {

    @Accessor("itemTextureId")
    void farn_setTextureID(int id);
}
