package vazkii.quark.mixin;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@Mixin(RenderingRegistry.class)
public interface RenderingRegistryAccessor {
    @Accessor(value = "INSTANCE", remap = false)
    static RenderingRegistry getINSTANCE() {
        throw new UnsupportedOperationException();
    }

    @Accessor(value = "entityRenderers", remap = false)
    Map<EntityType<? extends Entity>, IRenderFactory<? extends Entity>> getEntityRenderers();
}
