package vazkii.quark.content.automation.block;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import vazkii.quark.base.block.QuarkButtonBlock;
import vazkii.quark.base.datagen.QuarkBlockStateProvider;
import vazkii.quark.base.module.QuarkModule;

import javax.annotation.Nonnull;

/**
 * @author WireSegal
 * Created at 9:14 PM on 10/8/19.
 */
public class MetalButtonBlock extends QuarkButtonBlock {

	private final int speed;
	private final String texName;

	public MetalButtonBlock(String regname, QuarkModule module, int speed, String texName) {
		super(regname, module, CreativeModeTab.TAB_REDSTONE,
				Block.Properties.of(Material.DECORATION)
						.noCollission()
						.strength(0.5F)
						.sound(SoundType.METAL));
		this.speed = speed;
		this.texName = texName;
	}

	@Override
	public int getPressDuration() {
		return speed;
	}

	@Nonnull
	@Override
	protected SoundEvent getSound(boolean powered) {
		return powered ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}

	@Override
	protected ResourceLocation gennedTexture(QuarkBlockStateProvider states) {
		return states.mcLoc("block/" + texName);
	}
}
