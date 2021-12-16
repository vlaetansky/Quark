package vazkii.quark.content.tools.module;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.tools.item.SeedPouchItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class SeedPouchModule extends QuarkModule {

	public static Item seed_pouch;

    public static Tag<Item> seedPouchHoldableTag;
	
	@Config public static int maxItems = 640;
	@Config public static boolean showAllVariantsInCreative = true;
	@Config public static int shiftRange = 3;

	@Override
	public void construct() {
		seed_pouch = new SeedPouchItem(this);
	}
	
    @Override
    public void setup() {
    	seedPouchHoldableTag = ItemTags.createOptional(new ResourceLocation(Quark.MOD_ID, "seed_pouch_holdable"));
    }

	@OnlyIn(Dist.CLIENT)
	@Override
	public void clientSetup() {
		enqueue(() -> ItemProperties.register(seed_pouch, new ResourceLocation("pouch_items"), SeedPouchItem::itemFraction));
	}

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		Player player = event.getPlayer();
		ItemStack stack = event.getItem().getItem();

		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();

		ImmutableSet<ItemStack> stacks = ImmutableSet.of(main, off);
		for(ItemStack heldStack : stacks)
			if(heldStack.getItem() == seed_pouch) {
				Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(heldStack);
				if(contents != null) {
					ItemStack pouchStack = contents.getLeft();
					if(ItemStack.isSame(pouchStack, stack)) {
						int curr = contents.getRight();
						int missing = maxItems - curr;

						int count = stack.getCount();
						int toAdd = Math.min(missing, count);

						stack.setCount(count - toAdd);
						SeedPouchItem.setCount(heldStack, curr + toAdd);

						if(player.level instanceof ServerLevel)
							((ServerLevel) player.level).playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_LEATHER, SoundSource.PLAYERS, 0.2F, (player.level.random.nextFloat() - player.level.random.nextFloat()) * 1.4F + 2.0F);

						if(stack.getCount() == 0)
							break;
					}
				}
			}
	}

//	@SubscribeEvent
//	@OnlyIn(Dist.CLIENT)
//	public void makeTooltip(ItemTooltipEvent event) {
//		ItemStack stack = event.getItemStack();
//		if(stack.getItem() == seed_pouch) {
//			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
//			if(contents != null) {	
//				List<Component> tooltip = event.getToolTip();
//
//				int stacks = Math.max(1, (contents.getRight() - 1) / contents.getLeft().getMaxStackSize() + 1);
//				int len = 16 + stacks * 8;
//
//				String s = "";
//				Minecraft mc = Minecraft.getInstance();
//				while(mc.font.width(s) < len)
//					s += " ";
//
//				tooltip.add(1, new TextComponent(s));
//				tooltip.add(1, new TextComponent(s));
//			}
//
//		}
//	}
//	@SubscribeEvent
//	@OnlyIn(Dist.CLIENT)
//	public void renderTooltip(RenderTooltipEvent.PostText event) {
//		ItemStack stack = event.getStack();
//		if(stack.getItem() == seed_pouch) {
//			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
//			if(contents != null) {			
//				ItemStack seed = contents.getLeft().copy();
//
//				Minecraft mc = Minecraft.getInstance();
//				ItemRenderer render = mc.getItemRenderer();
//
//				int x = event.getX();
//				int y = event.getY();
//
//				int count = contents.getRight();
//				int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);
//
//				GlStateManager._pushMatrix();
//				GlStateManager._translated(x, y + 12, 500);
//				for(int i = 0; i < stacks; i++) {
//					if(i == (stacks - 1))
//						seed.setCount(count);
//
//					GlStateManager._pushMatrix();
//					GlStateManager._translated(8 * i, Math.sin(i * 498543) * 2, 0);
//
//					render.renderAndDecorateItem(seed, 0, 0);
//					render.renderGuiItemDecorations(mc.font, seed, 0, 0);
//					GlStateManager._popMatrix();
//				}
//				GlStateManager._popMatrix();
//			}
//		}
//	}

}
