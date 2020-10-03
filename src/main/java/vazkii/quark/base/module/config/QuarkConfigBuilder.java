package vazkii.quark.base.module.config;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Predicates;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class QuarkConfigBuilder implements IConfigBuilder {

	private final ForgeConfigSpec.Builder parent;
	private final IConfigCallback callback;
	
	private String currComment = "";
	
	public QuarkConfigBuilder(ForgeConfigSpec.Builder parent, IConfigCallback callback) {
		this.parent = parent;
		this.callback = callback;
	}

	@Override
	public <T> ForgeConfigSpec configure(Function<IConfigBuilder, T> func) {
		return parent.configure(b -> func.apply(this)).getRight();
	}
	
	@Override
	public void push(String s) {
		parent.push(s);
		callback.push(s, currComment);
		currComment = "";
	}

	@Override
	public void pop() {
		parent.pop();
		callback.pop();
	}

	@Override
	public void comment(String s) {
		parent.comment(s);
		currComment = s;
	}

	@Override
	public ConfigValue<?> defineList(String name, List<?> default_, Predicate<Object> predicate) {
		onDefine(name, default_, predicate);
		return parent.defineList(name, default_, predicate);
	}

	@Override
	public ConfigValue<?> defineObj(String name, Object default_, Predicate<Object> predicate) {
		onDefine(name, default_, predicate);
		return parent.define(name, default_, predicate);
	}

	@Override
	public ConfigValue<Boolean> defineBool(String name, boolean default_) {
		onDefine(name, default_, Predicates.alwaysTrue());
		return parent.define(name, default_);
	}

	@Override
	public ConfigValue<Integer> defineInt(String name, int default_) {
		onDefine(name, default_, Predicates.alwaysTrue());
		return parent.define(name, default_);
	}
	
	@Override
	public ConfigValue<Double> defineDouble(String name, double default_) {
		onDefine(name, default_, Predicates.alwaysTrue());
		return parent.define(name, default_);
	}
	
	private void onDefine(String name, Object default_, Predicate<Object> predicate) {
		callback.addEntry(name, default_, currComment, predicate);
		currComment = "";
	}

}
