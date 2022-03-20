package vazkii.quark.base.module.config.type.inputtable;

import java.util.Objects;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.module.config.Config;

public class RGBAColorConfig extends RGBColorConfig {

	@Config public double a;

	private RGBAColorConfig(double r, double g, double b, double a) {
		super(r, g, b, a);
		this.a = a;
	}
	
	public static RGBAColorConfig forColor(double r, double g, double b, double a) {
		RGBAColorConfig config = new RGBAColorConfig(r, g, b, a);
		config.calculateColor();
		return config;
	}

	@Override
	public double getAlphaComponent() {
		return a;
	}
	
	@Override
	public void inherit(RGBColorConfig other) {
		if(other instanceof RGBAColorConfig rgba)
			a = rgba.a;
		
		super.inherit(other);
	}

	@Override
	public RGBAColorConfig copy() {
		RGBAColorConfig newMatrix = new RGBAColorConfig(r, g, b, a);
		newMatrix.inherit(this);
		return newMatrix;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RGBAColorConfig that = (RGBAColorConfig) o;
		return Double.compare(that.r, r) == 0 && Double.compare(that.g, g) == 0 && Double.compare(that.b, b) == 0 && Double.compare(that.a, a) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(r, g, b, a);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public String getSubtitle() {
		return String.format("[%.1f, %.1f, %.1f, %.1f]", r, g, b, a);
	}

}
