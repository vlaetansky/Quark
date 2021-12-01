package vazkii.quark.content.tools.tile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.content.tools.module.BottledCloudModule;

public class CloudTileEntity extends TileMod implements TickableBlockEntity {

	private static final String TAG_LIVE_TIME = "liveTime";
	
	public int liveTime = -10000;
	
	public CloudTileEntity() {
		super(BottledCloudModule.tileEntityType);
	}

	@Override
	public void tick() {
		if(liveTime < -1000)
			liveTime = 200;
		
		if(liveTime > 0) {
			liveTime--;
			
			if(level.isClientSide && liveTime % 20 == 0)
				for(int i = 0; i < (10 - (200 - liveTime) / 20); i++)
					level.addParticle(ParticleTypes.CLOUD, worldPosition.getX() + Math.random(), worldPosition.getY() + Math.random(), worldPosition.getZ() + Math.random(), 0, 0, 0);
		} else {
			if(!level.isClientSide)
				level.removeBlock(getBlockPos(), false);
		}
	}
	
	@Override
	public void writeSharedNBT(CompoundTag cmp) {
		cmp.putInt(TAG_LIVE_TIME, liveTime);
	}
	
	@Override
	public void readSharedNBT(CompoundTag cmp) {
		liveTime = cmp.getInt(TAG_LIVE_TIME);
	}

}
