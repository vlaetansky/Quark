package vazkii.quark.content.world.module.underground;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.BeaconTileEntity.BeamSegment;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.common.BiomeDictionary;
import vazkii.quark.api.IIndirectConnector;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.QuarkInheritedPaneBlock;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.world.block.CaveCrystalBlock;
import vazkii.quark.content.world.block.CaveCrystalClusterBlock;
import vazkii.quark.content.world.config.UndergroundBiomeConfig;
import vazkii.quark.content.world.gen.underground.CaveCrystalUndergroundBiome;

@LoadModule(category = ModuleCategory.WORLD)
public class CaveCrystalUndergroundBiomeModule extends UndergroundBiomeModule {

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalChance = 0.16;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalClusterChance = 0.2;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double crystalClusterOnSidesChance = 0.6;

	@Config
	@Config.Min(value = 0)
	@Config.Max(value = 1)
	public static double doubleCrystalChance = 0.2;

	@Config(description = "The chance that a crystal can grow, this is on average 1 in X world ticks, set to a higher value to make them grow slower. Minimum is 1, for every tick. Set to 0 to disable growth.")
	public static int caveCrystalGrowthChance = 5;

	@Config(flag = "cave_crystal_runes")
	public static boolean crystalsCraftRunes = true;

	@Config public static boolean enableBeaconRedirection = true;
	@Config public static boolean enableCollateralMovement = true;

	public static boolean staticEnabled;

	public static List<CaveCrystalBlock> crystals = Lists.newArrayList();
	public static ITag<Block> crystalTag;

	public static Block crystal(int floorIdx) {
		return crystals.get(MathHelper.clamp(floorIdx, 0, crystals.size() - 1));
	}

	@Override
	public void construct() {
		crystals.add(new CaveCrystalBlock("red_crystal", 0xff0000, this, MaterialColor.RED));
		crystals.add(new CaveCrystalBlock("orange_crystal", 0xff8000, this, MaterialColor.ADOBE));
		crystals.add(new CaveCrystalBlock("yellow_crystal", 0xffff00, this, MaterialColor.YELLOW));
		crystals.add(new CaveCrystalBlock("green_crystal", 0x00ff00, this, MaterialColor.GREEN));
		crystals.add(new CaveCrystalBlock("blue_crystal", 0x00ffff, this, MaterialColor.LIGHT_BLUE)); // *grumbling about the names of colors in the rainbow*
		crystals.add(new CaveCrystalBlock("indigo_crystal", 0x0000ff, this, MaterialColor.BLUE));
		crystals.add(new CaveCrystalBlock("violet_crystal", 0xff00ff, this, MaterialColor.MAGENTA));
		crystals.add(new CaveCrystalBlock("white_crystal", 0xffffff, this, MaterialColor.SNOW));
		crystals.add(new CaveCrystalBlock("black_crystal", 0x000000, this, MaterialColor.BLACK));

		for(CaveCrystalBlock block : crystals)
			new QuarkInheritedPaneBlock(block);

		for(CaveCrystalBlock block : crystals) {
			CaveCrystalClusterBlock cluster = new CaveCrystalClusterBlock(block);
			
			ClusterConnection connection = new ClusterConnection(cluster);
			IIndirectConnector.INDIRECT_STICKY_BLOCKS.add(Pair.of(connection::isValidState, connection));
		}

		super.construct();
	}

	@Override
	public void setup() {
		super.setup();
		crystalTag = BlockTags.createOptional(new ResourceLocation(Quark.MOD_ID, "crystal"));
	}

	@Override
	public void configChanged() {
		staticEnabled = enabled;
	}

	@Override
	protected String getBiomeName() {
		return "crystal";
	}

	@Override
	protected UndergroundBiomeConfig getBiomeConfig() {
		return new UndergroundBiomeConfig(new CaveCrystalUndergroundBiome(), 400, true, BiomeDictionary.Type.OCEAN)
				.setDefaultSize(42, 18, 22, 8);
	}

	// The value that comes out of this is fed onto a constant for the FOR loop that
	// computes the beacon segments, so we return 0 to run that code, or MAX_VALUE to not
	public static int tickBeacon(BeaconTileEntity beacon) {
		if(!staticEnabled || !enableBeaconRedirection)
			return 0; 

		World world = beacon.getWorld();
		BlockPos beaconPos = beacon.getPos();
		BlockPos currPos = beaconPos;

		int horizontalMoves = 64;
		int targetHeight = world.getHeight(Heightmap.Type.WORLD_SURFACE, beaconPos.getX(), beaconPos.getZ());

		beacon.beamColorSegments.clear();
		boolean broke = false;
		
		float[] currColor = new float[] { 1, 1, 1 };
		ExtendedBeamSegment currSegment = new ExtendedBeamSegment(Direction.UP, Vector3i.NULL_VECTOR, currColor);

		List<BlockPos> seenPositions = new LinkedList<>();
		boolean check = true;

		while(currPos.getY() < 256 && currPos.getY() > 0 && horizontalMoves > 0) {
			currPos = currPos.offset(currSegment.dir);
			if(currSegment.dir.getAxis().isHorizontal())
				horizontalMoves--;

			BlockState blockstate = world.getBlockState(currPos);
			Block block = blockstate.getBlock();
			float[] targetColor = blockstate.getBeaconColorMultiplier(world, currPos, beaconPos);
			
			if(block instanceof CaveCrystalClusterBlock) {
				Direction dir = blockstate.get(CaveCrystalClusterBlock.FACING);
				if(dir == currSegment.dir)
					currSegment.incrementHeight();
				else {
					check = true;
					beacon.beamColorSegments.add(currSegment);
					
					targetColor = ((CaveCrystalClusterBlock) block).base.colorComponents;
					if(targetColor[0] == 1F && targetColor[1] == 1F && targetColor[2] == 1F)
						targetColor = currColor;
					
					float[] mixedColor = new float[]{(currColor[0] + targetColor[0] * 3) / 4.0F, (currColor[1] + targetColor[1] * 3) / 4.0F, (currColor[2] + targetColor[2] * 3) / 4.0F};
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(dir, currPos.subtract(beaconPos), currColor);
				}
			} else if(targetColor != null) {
				if(Arrays.equals(targetColor, currColor))
					currSegment.incrementHeight();
				else {
					check = true;
					beacon.beamColorSegments.add(currSegment);

					float[] mixedColor = new float[]{(currColor[0] + targetColor[0]) / 2.0F, (currColor[1] + targetColor[1]) / 2.0F, (currColor[2] + targetColor[2]) / 2.0F};
					currColor = mixedColor;
					currSegment = new ExtendedBeamSegment(currSegment.dir, currPos.subtract(beaconPos), mixedColor);
				}
			} else {
				if (blockstate.getOpacity(world, currPos) >= 15 || block == Blocks.BEDROCK) {
					broke = true;
					break;
				}

				currSegment.incrementHeight();
			}
			
			if(check) {
				if(seenPositions.contains(currPos)) {
					broke = true;
					break;
				} else seenPositions.add(currPos);
			}
		}
		
		if(horizontalMoves == 0 || currPos.getY() <= 0)
			broke = true;

		if(!broke) {
			beacon.beamColorSegments.add(currSegment);
			beacon.beaconSize = targetHeight + 1;
		} else {
			beacon.beamColorSegments.clear();
			beacon.beaconSize = targetHeight;
		}
		

		return Integer.MAX_VALUE;
	}

	public static class ExtendedBeamSegment extends BeamSegment {

		public final Direction dir;
		public final Vector3i offset;
		
		private boolean isTurn = false;

		public ExtendedBeamSegment(Direction dir, Vector3i offset, float[] colorsIn) {
			super(colorsIn);
			this.offset = offset;
			this.dir = dir;
		}

		public void makeTurn() {
			isTurn = true;
		}
		
		public boolean isTurn() {
			return isTurn;
		}
		
		@Override
		public void incrementHeight() { // increase visibility
			super.incrementHeight();
		}

	}

	public static class ClusterConnection implements IIndirectConnector {

		final CaveCrystalClusterBlock cluster;
		
		public ClusterConnection(CaveCrystalClusterBlock cluster) {
			this.cluster = cluster;
		}
		
		@Override
		public boolean isEnabled() {
			return enableCollateralMovement;
		}
		
		private boolean isValidState(BlockState state) {
			return state.getBlock() == cluster; 
		}
		
		@Override
		public boolean canConnectIndirectly(World world, BlockPos ourPos, BlockPos sourcePos, BlockState ourState, BlockState sourceState) {
			BlockPos offsetPos = ourPos.offset(ourState.get(CaveCrystalClusterBlock.FACING).getOpposite());
			if(!offsetPos.equals(sourcePos))
				return false;
			
			return sourceState.getBlock() == cluster.base;
		}
		
	}
	
}
