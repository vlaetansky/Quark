package vazkii.quark.base.module.config.type.inputtable;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.api.config.IConfigElement;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;
import vazkii.quark.base.client.config.screen.inputtable.IInputtableConfigType;
import vazkii.quark.base.client.config.screen.inputtable.RGBColorInputScreen;
import vazkii.quark.base.client.config.screen.widgets.PencilButton;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.ConfigFlagManager;
import vazkii.quark.base.module.config.type.AbstractConfigType;

public class RGBColorConfig extends AbstractConfigType implements IInputtableConfigType<RGBColorConfig> {

	@Config double r;
	@Config double g;
	@Config double b;

	private int color;
	
	private RGBColorConfig(double r, double g, double b) {
		this(r, g, b, 1);
	}

	RGBColorConfig(double r, double g, double b, double a) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public static RGBColorConfig forColor(double r, double g, double b) {
		RGBColorConfig config = new RGBColorConfig(r, g, b);
		config.color = config.calculateColor();
		return config;
	}

	public int getColor() {
		return color;
	}
	
	public double getElement(int idx) {
		return switch(idx) {
		case 0 -> r;
		case 1 -> g;
		case 2 -> b;
		case 3 -> getAlphaComponent();
		default -> 0f;
		};
	}
	
	public void setElement(int idx, double c) {
		switch(idx) {
		case 0 -> r = c;
		case 1 -> g = c;
		case 2 -> b = c;
		case 3 -> setAlphaComponent(c);
		};
		
		color = calculateColor();
	}

	@Override
	public void onReload(ConfigFlagManager flagManager) {
		color = calculateColor();
	}
	
	int calculateColor() {
		int rComponent = clamp(r * 255) << 16;
		int gComponent = clamp(g * 255) << 8;
		int bComponent = clamp(b * 255);
		int aComponent = clamp(getAlphaComponent() * 255) << 24;
		return aComponent | bComponent | gComponent | rComponent;
	}

	double getAlphaComponent() {
		return 1.0;
	}
	
	void setAlphaComponent(double c) {
		// NO-OP
	}
	
	@Override
	public void inherit(RGBColorConfig other) {
		r = other.r;
		g = other.g;
		b = other.b;
		color = other.color;

		if(category != null) {
			category.refresh();
			category.updateDirty();
		}
	}

	@Override
	public RGBColorConfig copy() {
		RGBColorConfig newMatrix = new RGBColorConfig(r, g, b);
		newMatrix.inherit(this);
		return newMatrix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RGBColorConfig that = (RGBColorConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addWidgets(CategoryScreen parent, IConfigElement element, List<WidgetWrapper> widgets) {
		Minecraft minecraft = Minecraft.getInstance();
		widgets.add(new WidgetWrapper(new PencilButton(230, 3, b -> minecraft.setScreen(new RGBColorInputScreen(parent, this, element, category)))));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle() {
		return String.format("[%.1f, %.1f, %.1f]", r, g, b);
	}
	
	private static int clamp(double val) {
		return clamp((int) val);
	}

	private static int clamp(int val) {
		return Mth.clamp(val, 0, 0xFF);
	}

}
