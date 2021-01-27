package vazkii.quark.content.building.block;

import java.util.function.BooleanSupplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.block.IQuarkBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class HedgeBlock extends FenceBlock implements IQuarkBlock, IBlockColorProvider {

	private final QuarkModule module;
	final Block leaf;
	private BooleanSupplier enabledSupplier = () -> true;
	
	public HedgeBlock(QuarkModule module, Block fence, Block leaf) {
		super(Block.Properties.from(fence));
		
		this.module = module;
		this.leaf = leaf;
		
		RegistryHelper.registerBlock(this, fence.getRegistryName().getPath().replaceAll("_fence", "_hedge"));
		RegistryHelper.setCreativeTab(this, ItemGroup.DECORATIONS);
		
		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.CUTOUT);
	}
	

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBlockColor getBlockColor() {
        final BlockColors colors = Minecraft.getInstance().getBlockColors();
        final BlockState leafState = leaf.getDefaultState();
        return (state, world, pos, tintIndex) -> colors.getColor(leafState, world, pos, tintIndex);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public IItemColor getItemColor() {
        final ItemColors colors = Minecraft.getInstance().getItemColors();
        final ItemStack leafStack = new ItemStack(leaf);
        return (stack, tintIndex) -> colors.getColor(leafStack, tintIndex);
    }

	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public HedgeBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

}
