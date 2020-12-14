/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [06/06/2016, 01:40:29 (GMT)]
 */
package vazkii.quark.content.management.module;

import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Blocks;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.IngameGui;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.LinkItemMessage;

@LoadModule(category = ModuleCategory.MANAGEMENT, hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class ItemSharingModule extends QuarkModule {

	@Config
	public static boolean renderItemsInChat = true;

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void keyboardEvent(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		GameSettings settings = mc.gameSettings;
		if(InputMappings.isKeyDown(mc.getMainWindow().getHandle(), settings.keyBindChat.getKey().getKeyCode()) &&
				event.getGui() instanceof ContainerScreen && Screen.hasShiftDown()) {
			ContainerScreen gui = (ContainerScreen) event.getGui();

			Slot slot = gui.getSlotUnderMouse();
			if(slot != null && slot.inventory != null) {
				ItemStack stack = slot.getStack();

				if(!stack.isEmpty() && !MinecraftForge.EVENT_BUS.post(new ClientChatEvent(stack.getTextComponent().getString()))) {
					QuarkNetwork.sendToServer(new LinkItemMessage(stack));
					event.setCanceled(true);
				}
			}
		}
	}

	public static void linkItem(PlayerEntity player, ItemStack item) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(ItemSharingModule.class))
			return;

		if(!item.isEmpty() && player instanceof ServerPlayerEntity) {
			ITextComponent comp = item.getTextComponent();
			ITextComponent fullComp = new TranslationTextComponent("chat.type.text", player.getDisplayName(), comp);

			PlayerList players = ((ServerPlayerEntity) player).server.getPlayerList();

			ServerChatEvent event = new ServerChatEvent((ServerPlayerEntity) player, comp.getString(), fullComp);
			if (!MinecraftForge.EVENT_BUS.post(event)) {
				players.func_232641_a_(fullComp, ChatType.CHAT, player.getUniqueID());

				ServerPlayNetHandler handler = ((ServerPlayerEntity) player).connection;
				int threshold = handler.chatSpamThresholdCount;
				threshold += 20;

				if (threshold > 200 && !players.canSendCommands(player.getGameProfile()))
					handler.onDisconnect(new TranslationTextComponent("disconnect.spam"));

				handler.chatSpamThresholdCount = threshold;
			}
		}

	}

	private static int chatX, chatY;

	public static IFormattableTextComponent createStackComponent(ItemStack stack, IFormattableTextComponent component) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(ItemSharingModule.class) || !renderItemsInChat)
			return component;
		Style style = component.getStyle();
		if (stack.getCount() > 64) {
			ItemStack copyStack = stack.copy();
			copyStack.setCount(64);
			style = style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(copyStack)));
			component.mergeStyle(style);
		}

//		IFormattableTextComponent out = new StringTextComponent("   ");
//		out.func_230530_a_(style);
//		return out.func_230529_a_(component);
		return component;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void getChatPos(RenderGameOverlayEvent.Chat event) {
		chatX = event.getPosX();
		chatY = event.getPosY();
	}

//	@SubscribeEvent
//	@OnlyIn(Dist.CLIENT) // TODO wire fix this thanks
//	public void renderSymbols(RenderGameOverlayEvent.Post event) {
//		if (!renderItemsInChat)
//			return;
//
//		Minecraft mc = Minecraft.getInstance();
//		IngameGui gameGui = mc.ingameGUI;
//		NewChatGui chatGui = gameGui.getChatGUI();
//		if (event.getType() == RenderGameOverlayEvent.ElementType.CHAT) {
//			int updateCounter = gameGui.getTicks();
//			List<ChatLine<IReorderingProcessor>> lines = chatGui.drawnChatLines;
//			int shift = chatGui.scrollPos;
//
//			int idx = shift;
//
//			while (idx < lines.size() && (idx - shift) < chatGui.getLineCount()) {
//				ChatLine<IReorderingProcessor> line = lines.get(idx);
//				String before = "";
//
//				IReorderingProcessor lineProperties = line.getLineString();
//
//				if (lineProperties instanceof ITextComponent) { // <- wire: not a valid cast
//					ITextComponent lineComponent = (ITextComponent) lineProperties;
//
//					String currentText = TextFormatting.getTextWithoutFormattingCodes(lineComponent.getString());
//					if (currentText != null && currentText.startsWith("   "))
//						render(mc, chatGui, updateCounter, before, line, idx - shift, lineComponent);
//					before += currentText;
//
//					for (ITextComponent sibling : lineComponent.getSiblings()) {
//						currentText = TextFormatting.getTextWithoutFormattingCodes(sibling.getString());
//						if (currentText != null && currentText.startsWith("   "))
//							render(mc, chatGui, updateCounter, before, line, idx - shift, sibling);
//						before += currentText;
//					}
//				}
//
//				idx++;
//			}
//		}
//	}

//	@OnlyIn(Dist.CLIENT)
//	private static void render(Minecraft mc, NewChatGui chatGui, int updateCounter, String before, ChatLine line, int lineHeight, ITextComponent component) {
//		Style style = component.getStyle();
//		HoverEvent hoverEvent = style.getHoverEvent();
//		if (hoverEvent != null && hoverEvent.getAction() == HoverEvent.Action.field_230551_b_) {
//			HoverEvent.ItemHover contents = hoverEvent.func_240662_a_(HoverEvent.Action.field_230551_b_);
//
//			ItemStack stack = contents != null ? contents.func_240689_a_() : ItemStack.EMPTY;
//
//			if (stack.isEmpty())
//				stack = new ItemStack(Blocks.BARRIER); // for invalid icon
//
//			int timeSinceCreation = updateCounter - line.getUpdatedCounter();
//			if (chatGui.getChatOpen()) timeSinceCreation = 0;
//
//			if (timeSinceCreation < 200) {
//				double chatOpacity = mc.gameSettings.chatOpacity * 0.9f + 0.1f;
//				float fadeOut = MathHelper.clamp((1 - timeSinceCreation / 200f) * 10, 0, 1);
//				double alpha = fadeOut * fadeOut * chatOpacity;
//
//				int x = chatX + 3 + mc.fontRenderer.getStringWidth(before);
//				int y = chatY - mc.fontRenderer.FONT_HEIGHT * lineHeight;
//
//				if (alpha > 0) {
//					RenderHelper.enableStandardItemLighting();
//					alphaValue = ((int) (alpha * 255) << 24);
//
//					renderItemIntoGUI(mc, mc.getItemRenderer(), stack, x, y);
//
//					alphaValue = -1;
//					RenderHelper.disableStandardItemLighting();
//				}
//			}
//		}
//	}
//
//	public static int transformColor(int src) {
//		if (alphaValue == -1)
//			return src;
//		return (src & RGB_MASK) | alphaValue;
//	}
//
//	public static final int RGB_MASK = 0x00FFFFFF;
//	private static int alphaValue = -1;
//
//	@OnlyIn(Dist.CLIENT)
//	private static void renderItemIntoGUI(Minecraft mc, ItemRenderer render, ItemStack stack, int x, int y) {
//		renderItemModelIntoGUI(mc, render, stack, x, y, render.getItemModelWithOverrides(stack, null, null));
//	}
//
//	@OnlyIn(Dist.CLIENT)
//	private static void renderItemModelIntoGUI(Minecraft mc, ItemRenderer render, ItemStack stack, int x, int y, IBakedModel model) {
//		TextureManager textureManager = mc.getTextureManager();
//
//		RenderSystem.pushMatrix();
//		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);
//		RenderSystem.enableRescaleNormal();
//		RenderSystem.enableAlphaTest();
//		RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
//		RenderSystem.enableBlend();
//		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//		RenderSystem.translatef(x - 2, y - 2, -2);
//		RenderSystem.scalef(0.65f, 0.65f, 0.65f);
//		render.renderItemIntoGUI(stack, 0, 0);
//		RenderSystem.disableAlphaTest();
//		RenderSystem.disableRescaleNormal();
//		RenderSystem.disableLighting();
//		RenderSystem.popMatrix();
//		textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
//		textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
//	}

}
