package vazkii.quark.content.tweaks.module;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallSignBlock;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class NoteBlockMobSoundsModule extends QuarkModule {

	public static final Direction[] SKULL_SEARCH_FACINGS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.EAST,
			Direction.WEST
	};
	
	@Config
	public static boolean enableVocaloid = true;

	@SubscribeEvent
	public void noteBlockPlayed(NoteBlockEvent.Play event) {
		IWorld world = event.getWorld();
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
			
			int note = event.getVanillaNoteId() - 12;
			if(sound.getRegistryName().toString().startsWith("quark:voice"))
				note += 6;
			
			float pitch = (float) Math.pow(2.0, (float) note / 12.0);
			world.playSound(null, pos.up(), sound, SoundCategory.BLOCKS, 1F, pitch);
		}
	}

	public SoundEvent getSoundEvent(IWorld world, BlockPos pos, Direction direction) {
		BlockPos offPos = pos.offset(direction);
		BlockState state = world.getBlockState(offPos); 
		Block block = state.getBlock();
		
		if(block instanceof WallSkullBlock && state.get(WallSkullBlock.FACING) == direction) {
			if(block == Blocks.SKELETON_WALL_SKULL)
				return SoundEvents.ENTITY_SKELETON_AMBIENT;
			else if(block == Blocks.WITHER_SKELETON_WALL_SKULL)
				return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
			else if(block == Blocks.ZOMBIE_WALL_HEAD)
				return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
			else if(block == Blocks.CREEPER_WALL_HEAD)
				return SoundEvents.ENTITY_CREEPER_PRIMED;
			else if(block == Blocks.DRAGON_WALL_HEAD)
				return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
		}
		
		if(enableVocaloid && block instanceof WallSignBlock && state.get(WallSignBlock.FACING) == direction) {
			TileEntity tile = world.getTileEntity(offPos);
			if(tile instanceof SignTileEntity) {
				SignTileEntity sign = (SignTileEntity) tile;
				String s = sign.signText[0].getString().trim();
				
				SoundEvent event = QuarkSounds.VOCAL_EVENTS.get(s);
				if(event != null)
					return event;
			}
		}
		
		return null;
	}
	
}
