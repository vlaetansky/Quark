package vazkii.quark.base.mixin;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(EntityRendererManager.class)
public class EntityRendererManagerMixin {

	@Shadow
	public native <T extends Entity> EntityRenderer<? super T> getRenderer(T entityIn);

	@Inject(method = "renderEntityStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", shift = At.Shift.AFTER))
	private <E extends Entity> void renderChain(E entityIn, double xIn, double yIn, double zIn, float rotationYawIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, CallbackInfo ci) {
		AsmHooks.renderChain(getRenderer(entityIn), entityIn, matrixStackIn, bufferIn, partialTicks);
	}
}
