package vazkii.quark.base.handler;

import java.util.UUID;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class DebugHandler {
	
	private static void debug(PlayerInteractEvent.RightClickBlock event) {
		World world = event.getWorld();
		BlockPos pos = event.getPos();
		
		int base = 3 + world.rand.nextInt(2);
		int part2 = base + 8 + world.rand.nextInt(5);
		int part3 = part2 + 10 + world.rand.nextInt(5);
		int part4 = part3 + 12 + world.rand.nextInt(8);
		int y = 0;
		
		BlockState state = Blocks.BLACKSTONE.getDefaultState();
		pos = pos.up();
		
		for(; y < base; y++) {
			for(int i = -2; i <= 2; i++)
				for(int j = -2; j <= 2; j++)
				world.setBlockState(pos.add(i, y, j), state);
		}
		
		for(; y < part2; y++) {
			for(int i = -1; i <= 1; i++)
				for(int j = -1; j <= 1; j++)
					world.setBlockState(pos.add(i, y, j), state);
		}
		
		for(; y < part3; y++) {
			for(int i = -1; i <= 1; i++)
				for(int j = -1; j <= 1; j++)
					if(i == 0 | j == 0)
						world.setBlockState(pos.add(i, y, j), state);
		}
		
		for(; y < part4; y++)
			world.setBlockState(pos.add(0, y, 0), state);
		world.setBlockState(pos.add(0, y, 0), Blocks.GLOWSTONE.getDefaultState());
		
		int steps = 100;
		int substeps = 10;
		
		double spin = 0.12 + world.rand.nextDouble() * 0.16;
		double spread = 0.12 + world.rand.nextDouble() * 0.16;
		
		ImmutableSet<Block> VALID_BLOCKS = ImmutableSet.of(Blocks.RED_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS, Blocks.PINK_STAINED_GLASS);
		state = VALID_BLOCKS.asList().get(world.rand.nextInt(VALID_BLOCKS.size())).getDefaultState();
		
		for(int i = 0; i < (steps * substeps); i++) {
			double t = (double) i * spin;
			int x = (int) (Math.sin(t / substeps) * i * spread / substeps);
			int z = (int) (Math.cos(t / substeps) * i * spread / substeps);
			
			BlockPos next = pos.add(x, y, z);
			world.setBlockState(next, state);
		}
	}
	
	@SubscribeEvent
	public static void onUse(PlayerInteractEvent.RightClickBlock event) {
		if(Quark.DEBUG_MODE) {
			ItemStack stack = event.getItemStack();
			if(stack.getItem() == Items.STICK && !event.getWorld().isRemote) {
				event.getPlayer().sendMessage(new StringTextComponent("Executing Quark Debug"), UUID.randomUUID());
				debug(event);
			}
		}
	}

}
