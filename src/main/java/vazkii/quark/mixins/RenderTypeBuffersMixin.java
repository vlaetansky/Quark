package vazkii.quark.mixins;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeBuffers;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(RenderTypeBuffers.class)
public class RenderTypeBuffersMixin {

	@Inject(method = "put", at = @At("HEAD"))
	private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> mapBuildersIn, RenderType renderTypeIn, CallbackInfo callbackInfo) {
		AsmHooks.addGlintTypes(mapBuildersIn);
	}
}
