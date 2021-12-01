package vazkii.quark.content.tools.module;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.WithdrawSeedsMessage;
import vazkii.quark.content.tools.capability.SeedPouchDropIn;
import vazkii.quark.content.tools.item.SeedPouchItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class SeedPouchModule extends QuarkModule {

	private static final ResourceLocation SEED_POUCH_CAP = new ResourceLocation(Quark.MOD_ID, "seed_pouch_drop_in");

	public static Item seed_pouch;

    public static Tag<Item> seedPouchHoldableTag;
	
	@Config public static int maxItems = 640;
	@Config public static boolean showAllVariantsInCreative = true;
	@Config public static int shiftRange = 3;

	private static boolean shouldCancelNextRelease = false;

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
	public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if(event.getObject().getItem() == seed_pouch)
			event.addCapability(SEED_POUCH_CAP, new SeedPouchDropIn());
	}

	@SubscribeEvent 
	@OnlyIn(Dist.CLIENT)
	public void onRightClick(GuiScreenEvent.MouseClickedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		Screen gui = mc.screen;
		if(gui instanceof AbstractContainerScreen && !(gui instanceof CreativeModeInventoryScreen) && event.getButton() == 1) {
			AbstractContainerScreen<?> container = (AbstractContainerScreen<?>) gui;
			Slot under = container.getSlotUnderMouse();
			if(under != null) {
				ItemStack underStack = under.getItem();
				ItemStack held = mc.player.inventory.getCarried();

				if(underStack.getItem() == seed_pouch) {
					Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(underStack);
					if(contents != null) {
						ItemStack seed = contents.getLeft();
						if(held.isEmpty()) {
							int takeOut = Math.min(seed.getMaxStackSize(), contents.getRight());

							ItemStack result = seed.copy();
							result.setCount(takeOut);
							mc.player.inventory.setCarried(result);

							QuarkNetwork.sendToServer(new WithdrawSeedsMessage(under.index));

							shouldCancelNextRelease = true;
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	@OnlyIn(Dist.CLIENT)
	public void onRightClickRelease(GuiScreenEvent.MouseReleasedEvent.Pre event) {
		if(shouldCancelNextRelease) {
			shouldCancelNextRelease = false;
			event.setCanceled(true);
		}
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

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == seed_pouch) {
			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
			if(contents != null) {	
				List<Component> tooltip = event.getToolTip();

				int stacks = Math.max(1, (contents.getRight() - 1) / contents.getLeft().getMaxStackSize() + 1);
				int len = 16 + stacks * 8;

				String s = "";
				Minecraft mc = Minecraft.getInstance();
				while(mc.font.width(s) < len)
					s += " ";

				tooltip.add(1, new TextComponent(s));
				tooltip.add(1, new TextComponent(s));
			}

		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event) {
		ItemStack stack = event.getStack();
		if(stack.getItem() == seed_pouch) {
			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
			if(contents != null) {			
				ItemStack seed = contents.getLeft().copy();

				Minecraft mc = Minecraft.getInstance();
				ItemRenderer render = mc.getItemRenderer();

				int x = event.getX();
				int y = event.getY();

				int count = contents.getRight();
				int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);

				GlStateManager._pushMatrix();
				GlStateManager._translated(x, y + 12, 500);
				for(int i = 0; i < stacks; i++) {
					if(i == (stacks - 1))
						seed.setCount(count);

					GlStateManager._pushMatrix();
					GlStateManager._translated(8 * i, Math.sin(i * 498543) * 2, 0);

					render.renderAndDecorateItem(seed, 0, 0);
					render.renderGuiItemDecorations(mc.font, seed, 0, 0);
					GlStateManager._popMatrix();
				}
				GlStateManager._popMatrix();
			}
		}
	}

}
