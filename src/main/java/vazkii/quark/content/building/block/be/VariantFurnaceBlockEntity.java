package vazkii.quark.content.building.block.be;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.building.module.VariantFurnacesModule;

import javax.annotation.Nonnull;

public class VariantFurnaceBlockEntity extends AbstractFurnaceBlockEntity {

	public VariantFurnaceBlockEntity(BlockPos p_155545_, BlockState p_155546_) {
		super(VariantFurnacesModule.blockEntityType, p_155545_, p_155546_, RecipeType.SMELTING);
	}

	@Nonnull
	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent("container.furnace");
	}

	@Nonnull
	@Override
	protected AbstractContainerMenu createMenu(int p_59293_, @Nonnull Inventory p_59294_) {
		return new FurnaceMenu(p_59293_, p_59294_, this, this.dataAccess);
	}

}
