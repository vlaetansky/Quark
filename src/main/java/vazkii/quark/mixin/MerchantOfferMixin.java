package vazkii.quark.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.quark.content.tools.module.AncientTomesModule;

@Mixin(MerchantOffer.class)
public class MerchantOfferMixin {

	@Inject(method = "isRequiredItem", at = @At("HEAD"), cancellable = true)
	private void isRequiredItem(ItemStack comparing, ItemStack reference, CallbackInfoReturnable<Boolean> cir) {
		MerchantOffer offer = (MerchantOffer) (Object) this;
		if (AncientTomesModule.matchWildcardEnchantedBook(offer, comparing, reference))
			cir.setReturnValue(true);
	}

}
