package vazkii.quark.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

@Mixin(EntityRenderers.class)
public interface RenderingRegistryAccessor {
	
    @Accessor(value = "INSTANCE", remap = false)
    static EntityRenderers getINSTANCE() {
        throw new UnsupportedOperationException();
    }

    @Accessor(value = "entityRenderers", remap = false)
    Map<EntityType<? extends Entity>, EntityRendererProvider<? extends Entity>> getEntityRenderers();
}
