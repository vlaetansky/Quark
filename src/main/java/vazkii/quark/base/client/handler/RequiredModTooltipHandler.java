package vazkii.quark.base.client.handler;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Quark.MOD_ID, value = Dist.CLIENT)
public class RequiredModTooltipHandler {

	private static final Map<Item, String> ITEMS = new HashMap<>();
	private static final Map<Block, String> BLOCKS = new HashMap<>();

	public static void map(Item item, String mod) {
		ITEMS.put(item, mod);
	}

	public static void map(Block block, String mod) {
		BLOCKS.put(block, mod);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onTooltip(ItemTooltipEvent event) {
		if(!BLOCKS.isEmpty() && event.getPlayer() != null && event.getPlayer().level != null) {
			for(Block b : BLOCKS.keySet())
				ITEMS.put(b.asItem(), BLOCKS.get(b));
			BLOCKS.clear();
		}

		Item item = event.getItemStack().getItem();
		if(ITEMS.containsKey(item)) {
			String mod = ITEMS.get(item);
			if (!ModList.get().isLoaded(mod)) {
				event.getToolTip().add(new TranslatableComponent("quark.misc.mod_disabled", mod).withStyle(ChatFormatting.GRAY));
			}
		}
	}
}
