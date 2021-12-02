package vazkii.quark.content.tweaks.module;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.SpamlessChatMessage;
import vazkii.quark.base.network.message.UpdateAfkMessage;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class ImprovedSleepingModule extends QuarkModule {

	private int timeSinceKeystroke;
	private static List<String> sleepingPlayers = new ArrayList<>();

	@Config
	public static boolean enableAfk = true;

	@Config
	@Config.Min(value = 0, exclusive = true)
	public static int afkTime = 2 * 1200;

	@Config
	@Config.Min(value = 0, exclusive = true)
	@Config.Max(1)
	public static double percentReq = 1;

	private static final String TAG_JUST_SLEPT = "quark:slept";
	private static final String TAG_AFK = "quark:afk";

	private static final int AFK_MSG = "quark afk".hashCode();
	private static final int SLEEP_MSG = "quark sleep".hashCode();

	public static void updateAfk(Player player, boolean afk) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(ImprovedSleepingModule.class) || !enableAfk)
			return;

		boolean alone = player.level.players().size() == 1;
		if(afk) {
			player.getPersistentData().putBoolean(TAG_AFK, true);
			if(!alone) {
				TranslatableComponent text = new TranslatableComponent("quark.misc.now_afk");
				text.withStyle(ChatFormatting.AQUA);
				SpamlessChatMessage.sendToPlayer(player, AFK_MSG, text);
			}
		} else {
			player.getPersistentData().putBoolean(TAG_AFK, false);
			if(!alone) {
				TranslatableComponent text = new TranslatableComponent("quark.misc.left_afk");
				text.withStyle(ChatFormatting.AQUA);
				SpamlessChatMessage.sendToPlayer(player, AFK_MSG, text);
			}
		}
	}

	public static boolean isEveryoneAsleep(boolean parent) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(ImprovedSleepingModule.class))
			return parent;

		return false;
	}

	public static boolean isEveryoneAsleep(Level world) {
		Pair<Integer, Integer> counts = getPlayerCounts(world);
		int legitPlayers = counts.getLeft();
		int sleepingPlayers = counts.getRight();

		int reqPlayers = Math.max(1, (int) (percentReq * (double) legitPlayers));
		return (legitPlayers > 0 && ((float) sleepingPlayers / (float) reqPlayers) >= 1);
	}

	public static void whenNightPasses(ServerLevel world) {
		MinecraftServer server = world.getServer();

		if (world.players().size() == 1)
			return;

		boolean isDay = world.getSkyDarken() < 4;
		int msgCount = 10;
		int msg = world.random.nextInt(msgCount);
		
		TranslatableComponent message = new TranslatableComponent(world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT) ?
				(isDay ? "quark.misc.day_has_passed" : ("quark.misc.night_has_passed" + msg)) :
				(isDay ? "quark.misc.day_no_passage" : "quark.misc.night_no_passage"));
		message.setStyle(message.getStyle().applyFormat(ChatFormatting.GOLD));

		List<ServerPlayer> serverPlayers = new ArrayList<>(server.getPlayerList().getPlayers());
		for (ServerPlayer player : serverPlayers)
			SpamlessChatMessage.sendToPlayer(player, SLEEP_MSG, message);
	}

	private static boolean doesPlayerCountForSleeping(Player player) {
		return !player.isSpectator() && !player.getPersistentData().getBoolean(TAG_AFK);
	}

	private static boolean isPlayerSleeping(Player player) {
		return player.isSleepingLongEnough();
	}

	private static Pair<Integer, Integer> getPlayerCounts(Level world) {
		int legitPlayers = 0;
		int sleepingPlayers = 0;
		List<Player> players = new ArrayList<>(world.players());
		for(Player player : players)
			if(doesPlayerCountForSleeping(player)) {
				legitPlayers++;

				if(isPlayerSleeping(player))
					sleepingPlayers++;
			}

		return Pair.of(legitPlayers, sleepingPlayers);
	}

	@SubscribeEvent
	public void onWakeUp(PlayerWakeUpEvent event) {
		Player player = event.getPlayer();
		if (/*event.shouldSetSpawn() && */!event.updateWorld() && !event.wakeImmediately())
			player.getPersistentData().putLong(TAG_JUST_SLEPT, player.level.getGameTime());
	}

	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		Level world = event.world;
		MinecraftServer server = world.getServer();

		if (event.side == LogicalSide.CLIENT ||
				!world.dimension().location().equals(DimensionType.OVERWORLD_LOCATION.location()) ||
				event.phase != TickEvent.Phase.END ||
				server == null)
			return;

		List<Player> worldPlayers = new ArrayList<>(world.players());
		if (isEveryoneAsleep(world)) {
			if (world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT) && world instanceof ServerLevel) {
				long time = world.getDayTime() + 24000L;
				((ServerLevel) world).setDayTime(ForgeEventFactory.onSleepFinished((ServerLevel) world, time - time % 24000L, world.getDayTime()));
			}

			worldPlayers.stream().filter(LivingEntity::isSleeping).forEach(Player::stopSleeping);
			if (world.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && world.isRaining()) {
				((ServerLevel) world).resetWeatherCycle();
			}

			if (world instanceof ServerLevel)
				whenNightPasses((ServerLevel) world);
			ImprovedSleepingModule.sleepingPlayers.clear();
			return;
		}
		
		List<String> sleepingPlayers = new ArrayList<>();
		List<String> newSleepingPlayers = new ArrayList<>();
		List<String> wasSleepingPlayers = new ArrayList<>();
		List<String> nonSleepingPlayers = new ArrayList<>();
		int legitPlayers = 0;

		for(Player player : worldPlayers) {
			if (doesPlayerCountForSleeping(player)) {
				String name = player.getGameProfile().getName();
				if (isPlayerSleeping(player)) {
					if (!ImprovedSleepingModule.sleepingPlayers.contains(name))
						newSleepingPlayers.add(name);
					sleepingPlayers.add(name);
				} else {
					if (ImprovedSleepingModule.sleepingPlayers.contains(name))
						wasSleepingPlayers.add(name);
					nonSleepingPlayers.add(name);
				}

				legitPlayers++;
			}
		}

		ImprovedSleepingModule.sleepingPlayers = sleepingPlayers;

		if((!newSleepingPlayers.isEmpty() || !wasSleepingPlayers.isEmpty()) && worldPlayers.size() != 1) {
			boolean isDay = world.getSunAngle(0F) < 0.5;

			int requiredPlayers = Math.max((int) Math.ceil((legitPlayers * percentReq)), 0);

			TextComponent sibling = new TextComponent("(" + sleepingPlayers.size() + "/" + requiredPlayers + ")");

			TextComponent sleepingList = new TextComponent("");

			for(String s : sleepingPlayers)
				sleepingList.append(new TextComponent("\n\u2714 " + s).withStyle(ChatFormatting.GREEN));
			for(String s : nonSleepingPlayers)
				sleepingList.append(new TextComponent("\n\u2718 " + s).withStyle(ChatFormatting.RED));

			TranslatableComponent hoverText = new TranslatableComponent("quark.misc.sleeping_list_header", sleepingList);

			HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, hoverText.copy());
			sibling.setStyle(sibling.getStyle().withHoverEvent(hover));
			sibling.getStyle().setUnderlined(true);

			String newPlayer = newSleepingPlayers.isEmpty() ? wasSleepingPlayers.get(0) : newSleepingPlayers.get(0);
			String translationKey = isDay ?
					(newSleepingPlayers.isEmpty() ? "quark.misc.person_not_napping" : "quark.misc.person_napping") :
					(newSleepingPlayers.isEmpty() ? "quark.misc.person_not_sleeping" : "quark.misc.person_sleeping");

			TranslatableComponent message = new TranslatableComponent(translationKey, newPlayer);
			message.withStyle(ChatFormatting.GOLD);
			message.append(" ");

			message.append(sibling.copy());

			List<ServerPlayer> serverPlayers = new ArrayList<>(server.getPlayerList().getPlayers());
			for (ServerPlayer player : serverPlayers)
				SpamlessChatMessage.sendToPlayer(player, SLEEP_MSG, message);
		}
	}

	@SubscribeEvent
	public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		Level logoutWorld = event.getPlayer().level;
		// no copy as no loops are done
		List<? extends Player> players = logoutWorld.players();
		if(players.size() == 1) {
			Player lastPlayer = players.get(0);
			if(lastPlayer.getPersistentData().getBoolean(TAG_AFK)) {
				lastPlayer.getPersistentData().putBoolean(TAG_AFK, false);
				TranslatableComponent text = new TranslatableComponent("quark.misc.left_afk");
				text.withStyle(ChatFormatting.AQUA);

				if (lastPlayer instanceof ServerPlayer)
					SpamlessChatMessage.sendToPlayer(lastPlayer, AFK_MSG, text);
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onClientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END && Minecraft.getInstance().level != null) {
			timeSinceKeystroke++;

			if(timeSinceKeystroke == afkTime)
				QuarkNetwork.sendToServer(new UpdateAfkMessage(true));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeystroke(InputEvent.KeyInputEvent event) {
		registerPress();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onKeystroke(ScreenEvent.KeyboardKeyEvent event) {
		registerPress();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onPlayerClick(PlayerInteractEvent event) {
		registerPress();
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onMousePress(ScreenEvent.MouseInputEvent event) {
		registerPress();
	}

	private void registerPress() {
		if(timeSinceKeystroke >= afkTime && Minecraft.getInstance().level != null)
			QuarkNetwork.sendToServer(new UpdateAfkMessage(false));
		timeSinceKeystroke = 0;
	}

}
