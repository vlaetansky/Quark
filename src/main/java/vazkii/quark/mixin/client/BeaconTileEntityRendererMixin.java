package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import vazkii.quark.content.world.client.render.QuarkBeaconBlockEntityRenderer;

@Mixin(BeaconRenderer.class)
public class BeaconTileEntityRendererMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void render(BeaconBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn, CallbackInfo callbackInfo) {
		if(QuarkBeaconBlockEntityRenderer.render(tileEntityIn, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn))
			callbackInfo.cancel();
	}


}
