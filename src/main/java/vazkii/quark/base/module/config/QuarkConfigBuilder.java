package vazkii.quark.base.module.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class QuarkConfigBuilder implements IConfigBuilder {

	private final ForgeConfigSpec.Builder parent;
	
	public QuarkConfigBuilder(ForgeConfigSpec.Builder parent) {
		this.parent = parent;
	}

	@Override
	public <T> ForgeConfigSpec configure(Function<IConfigBuilder, T> func) {
		return parent.configure(b -> func.apply(this)).getRight();
	}
	
	@Override
	public void push(String s) {
		parent.push(s);
	}

	@Override
	public void pop() {
		parent.pop();
	}

	@Override
	public void comment(String s) {
		parent.comment(s);
	}

	@Override
	public ConfigValue<?> defineList(String name, List<?> default_, Predicate<Object> predicate) {
		return parent.defineList(name, default_, predicate);
	}

	@Override
	public ConfigValue<?> defineObj(String name, Object default_, Predicate<Object> predicate) {
		return parent.define(name, default_, predicate);
	}

	@Override
	public ConfigValue<Boolean> defineBool(String name, boolean default_) {
		return parent.define(name, default_);
	}

	@Override
	public ConfigValue<Integer> defineInt(String name, int default_) {
		return parent.define(name, default_);
	}
	
	@Override
	public ConfigValue<Double> defineDouble(String name, double default_) {
		return parent.define(name, default_);
	}

}
