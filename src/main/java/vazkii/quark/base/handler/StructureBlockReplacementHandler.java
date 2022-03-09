package vazkii.quark.base.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;

public class StructureBlockReplacementHandler {

	public static List<StructureFunction> functions = new ArrayList<>();

	private static ThreadLocal<StructureHolder> structureHolder = new ThreadLocal<>();

	public static BlockState getResultingBlockState(BlockState blockstate) {
		StructureHolder curr = getCurrentSturctureHolder();

		if(curr != null && curr.currentStructure != null)
			for(StructureFunction fun : functions) {
				
				BlockState res = fun.apply(blockstate, curr);
				if(res != null)
					return res;
			}

		return blockstate;
	}

	private static StructureHolder getCurrentSturctureHolder() {
		return structureHolder.get();
	}

	public static void setActiveStructure(ConfiguredStructureFeature<?, ?> structure, PiecesContainer components) {
		StructureHolder curr = getCurrentSturctureHolder();
		if(curr == null) {
			curr = new StructureHolder();
			structureHolder.set(curr);
		}

		curr.currentStructure = structure == null ? null : structure.feature;
		curr.currentComponents = components == null ? null : components.pieces();
	}

	public static interface StructureFunction extends BiFunction<BlockState, StructureHolder, BlockState> {}

	public static class StructureHolder {
		public StructureFeature<?> currentStructure;
		public List<StructurePiece> currentComponents;
	}

}
