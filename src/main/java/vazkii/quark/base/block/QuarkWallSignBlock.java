package vazkii.quark.base.block;

import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class QuarkWallSignBlock extends WallSignBlock implements IQuarkBlock {

	private final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkWallSignBlock(String regname, QuarkModule module, WoodType type, Properties properties) {
		super(properties, type);
		this.module = module;

		RegistryHelper.registerBlock(this, regname, false);
	}

	@Override
	public QuarkWallSignBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public void dataGen(QuarkBlockStateProvider blockTags) {
		// TODO
	}
}
