package vazkii.quark.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.quark.content.management.module.ItemSharingModule;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {

	@Redirect(method = "render", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I"))
	private int drawItems(Font instance, PoseStack poseStack, FormattedCharSequence sequence, float x, float y, int color) {
		ItemSharingModule.renderItemForMessage(poseStack, sequence, x, y, color);

		return instance.drawShadow(poseStack, sequence, x, y, color);
	}
}
