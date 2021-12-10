package vazkii.quark.content.client.tooltip;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import vazkii.quark.content.client.module.ImprovedTooltipsModule;

public class MapTooltips {

	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

	@OnlyIn(Dist.CLIENT)
	public static void makeTooltip(RenderTooltipEvent.GatherComponents event) {
		ItemStack stack = event.getItemStack();
		if(!stack.isEmpty() && stack.getItem() instanceof MapItem) {
			List<Either<FormattedText, TooltipComponent>> tooltip = event.getTooltipElements();

			if(!ImprovedTooltipsModule.mapRequireShift || Screen.hasShiftDown())
				tooltip.add(1, Either.right(new MapComponent(stack)));
			else if(ImprovedTooltipsModule.mapRequireShift && !Screen.hasShiftDown())
				tooltip.add(1, Either.left(new TranslatableComponent("quark.misc.map_shift")));
		}
	}

	public static class MapComponent implements ClientTooltipComponent, TooltipComponent {

		private final ItemStack stack;

		public MapComponent(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public void renderImage(Font font, int tooltipX, int tooltipY, PoseStack pose, ItemRenderer itemRenderer, int something) {
			Minecraft mc = Minecraft.getInstance();

			MapItemSavedData mapdata = MapItem.getSavedData(stack, mc.level);
			Integer mapID = MapItem.getMapId(stack);

			if(mapdata == null)
				return;

			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.setShaderTexture(0, RES_MAP_BACKGROUND);

			int pad = 7;
			float size = 135;
			float scale = 0.5F;

			pose.pushPose();
			pose.translate(tooltipX + 3, tooltipY + 3, 500);
			pose.scale(scale, scale, 1F);
			RenderSystem.enableBlend();

			BufferBuilder buffer = Tesselator.getInstance().getBuilder();
			Matrix4f mat = pose.last().pose();

			buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
			buffer.vertex(mat, -pad, size, 0.0F).uv(0.0F, 1.0f).endVertex();
			buffer.vertex(mat, size, size, 0.0F).uv(1.0F, 1.0f).endVertex();
			buffer.vertex(mat, size, -pad, 0.0F).uv(1.0F, 0.0F).endVertex();
			buffer.vertex(mat, -pad, -pad, 0.0F).uv(0.0F, 0.0F).endVertex();
			buffer.end();
			BufferUploader.end(buffer);

			MultiBufferSource.BufferSource immediateBuffer = MultiBufferSource.immediate(buffer);
			mc.gameRenderer.getMapRenderer().render(pose, immediateBuffer, mapID, mapdata, true, 240);
			immediateBuffer.endBatch();
			pose.popPose();
		}

		@Override
		public int getHeight() {
			return 75;
		}

		@Override
		public int getWidth(Font font) {
			return 72;
		}
	}


}
