package vazkii.quark.base.module.config.type;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;
import vazkii.quark.base.client.config.screen.widgets.IWidgetProvider;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;

import java.util.List;
import java.util.Objects;

public class ColorNoAlphaConfig extends AbstractConfigType implements IWidgetProvider {

	@Config public double r;
	@Config public double g;
	@Config public double b;

	private int color;

	public ColorNoAlphaConfig(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;

		color = ColorConfig.calculateColor(r, g, b, 1);
	}

	public int getColor() {
		return color;
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		color = ColorConfig.calculateColor(r, g, b, 1);
	}

	public void inherit(ColorNoAlphaConfig other) {
		r = other.r;
		g = other.g;
		b = other.b;
		color = other.color;

		if(category != null) {
			category.refresh();
			category.updateDirty();
		}
	}

	public ColorNoAlphaConfig copy() {
		ColorNoAlphaConfig newMatrix = new ColorNoAlphaConfig(r, g, b);
		newMatrix.inherit(this);
		return newMatrix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ColorNoAlphaConfig that = (ColorNoAlphaConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addWidgets(CategoryScreen parent, List<WidgetWrapper> widgets) {
		// TODO give config screens
		Minecraft minecraft = Minecraft.getInstance();
//		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> minecraft.setScreen(new ColorInputScreen(parent, this, category)))));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle() {
		return String.format("[%.1f, %.1f, %.1f]", r, g, b);
	}

}
