package vazkii.quark.base.module.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IConfigBuilder {

	public <T> ForgeConfigSpec configure(Function<IConfigBuilder, T> consumer);
	public void push(String s);
	public void pop();
	public void comment(String s);
	
	ForgeConfigSpec.ConfigValue<?> defineList(String name, List<?> default_, Predicate<Object> predicate);
	ForgeConfigSpec.ConfigValue<?> defineObj(String name, Object default_, Predicate<Object> predicate);
	
	ForgeConfigSpec.ConfigValue<Boolean> defineBool(String name, boolean default_);
	ForgeConfigSpec.ConfigValue<Integer> defineInt(String name, int default_);
	ForgeConfigSpec.ConfigValue<Double> defineDouble(String name, double default_);
}
