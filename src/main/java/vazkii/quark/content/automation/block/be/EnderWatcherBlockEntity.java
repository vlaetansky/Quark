package vazkii.quark.content.automation.tile;

import java.util.List;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.base.handler.RayTraceHandler;
import vazkii.quark.content.automation.block.EnderWatcherBlock;
import vazkii.quark.content.automation.module.EnderWatcherModule;

public class EnderWatcherTileEntity extends TileMod implements TickableBlockEntity {
	
	public EnderWatcherTileEntity() {
		super(EnderWatcherModule.enderWatcherTEType);
	}

	@Override
	public void tick() {
		BlockState state = getBlockState();
		boolean wasLooking = state.getValue(EnderWatcherBlock.WATCHED);
		int currWatch = state.getValue(EnderWatcherBlock.POWER);
		int range = 80;
		
		int newWatch = 0;
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition.offset(-range, -range, -range), worldPosition.offset(range, range, range)));
		
		boolean looking = false;
		for(Player player : players) {
			ItemStack helm = player.getItemBySlot(EquipmentSlot.HEAD);
			if(!helm.isEmpty() && helm.getItem() == Items.PUMPKIN)
				continue;

			HitResult result = RayTraceHandler.rayTrace(player, level, player, Block.OUTLINE, Fluid.NONE, 64);
			if(result != null && result instanceof BlockHitResult && ((BlockHitResult) result).getBlockPos().equals(worldPosition)) {
				looking = true;
				
				Vec3 vec = result.getLocation();
				Direction dir = ((BlockHitResult) result).getDirection();
				double x = Math.abs(vec.x - worldPosition.getX() - 0.5) * (1 - Math.abs(dir.getStepX()));
				double y = Math.abs(vec.y - worldPosition.getY() - 0.5) * (1 - Math.abs(dir.getStepY()));
				double z = Math.abs(vec.z - worldPosition.getZ() - 0.5) * (1 - Math.abs(dir.getStepZ()));
				
				// 0.7071067811865476 being the hypotenuse of an isosceles triangle with cathetus of length 0.5
				double fract = 1 - (Math.sqrt(x*x + y*y + z*z) / 0.7071067811865476);
				newWatch = Math.max(newWatch, (int) Math.ceil(fract * 15));
			}
		}
		
		if(!level.isClientSide && (looking != wasLooking || currWatch != newWatch))
			level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(EnderWatcherBlock.WATCHED, looking).setValue(EnderWatcherBlock.POWER, newWatch), 1 | 2);
		
		if(looking) {
			double x = worldPosition.getX() - 0.1 + Math.random() * 1.2;
			double y = worldPosition.getY() - 0.1 + Math.random() * 1.2;
			double z = worldPosition.getZ() - 0.1 + Math.random() * 1.2;

			level.addParticle(new DustParticleOptions(1.0F, 0.0F, 0.0F, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
		}
	}

}
