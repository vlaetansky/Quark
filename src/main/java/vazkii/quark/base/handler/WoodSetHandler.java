package vazkii.quark.base.handler;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import net.minecraft.core.Direction;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ToolActions;
import vazkii.quark.base.Quark;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.block.QuarkDoorBlock;
import vazkii.quark.base.block.QuarkFenceBlock;
import vazkii.quark.base.block.QuarkFenceGateBlock;
import vazkii.quark.base.block.QuarkPillarBlock;
import vazkii.quark.base.block.QuarkPressurePlateBlock;
import vazkii.quark.base.block.QuarkStandingSignBlock;
import vazkii.quark.base.block.QuarkTrapdoorBlock;
import vazkii.quark.base.block.QuarkWallSignBlock;
import vazkii.quark.base.block.QuarkWoodenButtonBlock;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.item.QuarkSignItem;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.VariantBookshelfBlock;
import vazkii.quark.content.building.block.VariantLadderBlock;
import vazkii.quark.content.building.block.WoodPostBlock;
import vazkii.quark.content.building.module.VariantBookshelvesModule;
import vazkii.quark.content.building.module.VariantChestsModule;
import vazkii.quark.content.building.module.VariantLaddersModule;
import vazkii.quark.content.building.module.WoodenPostsModule;

public class WoodSetHandler {

	public static WoodSet addWoodSet(QuarkModule module, String name, MaterialColor color, MaterialColor barkColor) {
		WoodSet set = new WoodSet(module);
		WoodType type = WoodType.register(WoodType.create(Quark.MOD_ID + ":" + name));
		
		set.log = log(name + "_log", module, color, barkColor);
		set.wood = new QuarkPillarBlock(name + "_wood", module, CreativeModeTab.TAB_BUILDING_BLOCKS, BlockBehaviour.Properties.of(Material.WOOD, barkColor).strength(2.0F).sound(SoundType.WOOD));
		set.planks = new QuarkBlock(name + "_planks", module, CreativeModeTab.TAB_BUILDING_BLOCKS, Properties.of(Material.WOOD, color).strength(2.0F, 3.0F).sound(SoundType.WOOD));
		set.strippedLog = log("stripped_" + name + "_log", module, color, color);
		set.strippedWood = new QuarkPillarBlock("stripped_" + name + "_wood", module, CreativeModeTab.TAB_BUILDING_BLOCKS, BlockBehaviour.Properties.of(Material.WOOD, color).strength(2.0F).sound(SoundType.WOOD));

		set.slab = (Block) VariantHandler.addSlab((IQuarkBlock) set.planks);
		set.stairs = (Block) VariantHandler.addStairs((IQuarkBlock) set.planks);
		set.fence = new QuarkFenceBlock(name + "_fence", module, CreativeModeTab.TAB_DECORATIONS, BlockBehaviour.Properties.of(Material.WOOD, color).strength(2.0F, 3.0F).sound(SoundType.WOOD));
		set.fenceGate = new QuarkFenceGateBlock(name + "_fence_gate", module, CreativeModeTab.TAB_DECORATIONS, BlockBehaviour.Properties.of(Material.WOOD, color).strength(2.0F, 3.0F).sound(SoundType.WOOD));
		
		set.door = new QuarkDoorBlock(name + "_door", module, CreativeModeTab.TAB_DECORATIONS, BlockBehaviour.Properties.of(Material.WOOD, color).strength(3.0F).sound(SoundType.WOOD).noOcclusion());
		set.trapdoor = new QuarkTrapdoorBlock(name + "_trapdoor", module, CreativeModeTab.TAB_DECORATIONS, BlockBehaviour.Properties.of(Material.WOOD, color).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((s, g, p, e) -> false));
		
		set.button = new QuarkWoodenButtonBlock(name + "_button", module, BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD));
		set.pressurePlate = new QuarkPressurePlateBlock(Sensitivity.EVERYTHING, name + "_pressure_plate", module, CreativeModeTab.TAB_REDSTONE, BlockBehaviour.Properties.of(Material.WOOD, color).noCollission().strength(0.5F).sound(SoundType.WOOD));
		
		set.sign = new QuarkStandingSignBlock(name + "_sign", module, CreativeModeTab.TAB_DECORATIONS, type, BlockBehaviour.Properties.of(Material.WOOD, color).noCollission().strength(1.0F).sound(SoundType.WOOD));
		set.wallSign = new QuarkWallSignBlock(name + "_wall_sign", module, CreativeModeTab.TAB_DECORATIONS, type, BlockBehaviour.Properties.of(Material.WOOD, color).noCollission().strength(1.0F).sound(SoundType.WOOD).dropsLike(set.sign));

		set.bookshelf = new VariantBookshelfBlock(name, module, true).setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(VariantBookshelvesModule.class));
		set.ladder = new VariantLadderBlock(name, module, true).setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(VariantLaddersModule.class));
		set.post = new WoodPostBlock(module, set.fence, "", false).setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(WoodenPostsModule.class));
		set.strippedPost = new WoodPostBlock(module, set.fence, "stripped_", false).setCondition(() -> ModuleLoader.INSTANCE.isModuleEnabled(WoodenPostsModule.class));
		
		VariantChestsModule.addChest(name, module, Block.Properties.copy(Blocks.CHEST), true);
		
		set.signItem = new QuarkSignItem(module, set.sign, set.wallSign);
		set.boatItem = new QuarkItem(name + "_boat", module, new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)); // TODO make do stuff
		
		makeSignWork(set.sign, set.wallSign);
		
		ToolInteractionHandler.registerInteraction(ToolActions.AXE_STRIP, set.log, set.strippedLog);
		ToolInteractionHandler.registerInteraction(ToolActions.AXE_STRIP, set.wood, set.strippedWood);
		ToolInteractionHandler.registerInteraction(ToolActions.AXE_STRIP, set.post, set.strippedPost);

		return set;
	}
	
	public static void makeSignWork(Block sign, Block wallSign) {
		Set<Block> validBlocks = new HashSet<>();
		validBlocks.add(sign);
		validBlocks.add(wallSign);
		validBlocks.addAll(BlockEntityType.SIGN.validBlocks);
		BlockEntityType.SIGN.validBlocks = ImmutableSet.copyOf(validBlocks);
	}

	private static RotatedPillarBlock log(String name, QuarkModule module, MaterialColor topColor, MaterialColor sideColor) {
		return new QuarkPillarBlock(name, module, CreativeModeTab.TAB_BUILDING_BLOCKS,
				BlockBehaviour.Properties.of(Material.WOOD, s -> s.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? topColor : sideColor)
				.strength(2.0F).sound(SoundType.WOOD));
	}

	public static class WoodSet {

		public Block log, wood, planks, strippedLog, strippedWood,
		slab, stairs, fence, fenceGate,
		door, trapdoor, button, pressurePlate, sign, wallSign, 
		bookshelf, ladder, post, strippedPost;

		public Item signItem, boatItem;

		public final QuarkModule module;

		public WoodSet(QuarkModule module) {
			this.module = module;
		}

	}

}
