package vazkii.quark.content.client.tooltip;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.world.item.MapItem;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;

public class MapTooltips {

	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");


	@OnlyIn(Dist.CLIENT)
	public static void makeTooltip(ItemTooltipEvent event) {
		if(!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof MapItem) {
			if(ImprovedTooltipsModule.mapRequireShift && !Screen.hasShiftDown())
				event.getToolTip().add(1, new TranslatableComponent("quark.misc.map_shift"));
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static void renderTooltip(RenderTooltipEvent.PostText event) {
		if(!event.getStack().isEmpty() && event.getStack().getItem() instanceof MapItem && (!ImprovedTooltipsModule.mapRequireShift || Screen.hasShiftDown())) {
			Minecraft mc = Minecraft.getInstance();

			MapItemSavedData mapdata = MapItem.getOrCreateSavedData(event.getStack(), mc.level);
			if(mapdata == null)
				return;

			PoseStack ms = event.getMatrixStack();
			RenderSystem.color3f(1F, 1F, 1F);
			mc.getTextureManager().bind(RES_MAP_BACKGROUND);
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder buffer = tessellator.getBuilder();

			int pad = 7;
			float size = 135;
			float scale = 0.5F;

			ms.pushPose();
			ms.translate(event.getX(), event.getY() - size * scale - 5, 500);
			ms.scale(scale, scale, 1F);
			RenderSystem.enableBlend();

			Matrix4f mat = ms.last().pose();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormat.POSITION_TEX);
			buffer.vertex(mat, -pad, size, 0.0F).uv(0.0F, 1.0f).endVertex();
			buffer.vertex(mat, size, size, 0.0F).uv(1.0F, 1.0f).endVertex();
			buffer.vertex(mat, size, -pad, 0.0F).uv(1.0F, 0.0F).endVertex();
			buffer.vertex(mat, -pad, -pad, 0.0F).uv(0.0F, 0.0F).endVertex();
			tessellator.end();

			MultiBufferSource.BufferSource immediateBuffer = MultiBufferSource.immediate(buffer);
			mc.gameRenderer.getMapRenderer().render(ms, immediateBuffer, mapdata, true, 240);
			immediateBuffer.endBatch();
			ms.popPose();
		}
	}

}
