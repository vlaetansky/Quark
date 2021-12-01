//package vazkii.quark.content.tweaks.capability;
//
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//import vazkii.arl.util.AbstractDropIn;
//import vazkii.quark.base.handler.SimilarBlockTypeHandler;
//
//public class LavaBucketDropIn extends AbstractDropIn {
//
//	@Override
//	public boolean canDropItemIn(Player player, ItemStack stack, ItemStack incoming, Slot slot) {
//		return !player.isCreative() && slot.mayPickup(player) && slot.mayPlace(stack) && !incoming.getItem().isFireResistant() && !SimilarBlockTypeHandler.isShulkerBox(incoming);
//	}
//
//	@Override
//	public ItemStack dropItemIn(Player player, ItemStack stack, ItemStack incoming, Slot slot) {
//		incoming.setCount(0);
//		
//		if(!player.level.isClientSide)
//			player.level.playSound(null, player.blockPosition(), SoundEvents.LAVA_EXTINGUISH, SoundSource.PLAYERS, 0.25F, 2F + (float) Math.random());
//		
//		return stack;
//	}
//
//}
