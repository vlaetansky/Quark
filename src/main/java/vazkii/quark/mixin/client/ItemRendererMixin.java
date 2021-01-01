package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import vazkii.quark.content.tools.module.ColorRunesModule;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V", at = @At("HEAD"))
	private void setColorRuneTargetStack(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn, CallbackInfo callbackInfo) {
		ColorRunesModule.setTargetStack(itemStackIn);
	}

	@Redirect(method = "getArmorVertexBuilder", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getArmorGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorGlint() {
		return ColorRunesModule.getArmorGlint();
	}

	@Redirect(method = "getArmorVertexBuilder", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getArmorEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorEntityGlint() {
		return ColorRunesModule.getArmorEntityGlint();
	}

	@Redirect(method = "getBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlint() {
		return ColorRunesModule.getGlint();
	}	

	@Redirect(method = "getBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlint() {
		return ColorRunesModule.getEntityGlint();
	}

	@Redirect(method = "getEntityGlintVertexBuilder", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintDirect() {
		return ColorRunesModule.getGlintDirect();
	}

	@Redirect(method = "getEntityGlintVertexBuilder", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getEntityGlintDirect()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlintDirect() {
		return ColorRunesModule.getEntityGlintDirect();
	}
}
