package vazkii.quark.base.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.datagen.QuarkBlockTagsProvider;
import vazkii.quark.base.datagen.QuarkItemTagsProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

public class QuarkWoodenButtonBlock extends QuarkButtonBlock {

	private final Block parent;

	public QuarkWoodenButtonBlock(String regname, Block parent, QuarkModule module, Properties properties) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE, properties);
		this.parent = parent;
	}

	@Nonnull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
	}

	@Override
	public int getPressDuration() {
		return 30;
	}

	@Override
	protected ResourceLocation gennedTexture(QuarkBlockStateProvider states) {
		return states.blockTexture(parent);
	}

	@Override
	public void dataGen(QuarkBlockTagsProvider blockTags) {
		super.dataGen(blockTags);
		blockTags.tag(BlockTags.WOODEN_BUTTONS).add(this);
	}

	@Override
	public void dataGen(QuarkItemTagsProvider itemTags) {
		super.dataGen(itemTags);
		itemTags.copyInto(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
	}
}
