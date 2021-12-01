package vazkii.quark.api;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IMagnetMoveAction {

	void onMagnetMoved(Level world, BlockPos pos, Direction direction, BlockState state, BlockEntity tile);
	
}
