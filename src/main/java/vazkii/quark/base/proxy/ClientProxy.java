package vazkii.quark.base.proxy;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigGuiHandler.ConfigGuiFactory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.client.config.screen.QuarkConfigHomeScreen;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.WoodSetHandler;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.IConfigCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.Month;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	public static boolean jingleBellsMotherfucker = false;

	@Override
	public void start() {
		LocalDateTime now = LocalDateTime.now();
		if(now.getMonth() == Month.DECEMBER && now.getDayOfMonth() >= 16 || now.getMonth() == Month.JANUARY && now.getDayOfMonth() <= 6)
			jingleBellsMotherfucker = true;

		super.start();

		ModuleLoader.INSTANCE.clientStart();

		ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((minecraft, screen) -> new QuarkConfigHomeScreen(screen)));

		copyProgrammerArtIfMissing();

		(new ExternalConfigHandler()).setAPIHandler();
	}

	@Override
	public void registerListeners(IEventBus bus) {
		super.registerListeners(bus);

		bus.addListener(this::clientSetup);
		bus.addListener(this::modelRegistry);
		bus.addListener(this::modelBake);
		bus.addListener(this::modelLayers);
		bus.addListener(this::textureStitch);
		bus.addListener(this::postTextureStitch);
	}

	public void clientSetup(FMLClientSetupEvent event) {
		RenderLayerHandler.init();
		WoodSetHandler.clientSetup(event);

		ModuleLoader.INSTANCE.clientSetup(event);
	}

	public void modelRegistry(ModelRegistryEvent event) {
		ModuleLoader.INSTANCE.modelRegistry();
	}

	public void modelBake(ModelBakeEvent event) {
		ModuleLoader.INSTANCE.modelBake(event);
	}

	public void modelLayers(EntityRenderersEvent.AddLayers event) {
		ModuleLoader.INSTANCE.modelLayers(event);
	}

	public void textureStitch(TextureStitchEvent.Pre event) {
		ModuleLoader.INSTANCE.textureStitch(event);
	}

	public void postTextureStitch(TextureStitchEvent.Post event) {
		ModuleLoader.INSTANCE.postTextureStitch(event);
	}

	@Override
	public void handleQuarkConfigChange() {
		super.handleQuarkConfigChange();

		ModuleLoader.INSTANCE.configChangedClient();
		IngameConfigHandler.INSTANCE.refresh();

		Minecraft mc = Minecraft.getInstance();
		mc.submit(() -> {
			if(mc.hasSingleplayerServer() && mc.player != null && mc.getSingleplayerServer() != null)
				for(int i = 0; i < 3; i++)
					mc.player.sendMessage(new TranslatableComponent("quark.misc.reloaded" + i).withStyle(i == 0 ? ChatFormatting.AQUA : ChatFormatting.WHITE), Util.NIL_UUID);
		});
	}

	@Override
	public InteractionResult useItemSided(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		if (player instanceof LocalPlayer lPlayer) {
			var mc = Minecraft.getInstance();
			if (mc.gameMode != null && mc.level != null)
				return mc.gameMode.useItemOn(lPlayer, mc.level, hand, hit);
		}
		return super.useItemSided(player, level, hand, hit);
	}

	@Override
	protected void initContributorRewards() {
		ContributorRewardHandler.getLocalName();
		super.initContributorRewards();
	}

	@Override
	public IConfigCallback getConfigCallback() {
		return IngameConfigHandler.INSTANCE;
	}

	private static void copyProgrammerArtIfMissing() {
		File dir = new File(".", "resourcepacks");
		File target = new File(dir, "Quark Programmer Art.zip");

		if(!target.exists())
			try {
				dir.mkdirs();
				InputStream in = Quark.class.getResourceAsStream("/assets/quark/programmer_art.zip");
				FileOutputStream out = new FileOutputStream(target);

				byte[] buf = new byte[16384];
				int len;
				while((len = in.read(buf)) > 0)
					out.write(buf, 0, len);

				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
