package vazkii.quark.content.world.tile;

import java.util.List;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.content.world.module.MonsterBoxModule;

public class MonsterBoxTileEntity extends TileMod implements TickableBlockEntity {

	private int breakProgress;
	
	public MonsterBoxTileEntity() {
		super(MonsterBoxModule.tileEntityType);
	}
	
	@Override
	public void tick() {
		if(level.getDifficulty() == Difficulty.PEACEFUL)
			return;
		
		BlockPos pos = getBlockPos();
		
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		if(level.isClientSide)
			level.addParticle(breakProgress == 0 ? ParticleTypes.FLAME : ParticleTypes.LARGE_SMOKE, x + Math.random(), y + Math.random(), z + Math.random(), 0, 0, 0);
		
		boolean doBreak = breakProgress > 0;
		if(!doBreak) {
			List<? extends Player> players = level.players();
			for(Player p : players)
				if(p.distanceToSqr(x + 0.5, y + 0.5, z + 0.5) < 6.25 && !p.isSpectator()) {
					doBreak = true;
					break;
				}
		}
		
		if(doBreak) {
			if(breakProgress == 0) 
				level.playSound(null, pos, QuarkSounds.BLOCK_MONSTER_BOX_GROWL, SoundSource.BLOCKS, 0.5F, 1F);
			
			breakProgress++;
			if(breakProgress > 40) {
				level.levelEvent(2001, pos, Block.getId(level.getBlockState(pos)));
				level.removeBlock(pos, false);
				spawnMobs();
			}
		}
	}
	
	private void spawnMobs() {
		if(level.isClientSide)
			return;
		
		BlockPos pos = getBlockPos();

		int mobCount = MonsterBoxModule.minMobCount + level.random.nextInt(Math.max(MonsterBoxModule.maxMobCount - MonsterBoxModule.minMobCount + 1, 1));
		for(int i = 0; i < mobCount; i++) {
			LivingEntity e;
			
			float r = level.random.nextFloat();
			if(r < 0.1)
				e = new Witch(EntityType.WITCH, level);
			else if(r < 0.3)
				e = new CaveSpider(EntityType.CAVE_SPIDER, level);
			else e = new Zombie(level);
			
			double motionMultiplier = 0.4;
			e.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			double mx = (level.random.nextFloat() - 0.5) * motionMultiplier;
			double my = (level.random.nextFloat() - 0.5) * motionMultiplier;
			double mz = (level.random.nextFloat() - 0.5) * motionMultiplier;
			e.setDeltaMovement(mx, my, mz);
			e.getPersistentData().putBoolean(MonsterBoxModule.TAG_MONSTER_BOX_SPAWNED, true);
			
			level.addFreshEntity(e);
		}
	}

}