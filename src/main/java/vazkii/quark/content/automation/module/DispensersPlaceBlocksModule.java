package vazkii.quark.content.automation.module;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class DispensersPlaceBlocksModule extends QuarkModule {

	@Config public static List<String> blacklist = Lists.newArrayList("minecraft:water", "minecraft:lava", "minecraft:fire");
	
	@Override
	public void loadComplete() {
		if(!enabled)
			return;
		
		Map<Item, IDispenseItemBehavior> registry = DispenserBlock.DISPENSE_BEHAVIOR_REGISTRY;

		Registry.BLOCK.forEach(block -> {
			if(!blacklist.contains(Objects.toString(Registry.BLOCK.getKey(block))))
			{
				Item item = block.asItem();
				if(item instanceof BlockItem && !registry.containsKey(item))
					registry.put(item, new BlockBehaviour((BlockItem) item));
			}
		});
	}

	public static class BlockBehaviour extends OptionalDispenseBehavior {

		private final BlockItem item;

		public BlockBehaviour(BlockItem item) {
			this.item = item;
		}

		@Nonnull
		@Override
		public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
			successful = false;

			Direction direction = source.getBlockState().get(DispenserBlock.FACING);
			Direction against = direction;
			BlockPos pos = source.getBlockPos().offset(direction);

			Block block = item.getBlock();
			if(block instanceof StairsBlock && direction.getAxis() != Axis.Y)
				direction = direction.getOpposite();
			else if(block instanceof SlabBlock)
				against = Direction.UP;

			successful = item.tryPlace(new NotStupidDirectionalPlaceContext(source.getWorld(), pos, direction, stack, against)) == ActionResultType.SUCCESS;

			return stack;
		}

	}

	// DirectionPlaceContext results in infinite loops when using slabs
	private static class NotStupidDirectionalPlaceContext extends DirectionalPlaceContext {

		protected boolean replaceClicked = true;

		public NotStupidDirectionalPlaceContext(World worldIn, BlockPos p_i50051_2_, Direction p_i50051_3_, ItemStack p_i50051_4_, Direction against) {
			super(worldIn, p_i50051_2_, p_i50051_3_, p_i50051_4_, against);
			replaceClicked = worldIn.getBlockState(func_242401_i().getPos()).isReplaceable(this); // func_242401_i = getRayTraceResult
		}
		
		@Override
		public boolean canPlace() {
			return replaceClicked;
		}

	}

}
