package vazkii.quark.content.building.module;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.building.block.BurntVineBlock;

// TODO CONTENT change to shear for parity
@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class BurnVinesModule extends QuarkModule {

	public static Block burnt_vine;
	
	@Override
	public void construct() {
		burnt_vine = new BurntVineBlock(this);
	}
	
	@SubscribeEvent
	public void onRightClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FLINT_AND_STEEL || stack.getItem() == Items.FIRE_CHARGE) {
			BlockPos pos = event.getPos();
			Level world = event.getWorld();
			BlockState state = world.getBlockState(pos);
			
			if(state.getBlock() == Blocks.VINE) {
				BlockState newState = burnt_vine.defaultBlockState();
				Map<Direction, BooleanProperty> map = VineBlock.PROPERTY_BY_DIRECTION;
				for(Direction d : map.keySet()) {
					BooleanProperty prop = map.get(d);
					newState = newState.setValue(prop, state.getValue(prop));
				}
				
				world.setBlockAndUpdate(pos, newState);
				
				BlockPos testPos = pos.below();
				BlockState testState = world.getBlockState(testPos);
				while(testState.getBlock() == Blocks.VINE) {
					world.removeBlock(testPos, false);
					testPos = testPos.below();
					testState = world.getBlockState(testPos);
				}
				
				world.playSound(event.getPlayer(), pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 0.5F, 1F);
				if(world instanceof ServerLevel) {
					ServerLevel sworld = (ServerLevel) world;
					sworld.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.25, 0.25, 0.25, 0.01);
					sworld.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0.25, 0.25, 0.25, 0.01);
				}
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
	
}
