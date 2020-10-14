package vazkii.quark.mixins;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;

import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.quark.base.handler.AsmHooks;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/model/ItemCameraTransforms$TransformType;ZLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IILnet/minecraft/client/renderer/model/IBakedModel;)V", at = @At("HEAD"))
	private void setColorRuneTargetStack(ItemStack itemStackIn, ItemCameraTransforms.TransformType transformTypeIn, boolean leftHand, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, IBakedModel modelIn, CallbackInfo callbackInfo) {
		AsmHooks.setColorRuneTargetStack(itemStackIn);
	}

	@Redirect(method = "func_239386_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;func_239270_k_()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorGlint() {
		return AsmHooks.getArmorGlint();
	}

	@Redirect(method = "func_239386_a_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;func_239271_l_()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getArmorEntityGlint() {
		return AsmHooks.getArmorEntityGlint();
	}

	@Redirect(method = "getBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlint() {
		return AsmHooks.getGlint();
	}

	@Redirect(method = "getBuffer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;getEntityGlint()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlint() {
		return AsmHooks.getEntityGlint();
	}

	@Redirect(method = "func_239391_c_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;func_239273_n_()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getGlintDirect() {
		return AsmHooks.getGlintDirect();
	}

	@Redirect(method = "func_239391_c_", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;func_239274_p_()Lnet/minecraft/client/renderer/RenderType;"))
	private static RenderType getEntityGlintDirect() {
		return AsmHooks.getEntityGlintDirect();
	}
}
