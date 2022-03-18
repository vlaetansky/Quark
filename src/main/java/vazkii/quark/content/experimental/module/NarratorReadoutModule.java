package vazkii.quark.content.experimental.module;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.client.handler.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

import java.util.List;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class NarratorReadoutModule extends QuarkModule {

	@OnlyIn(Dist.CLIENT)
	private KeyMapping keybind;

	@OnlyIn(Dist.CLIENT)
	private KeyMapping keybindFull;

	private float last;

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		if(enabled) {
			keybind = ModKeybindHandler.init("narrator_readout", null, ModKeybindHandler.MISC_GROUP);
			keybindFull = ModKeybindHandler.init("narrator_full_readout", null, ModKeybindHandler.MISC_GROUP);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		boolean down = isDown(event.getButton(), 0, true, keybind);
		boolean full = isDown(event.getButton(), 0, true, keybindFull);

		acceptInput(down || full, down);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		boolean down = isDown(event.getKey(), event.getScanCode(), false, keybind);
		boolean full = isDown(event.getKey(), event.getScanCode(), false, keybindFull);

		acceptInput(down || full, down);
	}

	@OnlyIn(Dist.CLIENT)
	private boolean isDown(int key, int scancode, boolean mouse, KeyMapping keybind) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.screen != null) {
			if(mouse)
				return (keybind.matchesMouse(key) &&
						(keybind.getKeyModifier() == KeyModifier.NONE || keybind.getKeyModifier().isActive(KeyConflictContext.GUI)));

			else return (keybind.matches(key, scancode) &&
					(keybind.getKeyModifier() == KeyModifier.NONE || keybind.getKeyModifier().isActive(KeyConflictContext.GUI)));
		}
		else return keybind.isDown();
	}

	@OnlyIn(Dist.CLIENT)
	private void acceptInput(boolean down, boolean full) {
		Minecraft mc = Minecraft.getInstance();

		float curr = ClientTicker.total;
		if(down && (curr - last) > 10) {
			Narrator narrator = Narrator.getNarrator();
			String readout = getReadout(mc, full);
			if(readout != null) {
				narrator.say(readout, true);
				last = curr;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private String getReadout(Minecraft mc, boolean full) {
		Player player = mc.player;
		if(player == null)
			return I18n.get("quark.readout.not_ingame");

		StringBuilder sb = new StringBuilder();

		if(mc.screen == null) {
			HitResult ray = mc.hitResult;
			if(ray instanceof BlockHitResult) {
				BlockPos pos = ((BlockHitResult) ray).getBlockPos();
				BlockState state = mc.level.getBlockState(pos);

				Item item = state.getBlock().asItem();
				if(item != null) {
					sb.append(I18n.get("quark.readout.looking", item.getName(new ItemStack(item)).getString()));

					if(full)
						sb.append(", ");
				}

				if(state.getBlock() instanceof SignBlock) {
					SignBlockEntity tile = (SignBlockEntity) mc.level.getBlockEntity(pos);
					sb.append(I18n.get("quark.readout.sign_says"));
					for(Component cmp : tile.messages) {
						String msg = cmp.getString().trim();
						if(!msg.isEmpty()) {
							sb.append(cmp.getString());
							sb.append(" ");
						}
					}

					sb.append(". ");
				}
			}

			if(full) {
				ItemStack stack = player.getMainHandItem();
				ItemStack stack2 = player.getOffhandItem();
				if(stack.isEmpty()) {
					stack = stack2;
					stack2 = ItemStack.EMPTY;
				}

				if(!stack.isEmpty()) {
					if(!stack2.isEmpty())
						sb.append(I18n.get("quark.readout.holding_with_off", stack.getCount(), stack.getHoverName().getString(), stack2.getCount(), stack2.getHoverName().getString()));
					else sb.append(I18n.get("quark.readout.holding", stack.getCount(), stack.getHoverName().getString()));

					sb.append(", ");
				}

				sb.append(I18n.get("quark.readout.health", (int) mc.player.getHealth()));
				sb.append(", ");

				sb.append(I18n.get("quark.readout.food", mc.player.getFoodData().getFoodLevel()));
			}
		}

		else {
			if(mc.screen instanceof AbstractContainerScreen<?> cnt) {
				Slot slot = cnt.getSlotUnderMouse();
				ItemStack stack = (slot == null ? ItemStack.EMPTY : slot.getItem());
				if(stack.isEmpty())
					sb.append(I18n.get("quark.readout.no_item"));
				else {
					List<Component> tooltip = cnt.getTooltipFromItem(stack);

					for(Component t : tooltip) {
						Component print = t.copy();
						List<Component> bros = print.getSiblings();

						for(Component sib : bros) {
							if(sib instanceof TranslatableComponent ttc) {
								if(ttc.getKey().contains("enchantment.level.")) {
									bros.set(bros.indexOf(sib), new TextComponent(ttc.getKey().substring("enchantment.level.".length())));
									break;
								}
							}
						}

						sb.append(print.getString());

						if(!full)
							break;

						sb.append(", ");
					}
				}
			}
			else sb.append(mc.screen.getNarrationMessage());
		}


		return sb.toString();
	}

}
