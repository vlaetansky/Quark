package vazkii.quark.content.building.tile;

import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.AABB;
import vazkii.quark.content.building.module.VariantChestsModule;

public class VariantChestTileEntity extends ChestBlockEntity {

	protected VariantChestTileEntity(BlockEntityType<?> typeIn) {
		super(typeIn);
	}

	public VariantChestTileEntity() {
		super(VariantChestsModule.chestTEType);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1, worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
	}

}
