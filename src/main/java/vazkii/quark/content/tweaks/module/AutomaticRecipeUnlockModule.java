package vazkii.quark.content.tweaks.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.RecipeToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class AutomaticRecipeUnlockModule extends QuarkModule {

	@Config(description = "A list of recipe names that should NOT be added in by default")
	public static List<String> ignoredRecipes = Lists.newArrayList();

	@Config public static boolean forceLimitedCrafting = false;	
	@Config public static boolean disableRecipeBook = false;

	@SubscribeEvent 
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
		Player player = event.getPlayer();

		if(player instanceof ServerPlayer) {
			ServerPlayer spe = (ServerPlayer) player;
			MinecraftServer server = spe.getServer();
			if (server != null) {
				List<Recipe<?>> recipes = new ArrayList<>(server.getRecipeManager().getRecipes());
				recipes.removeIf((recipe) -> ignoredRecipes.contains(Objects.toString(recipe.getId())) || recipe.getResultItem().isEmpty());
				
				int idx = 0;
				int maxShift = 1000;
				int shift = 0;
				int size = recipes.size();
				do {
					shift = size - idx;
					int effShift = Math.min(maxShift, shift);
					
					List<Recipe<?>> sectionedRecipes = recipes.subList(idx, idx + effShift);
					player.awardRecipes(sectionedRecipes);
					idx += effShift;
				} while(shift > maxShift);
				

				if (forceLimitedCrafting)
					player.level.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
			}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onInitGui(InitScreenEvent.Post event) {
		Screen gui = event.getScreen();
		if(disableRecipeBook && gui instanceof RecipeUpdateListener) {
			Minecraft.getInstance().player.getRecipeBook().getBookSettings().setOpen(RecipeBookType.CRAFTING, false); 

			List<GuiEventListener> widgets = event.getListenersList();
			for(GuiEventListener w : widgets)
				if(w instanceof ImageButton) {
					event.removeListener(w);
					return;
				}
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientTick(ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if(mc.player != null && mc.player.tickCount < 20) {
			ToastComponent toasts = mc.getToasts();
			Queue<Toast> toastQueue = toasts.queued;
			for(Toast toast : toastQueue)
				if(toast instanceof RecipeToast) {
					RecipeToast recipeToast = (RecipeToast) toast;
					List<Recipe<?>> stacks = recipeToast.recipes;
					if(stacks.size() > 100) {
						toastQueue.remove(toast);
						return;
					}
				}
		}
	}

}
