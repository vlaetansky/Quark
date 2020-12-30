package vazkii.quark.content.experimental.module;

import com.mojang.text2speech.Narrator;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.client.ModKeybindHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class NarratorReadoutModule extends QuarkModule {

	@OnlyIn(Dist.CLIENT)
	private KeyBinding keybind;
	
	@OnlyIn(Dist.CLIENT)
	private KeyBinding keybindFull;
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		if(enabled) {
			keybind = ModKeybindHandler.init("narrator_readout", "n", ModKeybindHandler.ACCESSIBILITY_GROUP);
			keybindFull = ModKeybindHandler.init("narrator_full_readout", "m", ModKeybindHandler.ACCESSIBILITY_GROUP);
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		acceptInput();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		acceptInput();
	}
	
	private void acceptInput() {
		Minecraft mc = Minecraft.getInstance();
		boolean down = keybind != null && (keybind.isKeyDown() || keybindFull.isKeyDown());
		
		if(mc.isGameFocused() && down) {
			Narrator narrator = Narrator.getNarrator();
			String readout = getReadout(mc, keybindFull.isKeyDown());
			if(readout != null) {
				narrator.say(readout, true);
				if(mc.player != null)
					mc.player.sendStatusMessage(new StringTextComponent(readout), true);
			}
		}
	}
	
	private String getReadout(Minecraft mc, boolean full) {
		PlayerEntity player = mc.player;
		if(player == null)
			return "Not ingame.";

		StringBuilder sb = new StringBuilder();
		
		RayTraceResult ray = mc.objectMouseOver;
		if(ray != null && ray instanceof BlockRayTraceResult) {
			BlockPos pos = ((BlockRayTraceResult) ray).getPos();
			BlockState state = mc.world.getBlockState(pos);
			
			Item item = state.getBlock().asItem();
			if(item != null) {
				sb.append(I18n.format("quark.readout.looking", item.getDisplayName(new ItemStack(item)).getString()));
				
				if(full)
					sb.append(", ");
			}
		}
		
		if(full) {
			ItemStack stack = player.getHeldItemMainhand();
			ItemStack stack2 = player.getHeldItemOffhand();
			if(stack.isEmpty()) {
				stack = stack2;
				stack2 = ItemStack.EMPTY;
			}
			
			if(!stack.isEmpty()) {
				if(!stack2.isEmpty())
					sb.append(I18n.format("quark.readout.holding_with_off", stack.getCount(), stack.getDisplayName().getString(), stack2.getCount(), stack2.getDisplayName().getString()));
				else sb.append(I18n.format("quark.readout.holding", stack.getCount(), stack.getDisplayName().getString()));
				
				sb.append(", ");
			}
			
			sb.append(I18n.format("quark.readout.health", (int) mc.player.getHealth()));
			sb.append(", ");
			
			sb.append(I18n.format("quark.readout.food", mc.player.getFoodStats().getFoodLevel()));
		}

		return sb.toString();
	}
	
}
