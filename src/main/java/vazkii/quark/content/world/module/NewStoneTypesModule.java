package vazkii.quark.content.world.module;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.function.BooleanSupplier;

import com.google.common.collect.Maps;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkBlockWrapper;
import vazkii.quark.base.handler.VariantHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.base.module.config.type.DimensionConfig;
import vazkii.quark.base.world.WorldGenHandler;
import vazkii.quark.base.world.WorldGenWeights;
import vazkii.quark.base.world.generator.OreGenerator;
import vazkii.quark.content.world.block.MyaliteBlock;
import vazkii.quark.content.world.config.BigStoneClusterConfig;
import vazkii.quark.content.world.config.StoneTypeConfig;

@LoadModule(category = ModuleCategory.WORLD, hasSubscriptions = true)
public class NewStoneTypesModule extends QuarkModule {

	@Config(flag = "limestone") private static boolean enableLimestone = true;
	@Config(flag = "jasper") private static boolean enableJasper = true;
	@Config(flag = "slate") private static boolean enableSlate = true;
	@Config(flag = "myalite") private static boolean enableMyalite = true;

	public static boolean enabledWithLimestone, enabledWithJasper, enabledWithSlate, enabledWithMyalite;
	
	@Config public static StoneTypeConfig limestone = new StoneTypeConfig();
	@Config public static StoneTypeConfig jasper = new StoneTypeConfig();
	@Config public static StoneTypeConfig slate = new StoneTypeConfig();
	@Config public static StoneTypeConfig myalite = new StoneTypeConfig(DimensionConfig.end(false));

	public static Block limestoneBlock, jasperBlock, slateBlock, myaliteBlock;

	public static Map<Block, Block> polishedBlocks = Maps.newHashMap();
	
	private Queue<Runnable> defers = new ArrayDeque<>();
	
	@Override
	public void construct() {
		expandVanillaStone(Blocks.CALCITE, BigStoneClustersModule.calcite);
		
		limestoneBlock = makeStone("limestone", limestone, BigStoneClustersModule.limestone, () -> enableLimestone, MaterialColor.STONE);
		jasperBlock = makeStone("jasper", jasper, BigStoneClustersModule.jasper, () -> enableJasper, MaterialColor.TERRACOTTA_RED);
		slateBlock = makeStone("slate", slate, BigStoneClustersModule.slate, () -> enableSlate, MaterialColor.ICE);
		myaliteBlock = makeStone(null, "myalite", myalite, BigStoneClustersModule.myalite, () -> enableMyalite, MaterialColor.COLOR_PURPLE, MyaliteBlock::new);
	}
	
	private void expandVanillaStone(Block raw, BigStoneClusterConfig bigConfig) {
		makeStone(raw, raw.getRegistryName().getPath(), null, bigConfig, () -> true, null, QuarkBlock::new);
	}
	
	private Block makeStone(String name, StoneTypeConfig config, BigStoneClusterConfig bigConfig, BooleanSupplier enabledCond, MaterialColor color) {
		return makeStone(null, name, config, bigConfig, enabledCond, color, QuarkBlock::new);
	}
	
	private Block makeStone(final Block raw, String name, StoneTypeConfig config, BigStoneClusterConfig bigConfig, BooleanSupplier enabledCond, MaterialColor color, QuarkBlock.Constructor<QuarkBlock> constr) {
		BooleanSupplier trueEnabledCond = () -> (!ModuleLoader.INSTANCE.isModuleEnabled(BigStoneClustersModule.class) || !bigConfig.enabled) && enabledCond.getAsBoolean();
		
		Block.Properties props;
		if(raw != null)
			props = Block.Properties.copy(raw);
		else 
			props = Block.Properties.of(Material.STONE, color)
				.requiresCorrectToolForDrops()
//				.harvestTool(ToolType.PICKAXE) TODO TAG
				.strength(1.5F, 6.0F); 
		
		Block normal;
		if(raw != null)
			normal = raw;
		else 
			normal = constr.make(name, this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(enabledCond);
		
		QuarkBlock polished = constr.make("polished_" + name, this, CreativeModeTab.TAB_BUILDING_BLOCKS, props).setCondition(enabledCond);
		polishedBlocks.put(normal, polished);

		VariantHandler.addSlabStairsWall(normal instanceof IQuarkBlock ? (IQuarkBlock) normal : new QuarkBlockWrapper(normal, this));
		VariantHandler.addSlabAndStairs(polished);
		
		if(raw == null) {
			defers.add(() ->
				WorldGenHandler.addGenerator(this, new OreGenerator(config.dimensions, config.oregen, normal.defaultBlockState(), OreGenerator.ALL_DIMS_STONE_MATCHER, trueEnabledCond), Decoration.UNDERGROUND_ORES, WorldGenWeights.NEW_STONES)
			);
		}
		
		return normal;
	}
	
	@Override
	public void configChanged() {
		enabledWithLimestone = enableLimestone && this.enabled;
		enabledWithJasper = enableJasper && this.enabled;
		enabledWithSlate = enableSlate && this.enabled;
		enabledWithMyalite = enableMyalite && this.enabled;
	}
	
	@Override
	public void setup() {
		while(!defers.isEmpty())
			defers.poll().run();
	}
	
}
