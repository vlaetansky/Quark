package vazkii.quark.base.block;

import java.util.function.BooleanSupplier;

import javax.annotation.Nullable;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class QuarkPaneBlock extends IronBarsBlock implements IQuarkBlock {
	
	public final QuarkModule module;
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkPaneBlock(String name, QuarkModule module, Block.Properties properties, RenderTypeSkeleton renderType) {
		super(properties);

		this.module = module;
		RegistryHelper.registerBlock(this, name);
		RegistryHelper.setCreativeTab(this, CreativeModeTab.TAB_DECORATIONS);
		
		if(renderType != null)
			RenderLayerHandler.setRenderType(this, renderType);
	}

	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {	
		if(group == CreativeModeTab.TAB_SEARCH || isEnabled())
			super.fillItemCategory(group, items);
	}

	@Nullable
	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public QuarkPaneBlock setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}


}