package vazkii.quark.api;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IIndirectConnector {
	
	public static List<Pair<Predicate<BlockState>, IIndirectConnector>> INDIRECT_STICKY_BLOCKS = new LinkedList<>();
	
	public default boolean isEnabled() {
		return true;
	}

	public default IConditionalSticky getStickyCondition() {
		return (w, pp, op, sp, os, ss, d) -> canConnectIndirectly(w, op, sp, os, ss);
	}
	
	public boolean canConnectIndirectly(Level world, BlockPos ourPos, BlockPos sourcePos, BlockState ourState, BlockState sourceState);
	
}
