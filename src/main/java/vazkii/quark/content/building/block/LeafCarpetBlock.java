package vazkii.quark.content.building.block;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.quark.base.block.QuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class LeafCarpetBlock extends QuarkBlock implements IBlockColorProvider {

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 1, 16);

	private final BlockState baseState;
	private ItemStack baseStack;

	public LeafCarpetBlock(String name, Block base, QuarkModule module) {
		super(name + "_leaf_carpet", module, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.CLOTH_DECORATION)
				.strength(0F)
				.sound(SoundType.GRASS)
				.noOcclusion());

		baseState = base.defaultBlockState();

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT_MIPPED);
	}

	@Override
	public boolean canBeReplaced(@Nonnull BlockState state, @Nonnull BlockPlaceContext useContext) {
		return true;
	}

	@Nonnull
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return SHAPE;
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext p_220071_4_) {
		return Shapes.empty();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		if(baseStack == null)
			baseStack = new ItemStack(baseState.getBlock());

		return (stack, tintIndex) -> Minecraft.getInstance().getItemColors().getColor(baseStack, tintIndex);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockColor getBlockColor() {
		return (state, worldIn, pos, tintIndex) -> Minecraft.getInstance().getBlockColors().getColor(baseState, worldIn, pos, tintIndex);
	}

}
