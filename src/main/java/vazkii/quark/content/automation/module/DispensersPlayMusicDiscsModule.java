package vazkii.quark.content.automation.module;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.tileentity.JukeboxTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class DispensersPlayMusicDiscsModule extends QuarkModule {
	
	@Override
	public void setup() {
		MusicDiscBehaviour behaviour = new MusicDiscBehaviour();
		ForgeRegistries.ITEMS.forEach(i -> {
			if(i instanceof MusicDiscItem)
				DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY.put(i, behaviour);
		});
	}
	
	public class MusicDiscBehaviour extends OptionalDispenseBehavior {
		
		@Nonnull
		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			Direction dir = source.getBlockState().get(DispenserBlock.FACING);
			BlockPos pos = source.getBlockPos().offset(dir);
			World world = source.getWorld();
			BlockState state = world.getBlockState(pos);
			
			if(state.getBlock() == Blocks.JUKEBOX) {
				JukeboxTileEntity jukebox = (JukeboxTileEntity) world.getTileEntity(pos);
				if (jukebox != null) {
					ItemStack currentRecord = jukebox.getRecord();
					((JukeboxBlock) state.getBlock()).insertRecord(world, pos, state, stack);
					world.playEvent(null, 1010, pos, Item.getIdFromItem(stack.getItem()));

					return currentRecord;
				}
			}
			
			return super.dispenseStack(source, stack);
		}
		
	}

}
