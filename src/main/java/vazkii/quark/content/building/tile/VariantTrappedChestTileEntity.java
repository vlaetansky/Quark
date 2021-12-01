package vazkii.quark.content.building.tile;

import vazkii.quark.content.building.module.VariantChestsModule;

public class VariantTrappedChestTileEntity extends VariantChestTileEntity {

	public VariantTrappedChestTileEntity() {
		super(VariantChestsModule.trappedChestTEType);
	}

	@Override
	protected void signalOpenCount() {
		super.signalOpenCount();
		this.level.updateNeighborsAt(this.worldPosition.below(), this.getBlockState().getBlock());
	}

}
