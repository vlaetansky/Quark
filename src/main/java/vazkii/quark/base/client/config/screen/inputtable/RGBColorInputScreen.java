package vazkii.quark.base.client.config.screen.inputtable;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.gui.widget.Slider;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.module.config.type.inputtable.RGBAColorConfig;
import vazkii.quark.base.module.config.type.inputtable.RGBColorConfig;

public class RGBColorInputScreen extends AbstractInputtableConfigTypeScreen<RGBColorConfig> {

	private static final String COLOR_COMPONENTS = "RGBA";
	
	public RGBColorInputScreen(Screen parent, RGBColorConfig original, ConfigCategory category) {
		super(parent, original, category);
	}

	@Override
	protected void onInit() {
		int w = 70;
		int p = 12;
		int x = width / 2 - 33;
		int y = 55;

		Component prefix = new TextComponent("");
		Component suffix = new TextComponent("");
		
		int cnt = (original instanceof RGBAColorConfig ? 4 : 3);
		for(int i = 0; i < cnt; i++)
			addRenderableWidget(new Slider(x , y + 25 * (i++), w - p, 20, prefix, suffix, 0f, 2f, COLOR_COMPONENTS.charAt(i), false, false, onSlide(i)));
	}

	@Override
	public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
		super.render(mstack, mouseX, mouseY, partialTicks);

		int x = width / 2 - 203;
		int y = 10;
		int size = 60;

		int titleLeft = width / 2 + 66;
		drawCenteredString(mstack, font, new TextComponent(category.getGuiDisplayName()).withStyle(ChatFormatting.BOLD), titleLeft, 20, 0xFFFFFF);
		drawCenteredString(mstack, font, new TextComponent("Presets"), titleLeft, 155, 0xFFFFFF);

		int sliders = 0;
		boolean needsUpdate = false;
		for(Widget w : renderables)
			if(w instanceof Slider s) {
				if(mouseX < s.x || mouseY < s.y || mouseX >= s.x + s.getWidth() || mouseY >= s.y + s.getHeight())
					s.dragging = false;

//				double val = correct(s);
//				double curr = mutable.colorMatrix[sliders];
//				if(curr != val) {
//					mutable.colorMatrix[sliders] = val;
//					needsUpdate = true;
//				}

				String displayVal = String.format("%.2f", val);
				font.drawShadow(mstack, displayVal, s.x + (float) (s.getWidth() / 2 - font.width(displayVal) / 2) , s.y + 6, 0xFFFFFF);

				switch (sliders) {
					case 0 -> {
						font.drawShadow(mstack, "R =", s.x - 20, s.y + 5, 0xFF0000);
						font.drawShadow(mstack, "R", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0xFF0000);
					}
					case 1 -> font.drawShadow(mstack, "G", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x00FF00);
					case 2 -> font.drawShadow(mstack, "B", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x0077FF);
					case 3 -> font.drawShadow(mstack, "G =", s.x - 20, s.y + 5, 0x00FF00);
					case 6 -> font.drawShadow(mstack, "B =", s.x - 20, s.y + 5, 0x0077FF);
					default -> {
					}
				}
				if((sliders % 3) != 0)
					font.drawShadow(mstack, "+", s.x - 9, s.y + 5, 0xFFFFFF);

				sliders++;
			}
//
//		String[] biomes = { "plains", "forest", "mountains", "jungle", "savanna", "swamp" };
//		int[] colors = { 0xff91bd59, 0xff79c05a, 0xff8ab689, 0xff59c93c, 0xffbfb755, 0xff6a7039 };
//		int[] folliageColors = { 0xff77ab2f, 0xff59ae30, 0xff6da36b, 0xff30bb0b, 0xffaea42a, 0xff6a7039 };
//		for(int i = 0; i < biomes.length; i++) {
//			String name = biomes[i];
//			int color = colors[i];
//			int folliage = folliageColors[i];
//
//			int convolved = mutable.convolve(color);
//			int convolvedFolliage = mutable.convolve(folliage);
//
//			int cx = x + (i % 2) * (size + 5);
//			int cy = y + (i / 2) * (size + 5);
//
//			fill(mstack, cx - 1, cy - 1, cx + size + 1, cy + size + 1, 0xFF000000);
//			fill(mstack, cx, cy, cx + size, cy + size, convolved);
//			fill(mstack, cx + size / 2 - 1, cy + size / 2 - 1, cx + size, cy + size, 0x22000000);
//			fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage);
//
//			font.draw(mstack, name, cx + 2, cy + 2, 0x55000000);
//
//			minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.OAK_SAPLING), cx + size - 18, cy + size - 16);
//			mstack.pushPose();
//			mstack.translate(0, 0, 999);
//			fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage & 0x55FFFFFF);
//			mstack.popPose();
//		}

		if(needsUpdate)
			update();
	}

	private double correct(Slider s) {
		double val = s.getValue();
		val = correct(val, 1.0, s);
		val = correct(val, 0.5, s);
		val = correct(val, 1.5, s);
		return val;
	}

	private double correct(double val, double correct, Slider s) {
		if(Math.abs(val - correct) < 0.02) {
			s.setValue(correct);
			return correct;
		}
		return val;
	}

	private OnPress onSlide(final int idx) {
		return button -> {
			update();
		};
	}

	@Override
	protected void setDefault() {
		// TODO
	}

	@Override
	protected void reset() {
		// TODO
	}
}
