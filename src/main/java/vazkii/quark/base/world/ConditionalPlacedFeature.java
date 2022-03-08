package vazkii.quark.base.world;

import java.util.Random;
import java.util.function.BooleanSupplier;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ConditionalPlacedFeature extends PlacedFeature {

	public final PlacedFeature parent;
	public final BooleanSupplier condition;

	public ConditionalPlacedFeature(PlacedFeature parent, BooleanSupplier condition) {
		super(parent.feature(), parent.placement());
		this.parent = parent;
		this.condition = condition;
	}

	@Override // place
	public boolean place(WorldGenLevel p_242765_1_, ChunkGenerator p_242765_2_, Random p_242765_3_, BlockPos p_242765_4_) {
		return condition.getAsBoolean() && super.place(p_242765_1_, p_242765_2_, p_242765_3_, p_242765_4_);
	}

}
