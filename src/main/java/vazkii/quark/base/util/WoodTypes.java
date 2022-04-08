package vazkii.quark.base.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class WoodTypes {

	public static record Wood(String name, Block planks, boolean nether) { }
	
	public static Wood OAK = new Wood("oak", Blocks.OAK_PLANKS, false);
	public static Wood SPRUCE = new Wood("spruce", Blocks.SPRUCE_PLANKS, false);
	public static Wood BIRCH = new Wood("birch", Blocks.BIRCH_PLANKS, false);
	public static Wood JUNGLE = new Wood("jungle", Blocks.JUNGLE_PLANKS, false);
	public static Wood ACACIA = new Wood("acacia", Blocks.ACACIA_PLANKS, false);
	public static Wood DARK_OAK = new Wood("dark_oak", Blocks.DARK_OAK_PLANKS, false);
	
	public static Wood CRIMSON = new Wood("crimson", Blocks.CRIMSON_PLANKS, false);
	public static Wood WARPED = new Wood("warped", Blocks.WARPED_PLANKS, false);

	public static final Wood[] OVERWORLD_NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK
	};

	public static final Wood[] OVERWORLD = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK
	};

	public static final Wood[] NETHER = new Wood[] {
			CRIMSON, WARPED
	};
	
	public static final Wood[] VANILLA = new Wood[] {
			OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED
	};
	
	public static final Wood[] VANILLA_NON_OAK = new Wood[] {
			SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK, CRIMSON, WARPED
	};
}
