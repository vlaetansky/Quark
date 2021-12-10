package vazkii.quark.content.client.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Either;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;
import vazkii.quark.content.tools.module.AncientTomesModule;

public class EnchantedBookTooltips {

	private static List<ItemStack> testItems = null;
	private static Multimap<Enchantment, ItemStack> additionalStacks = null;

	public static void reloaded() {
		additionalStacks = null;
		testItems = null;
	}

	@OnlyIn(Dist.CLIENT)
	public static void makeTooltip(RenderTooltipEvent.GatherComponents event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player == null)
			return;

		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == AncientTomesModule.ancient_tome) {
			List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();
			int tooltipIndex = 0;

			List<EnchantmentInstance> enchants = getEnchantedBookEnchantments(stack);
			for(EnchantmentInstance ed : enchants) {
				Component match = ed.enchantment.getFullname(ed.level);

				for(; tooltipIndex < tooltip.size(); tooltipIndex++) {
					Either<FormattedText, TooltipComponent> elmAt = tooltip.get(tooltipIndex);
					if(elmAt.left().isPresent() && elmAt.left().get().equals(match)) {
						List<ItemStack> items = getItemsForEnchantment(ed.enchantment);
						int itemCount = items.size();
						int lines = (int) Math.ceil((double) itemCount / 10.0);
						
						int remLines = lines;
						int len = 3 + Math.min(10, itemCount) * 9;
						while(remLines > 0) {
							tooltip.add(tooltipIndex + 1, Either.right(new EnchantedBookComponent(stack, len, 10, tooltip)));
							tooltipIndex++;
							remLines--;
						}

						break;
					}
				}
			}
		}
	}

	private static final ThreadLocal<Enchantment> clueHolder = new ThreadLocal<>();
	private static final ThreadLocal<Integer> clueLevelHolder = ThreadLocal.withInitial(() -> 0);

	@OnlyIn(Dist.CLIENT)
	public static List<String> captureEnchantingData(List<String> list, EnchantmentScreen screen, Enchantment enchantment, int level) {
		ItemStack last = screen.last;
		if (!last.isEmpty() && last.getItem() == Items.BOOK) {
			clueHolder.set(enchantment);
			clueLevelHolder.set(level);
			if(enchantment != null) {
				Minecraft mc = Minecraft.getInstance();
				int tooltipIndex = 0;

				String match = ChatFormatting.stripFormatting(I18n.get("container.enchant.clue", enchantment.getFullname(level).getString()));

				for(; tooltipIndex < list.size(); tooltipIndex++) {
					String line = ChatFormatting.stripFormatting(list.get(tooltipIndex));
					if (line != null && line.equals(match)) {
						List<ItemStack> items = getItemsForEnchantment(enchantment);
						int itemCount = items.size();
						int lines = (int) Math.ceil((double) itemCount / 10.0);
						
						int remLines = lines;
						int len = 3 + Math.min(10, itemCount) * 9;
						while(remLines > 0) {
							String spaces = "";
							while (mc.font.width(spaces) < len)
								spaces += " ";

							list.add(tooltipIndex + 1, spaces);
							tooltipIndex++;
							remLines--;
						}

						break;
					}
				}
			}
		}


		return list;
	}

	public static List<ItemStack> getItemsForEnchantment(Enchantment e) {
		List<ItemStack> list = new ArrayList<>();

		for(ItemStack stack : getTestItems()) {
			Item item = stack.getItem();
			if(item instanceof QuarkItem && !((QuarkItem) item).isEnabled())
				continue;
			
			if(!stack.isEmpty() && e.canEnchant(stack))
				list.add(stack);
		}

		if(getAdditionalStacks().containsKey(e))
			list.addAll(getAdditionalStacks().get(e));

		return list;
	}

	public static List<EnchantmentInstance> getEnchantedBookEnchantments(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

		List<EnchantmentInstance> retList = new ArrayList<>(enchantments.size());

		for(Enchantment enchantment : enchantments.keySet()) {
			if (enchantment != null) {
				int level = enchantments.get(enchantment);
				retList.add(new EnchantmentInstance(enchantment, level));
			}
		}

		return retList;
	}

	private static Multimap<Enchantment, ItemStack> getAdditionalStacks() {
		if (additionalStacks == null)
			computeAdditionalStacks();
		return additionalStacks;
	}

	private static List<ItemStack> getTestItems() {
		if (testItems == null)
			computeTestItems();
		return testItems;
	}

	private static void computeTestItems() {
		testItems = Lists.newArrayList();

		for (String loc : ImprovedTooltipsModule.enchantingStacks) {
			Registry.ITEM.getOptional(new ResourceLocation(loc)).ifPresent(item -> testItems.add(new ItemStack(item)));
		}
	}

	private static void computeAdditionalStacks() {
		additionalStacks = HashMultimap.create();

		for(String s : ImprovedTooltipsModule.enchantingAdditionalStacks) {
			if(!s.contains("="))
				continue;

			String[] tokens = s.split("=");
			String left = tokens[0];
			String right = tokens[1];

			Optional<Enchantment> ench = Registry.ENCHANTMENT.getOptional(new ResourceLocation(left));
			if(ench.isPresent()) {
				tokens = right.split(",");

				for(String itemId : tokens) {
					Registry.ITEM.getOptional(new ResourceLocation(itemId)).ifPresent(item -> additionalStacks.put(ench.get(), new ItemStack(item)));
				}
			}
		}
	}
	
	public static class EnchantedBookComponent implements ClientTooltipComponent, TooltipComponent {

		private final ItemStack stack;
		private final int height, width;
		private final List<Either<FormattedText, TooltipComponent>> tooltip;

		public EnchantedBookComponent(ItemStack stack, int width, int height, List<Either<FormattedText, TooltipComponent>> tooltip) {
			this.stack = stack;
			this.height = height;
			this.width = width;
			this.tooltip = tooltip;
		}
		
		@Override
		public void renderImage(Font font, int tooltipX, int tooltipY, PoseStack pose, ItemRenderer itemRenderer, int something) {
			Enchantment enchantment = clueHolder.get();
			int level = clueLevelHolder.get();

			pose.pushPose();
			pose.translate(tooltipX, tooltipY + 12, 500);
			pose.scale(0.5f, 0.5f, 1.0f);
			Minecraft mc = Minecraft.getInstance();

			if (enchantment != null) {
				clueHolder.remove();
				clueLevelHolder.remove();
				String match = ChatFormatting.stripFormatting(I18n.get("container.enchant.clue", enchantment.getFullname(level).getString()));
				for(int tooltipIndex = 0; tooltipIndex < tooltip.size(); tooltipIndex++) {
					Either<FormattedText, TooltipComponent> either = tooltip.get(tooltipIndex);
					String line = either.left().isPresent() ? ChatFormatting.stripFormatting(either.left().get().getString()) : null;
					
					if(line != null && line.equals(match)) {
						int drawn = 0;

						List<ItemStack> items = getItemsForEnchantment(enchantment);
						for(ItemStack testStack : items) {
							mc.getItemRenderer().renderGuiItem(testStack, 6 + drawn * 18, tooltipIndex * 20 - 2);
							drawn++;
						}

						break;
					}
				}

			} else if(stack.getItem() == Items.ENCHANTED_BOOK || stack.getItem() == AncientTomesModule.ancient_tome) {
				List<EnchantmentInstance> enchants = getEnchantedBookEnchantments(stack);

				for(EnchantmentInstance ed : enchants) {
					String match = ed.enchantment.getFullname(ed.level).getString();
					for(int tooltipIndex = 0; tooltipIndex < tooltip.size(); tooltipIndex++) {
						Either<FormattedText, TooltipComponent> either = tooltip.get(tooltipIndex);
						String line = either.left().isPresent() ? ChatFormatting.stripFormatting(either.left().get().getString()) : null;
						
						if(line != null && line.equals(match)) {
							int drawn = 0;

							List<ItemStack> items = getItemsForEnchantment(ed.enchantment);
							for(ItemStack testStack : items) {
								mc.getItemRenderer().renderGuiItem(testStack, 6 + (drawn % 10) * 18, tooltipIndex * 20 - 2 + (drawn / 10) * 20);
								drawn++;
							}

							break;
						}
					}
				}
			}

			pose.popPose();
		}

		@Override
		public int getHeight() {
			return height;
		}

		@Override
		public int getWidth(Font font) {
			return width;
		}
		
	}
}
