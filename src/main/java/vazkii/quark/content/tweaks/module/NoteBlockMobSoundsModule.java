package vazkii.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class NoteBlockMobSoundsModule extends QuarkModule {

	public static final Direction[] SKULL_SEARCH_FACINGS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.EAST,
			Direction.WEST
	};

	@SubscribeEvent
	public void noteBlockPlayed(NoteBlockEvent.Play event) {
		LevelAccessor world = event.getWorld();
		BlockPos pos = event.getPos();
		if(world.getBlockState(pos).getBlock() != Blocks.NOTE_BLOCK)
			return;

		SoundEvent sound = null;
		for(Direction dir : MiscUtil.HORIZONTALS) {
			sound = getSoundEvent(world, pos, dir);
			if(sound != null)
				break;
		}

		if(sound != null) {
			event.setCanceled(true);
			
			float pitch = (float) Math.pow(2.0, (event.getVanillaNoteId() - 12) / 12.0);
			world.playSound(null, pos.above(), sound, SoundSource.BLOCKS, 1F, pitch);
		}
	}

	public SoundEvent getSoundEvent(LevelAccessor world, BlockPos pos, Direction direction) {
		BlockState state = world.getBlockState(pos.relative(direction)); 
		Block block = state.getBlock();
		
		if(block instanceof WallSkullBlock && state.getValue(WallSkullBlock.FACING) == direction) {
			if(block == Blocks.SKELETON_WALL_SKULL)
				return SoundEvents.SKELETON_AMBIENT;
			else if(block == Blocks.WITHER_SKELETON_WALL_SKULL)
				return SoundEvents.WITHER_SKELETON_AMBIENT;
			else if(block == Blocks.ZOMBIE_WALL_HEAD)
				return SoundEvents.ZOMBIE_AMBIENT;
			else if(block == Blocks.CREEPER_WALL_HEAD)
				return SoundEvents.CREEPER_PRIMED;
			else if(block == Blocks.DRAGON_WALL_HEAD)
				return SoundEvents.ENDER_DRAGON_AMBIENT;
		}
		
		return null;
	}
	
}
