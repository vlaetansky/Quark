package vazkii.quark.content.client.module;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.api.IUsageTickerOverride;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@LoadModule(category = ModuleCategory.CLIENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class UsageTickerModule extends QuarkModule {

	public static List<TickerElement> elements = new ArrayList<>();

	@Config(description = "Switch the armor display to the off hand side and the hand display to the main hand side")
	public static boolean invert = false;

	@Config public static int shiftLeft = 0;
	@Config public static int shiftRight = 0;

	@Config public static boolean enableMainHand = true;
	@Config public static boolean enableOffHand = true;
	@Config public static boolean enableArmor = true;

	@Override
	public void configChanged() {
		elements = new ArrayList<>();

		if(enableMainHand)
			elements.add(new TickerElement(EquipmentSlot.MAINHAND));
		if(enableOffHand)
			elements.add(new TickerElement(EquipmentSlot.OFFHAND));
		if(enableArmor) {
			elements.add(new TickerElement(EquipmentSlot.HEAD));
			elements.add(new TickerElement(EquipmentSlot.CHEST));
			elements.add(new TickerElement(EquipmentSlot.LEGS));
			elements.add(new TickerElement(EquipmentSlot.FEET));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player != null && mc.level != null)
				for(TickerElement ticker : elements)
					if(ticker != null)
						ticker.tick(mc.player);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderHUD(RenderGameOverlayEvent.Post event) {
		if(event.getType() == ElementType.ALL) {
			Window window = event.getWindow();
			Player player = Minecraft.getInstance().player;
			float partial = event.getPartialTicks();

			for(TickerElement ticker : elements)
				if(ticker != null)
					ticker.render(window, player, invert, partial);
		}
	}

	public static class TickerElement {

		private static final int MAX_TIME = 60;
		private static final int ANIM_TIME = 5;

		public int liveTicks;
		public final EquipmentSlot slot;
		public ItemStack currStack = ItemStack.EMPTY;
		public ItemStack currRealStack = ItemStack.EMPTY;
		public int currCount;

		public TickerElement(EquipmentSlot slot) {
			this.slot = slot;
		}

		@OnlyIn(Dist.CLIENT)
		public void tick(Player player) {
			ItemStack realStack = getStack(player);
			int count = getStackCount(player, realStack);

			ItemStack displayedStack = getDisplayedStack(realStack, count);

			if(displayedStack.isEmpty())
				liveTicks = 0;
			else if(shouldChange(realStack, currRealStack, count, currCount) || shouldChange(displayedStack, currStack, count, currCount)) {
				boolean done = liveTicks == 0;
				boolean animatingIn = liveTicks > MAX_TIME - ANIM_TIME;
				boolean animatingOut = liveTicks < ANIM_TIME && !done;
				if(animatingOut)
					liveTicks = MAX_TIME - liveTicks;
				else if(!animatingIn) {
					if(!done)
						liveTicks = MAX_TIME - ANIM_TIME;
					else liveTicks = MAX_TIME;
				}
			} else if(liveTicks > 0)
				liveTicks--;

			currCount = count;
			currStack = displayedStack;
			currRealStack = realStack;
		}

		@OnlyIn(Dist.CLIENT)
		public void render(Window window, Player player, boolean invert, float partialTicks) {
			if(liveTicks > 0) {
				float animProgress;

				if(liveTicks < ANIM_TIME)
					animProgress = Math.max(0, liveTicks - partialTicks) / ANIM_TIME;
				else animProgress = Math.min(ANIM_TIME, (MAX_TIME - liveTicks) + partialTicks) / ANIM_TIME;

				float anim = -animProgress * (animProgress - 2) * 20F;

				float x = window.getGuiScaledWidth() / 2f;
				float y = window.getGuiScaledHeight() - anim;

				int barWidth = 190;
				boolean armor = slot.getType() == Type.ARMOR;

				HumanoidArm primary = player.getMainArm();
				HumanoidArm ourSide = (armor != invert) ? primary : primary.getOpposite();

				int slots = armor ? 4 : 2;
				int index = slots - slot.getIndex() - 1;
				float mul = ourSide == HumanoidArm.LEFT ? -1 : 1;

				if(ourSide != primary && !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
					barWidth += 58;

				Minecraft mc = Minecraft.getInstance();
				x += (barWidth / 2f) * mul + index * 20;
				if(ourSide == HumanoidArm.LEFT) {
					x -= slots * 20;
					x += shiftLeft;
				} else x += shiftRight;

				ItemStack stack = getRenderedStack(player);

				mc.getItemRenderer().renderAndDecorateItem(stack, (int) x, (int) y);
				mc.getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, (int) x, (int) y);
			}
		}

		@OnlyIn(Dist.CLIENT)
		public boolean shouldChange(ItemStack currStack, ItemStack prevStack, int currentTotal, int pastTotal) {
			return !prevStack.sameItem(currStack) || (currStack.isDamageableItem() && currStack.getDamageValue() != prevStack.getDamageValue()) || currentTotal != pastTotal;
		}

		@OnlyIn(Dist.CLIENT)
		public ItemStack getStack(Player player) {
			return player.getItemBySlot(slot);
		}

		@OnlyIn(Dist.CLIENT)
		public ItemStack getDisplayedStack(ItemStack stack, int count) {
			boolean verifySize = true;
			if((stack.getItem() instanceof BowItem || stack.getItem() instanceof CrossbowItem) && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) == 0) {
				stack = new ItemStack(Items.ARROW);
				verifySize = false;
			}

			else if(stack.getItem() instanceof IUsageTickerOverride over) {

				stack = over.getUsageTickerItem(stack);
				verifySize = over.shouldUsageTickerCheckMatchSize(currStack);
			}

			if(!stack.isStackable() && slot.getType() == Type.HAND)
				return ItemStack.EMPTY;

			if(verifySize && stack.isStackable() && count == stack.getCount())
				return ItemStack.EMPTY;

			return stack;
		}

		@OnlyIn(Dist.CLIENT)
		public ItemStack getRenderedStack(Player player) {
			ItemStack stack = getStack(player);
			int count = getStackCount(player, stack);
			ItemStack displayStack = getDisplayedStack(stack, count).copy();
			if(displayStack != stack)
				count = getStackCount(player,  displayStack);
			displayStack.setCount(count);

			return displayStack;
		}

		@OnlyIn(Dist.CLIENT)
		public int getStackCount(Player player, ItemStack stack) {
			if(!stack.isStackable())
				return 1;

			Predicate<ItemStack> predicate = (stackAt) -> ItemStack.isSame(stackAt, stack) && ItemStack.tagMatches(stackAt, stack);

			if(stack.getItem() == Items.ARROW)
				predicate = (stackAt) -> stackAt.getItem() instanceof ArrowItem;

			int total = 0;
			Inventory inventory = player.getInventory();
			for(int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack stackAt = inventory.getItem(i);
				if(predicate.test(stackAt))
					total += stackAt.getCount();

				else if(stackAt.getItem() instanceof IUsageTickerOverride over) {
					total += over.getUsageTickerCountForItem(stackAt, predicate);
				}
			}

			return total;
		}

	}

}
