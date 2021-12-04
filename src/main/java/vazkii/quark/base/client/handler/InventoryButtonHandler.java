package vazkii.quark.base.client.handler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.api.IQuarkButtonAllowed;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.GeneralConfig;
import vazkii.quark.base.handler.InventoryTransferHandler;
import vazkii.quark.base.module.QuarkModule;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Quark.MOD_ID, value = Dist.CLIENT)
public final class InventoryButtonHandler {

	private static final Multimap<ButtonTargetType, ButtonProviderHolder> providers = Multimaps.newSetMultimap(new HashMap<>(), TreeSet::new);
	private static final Multimap<ButtonTargetType, Button> currentButtons = Multimaps.newSetMultimap(new HashMap<>(), LinkedHashSet::new);
	
	@SubscribeEvent
	public static void initGui(ScreenEvent.InitScreenEvent.Post event) {
		Screen screen = event.getScreen();
		if(GeneralConfig.printScreenClassnames)
			Quark.LOG.info("Opened screen {}", screen.getClass().getName());
		currentButtons.clear();
		
		if(screen instanceof AbstractContainerScreen && (screen instanceof IQuarkButtonAllowed || GeneralConfig.isScreenAllowed(screen))) {
			Minecraft mc = Minecraft.getInstance();
			AbstractContainerScreen<?> containerScreen = (AbstractContainerScreen<?>) screen;

			if(containerScreen instanceof InventoryScreen || containerScreen.getClass().getName().contains("CuriosScreen"))
				applyProviders(event, ButtonTargetType.PLAYER_INVENTORY, containerScreen, s -> s.container == mc.player.getInventory() && s.getSlotIndex() == 17);
			else {
				if(InventoryTransferHandler.accepts(containerScreen.getMenu(), mc.player)) { 
					applyProviders(event, ButtonTargetType.CONTAINER_INVENTORY, containerScreen, s -> s.container != mc.player.getInventory() && s.getSlotIndex() == 8);
					applyProviders(event, ButtonTargetType.CONTAINER_PLAYER_INVENTORY, containerScreen, s -> s.container == mc.player.getInventory() && s.getSlotIndex() == 17);
				}
			}
		}
	}

	private static Collection<ButtonProviderHolder> forGui(Screen gui) {
		Set<ButtonProviderHolder> holders = new HashSet<>();
		if (gui instanceof AbstractContainerScreen) {
			AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) gui;

			if (gui instanceof InventoryScreen)
				holders.addAll(providers.get(ButtonTargetType.PLAYER_INVENTORY));
			else {
				Minecraft mc = Minecraft.getInstance();
				if(InventoryTransferHandler.accepts(screen.getMenu(), mc.player)) {
					holders.addAll(providers.get(ButtonTargetType.CONTAINER_INVENTORY));
					holders.addAll(providers.get(ButtonTargetType.CONTAINER_PLAYER_INVENTORY));
				}
			}
		}

		return holders;
	}

	@SubscribeEvent
	public static void mouseInputEvent(ScreenEvent.MouseClickedEvent.Pre pressed) {
		Screen gui = pressed.getScreen();
		if (gui instanceof AbstractContainerScreen) {
			AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) gui;
			if(!GeneralConfig.isScreenAllowed(screen))
				return;
			
			Collection<ButtonProviderHolder> holders = forGui(screen);

			for (ButtonProviderHolder holder : holders) {
				if (holder.keybind != null &&
						holder.keybind.matchesMouse(pressed.getButton()) &&
						(holder.keybind.getKeyModifier() == KeyModifier.NONE || holder.keybind.getKeyModifier().isActive(KeyConflictContext.GUI))) {
					holder.pressed.accept(screen);
					pressed.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void keyboardInputEvent(ScreenEvent.KeyboardKeyPressedEvent.Post pressed) {
		Screen gui = pressed.getScreen();
		if (gui instanceof AbstractContainerScreen) {
			AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) gui;
			if(!GeneralConfig.isScreenAllowed(screen))
				return;

			Collection<ButtonProviderHolder> holders = forGui(screen);

			for (ButtonProviderHolder holder : holders) {
				if (holder.keybind != null &&
						holder.keybind.matches(pressed.getKeyCode(), pressed.getScanCode()) &&
						(holder.keybind.getKeyModifier() == KeyModifier.NONE || holder.keybind.getKeyModifier().isActive(KeyConflictContext.GUI))) {
					holder.pressed.accept(screen);
					pressed.setCanceled(true);
				}
			}
		}

	}

	private static void applyProviders(ScreenEvent.InitScreenEvent.Post event, ButtonTargetType type, AbstractContainerScreen<?> screen, Predicate<Slot> slotPred) {
		Collection<ButtonProviderHolder> holders = providers.get(type);
		if(!holders.isEmpty()) {
			for(Slot slot : screen.getMenu().slots)
				if(slotPred.test(slot)) {
					int x = slot.x + 6;
					int y = slot.y - 13;
					
//					if(screen instanceof BackpackInventoryScreen) TODO ODDITIES
//						y -= 60;
					
					for(ButtonProviderHolder holder : holders) {
						Button button = holder.getButton(screen, x, y);
						if(button != null) {
							event.addListener(button);
							currentButtons.put(type, button);
							x -= 12;
						}
					}

					return;
				}
		}
	}
	
	public static Collection<Button> getActiveButtons(ButtonTargetType type) {
		return currentButtons.get(type);
	}

	public static void addButtonProvider(QuarkModule module, ButtonTargetType type, int priority, KeyMapping binding, Consumer<AbstractContainerScreen<?>> onKeybind, ButtonProvider provider) {
		providers.put(type, new ButtonProviderHolder(module, priority, provider,
				binding, onKeybind));
	}

	public static void addButtonProvider(QuarkModule module, ButtonTargetType type, int priority, String keybindName, Consumer<AbstractContainerScreen<?>> onKeybind, ButtonProvider provider) {
		KeyMapping keybind = ModKeybindHandler.init(keybindName, null, ModKeybindHandler.INV_GROUP);
		keybind.setKeyConflictContext(KeyConflictContext.GUI);
		addButtonProvider(module, type, priority, keybind, onKeybind, provider);
	}

	public static void addButtonProvider(QuarkModule module, ButtonTargetType type, int priority, ButtonProvider provider) {
		providers.put(type, new ButtonProviderHolder(module, priority, provider));
	}

	public enum ButtonTargetType {
		PLAYER_INVENTORY,
		CONTAINER_INVENTORY,
		CONTAINER_PLAYER_INVENTORY
	}

	public interface ButtonProvider {
		Button provide(AbstractContainerScreen<?> parent, int x, int y);
	}

	private static class ButtonProviderHolder implements Comparable<ButtonProviderHolder> {

		private final int priority;
		private final QuarkModule module;
		private final ButtonProvider provider;

		private final KeyMapping keybind;
		private final Consumer<AbstractContainerScreen<?>> pressed;

		public ButtonProviderHolder(QuarkModule module, int priority, ButtonProvider provider, KeyMapping keybind, Consumer<AbstractContainerScreen<?>> onPressed) {
			this.module = module;
			this.priority = priority;
			this.provider = provider;
			this.keybind = keybind;
			this.pressed = onPressed;
		}

		public ButtonProviderHolder(QuarkModule module, int priority, ButtonProvider provider) {
			this(module, priority, provider, null, (screen) -> {});
		}

		@Override
		public int compareTo(@Nonnull ButtonProviderHolder o) {
			return priority - o.priority;
		}

		public Button getButton(AbstractContainerScreen<?> parent, int x, int y) {
			return module.enabled ? provider.provide(parent, x, y) : null;
		}

	}

}
