package vazkii.quark.base.block;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.interf.IItemColorProvider;
import vazkii.quark.base.handler.RenderLayerHandler;

/**
 * @author WireSegal
 * Created at 1:09 PM on 9/19/19.
 */
public class QuarkInheritedPaneBlock extends QuarkPaneBlock implements IQuarkBlock, IBlockColorProvider {

	public final IQuarkBlock parent;

	public QuarkInheritedPaneBlock(IQuarkBlock parent, String name, Block.Properties properties) {
		super(name, parent.getModule(), properties, null);

		this.parent = parent;
		RenderLayerHandler.setInherited(this, parent.getBlock());
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent, Block.Properties properties) {
		this(parent, Objects.toString(parent.getBlock().getRegistryName()) + "_pane", properties);
	}

	public QuarkInheritedPaneBlock(IQuarkBlock parent) {
		this(parent, Block.Properties.copy(parent.getBlock()));
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && parent.isEnabled();
	}

	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
		return parent.getBlock().getBeaconColorMultiplier(parent.getBlock().defaultBlockState(), world, pos, beaconPos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockColor getBlockColor() {
		return parent instanceof IBlockColorProvider ? ((IBlockColorProvider) parent).getBlockColor() : null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ItemColor getItemColor() {
		return parent instanceof IItemColorProvider ? ((IItemColorProvider) parent).getItemColor() : null;
	}
}
