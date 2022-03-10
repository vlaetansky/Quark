package vazkii.quark.content.automation.module;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@LoadModule(category = ModuleCategory.AUTOMATION)
public class DispensersPlaceBlocksModule extends QuarkModule {

	@Config public static List<String> blacklist = Lists.newArrayList("minecraft:water", "minecraft:lava", "minecraft:fire");

	@Override
	public void setup() {
		if(!enabled)
			return;

		BlockBehaviour behavior = new BlockBehaviour();

		enqueue(() -> {
			Map<Item, DispenseItemBehavior> registry = DispenserBlock.DISPENSER_REGISTRY;
			for(Block b : ForgeRegistries.BLOCKS) {
				ResourceLocation res = b.getRegistryName();
				if(!blacklist.contains(Objects.toString(res))) {
					Item item = b.asItem();
					if(item instanceof BlockItem && !registry.containsKey(item))
						registry.put(item, behavior);
				}
			}
		});
	}

	public static class BlockBehaviour extends OptionalDispenseItemBehavior {

		@Nonnull
		@Override
		public ItemStack execute(BlockSource source, ItemStack stack) {
			setSuccess(false);

			Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
			Direction against = direction;
			BlockPos pos = source.getPos().relative(direction);

			BlockItem item = (BlockItem) stack.getItem();
			Block block = item.getBlock();
			if(block instanceof StairBlock && direction.getAxis() != Axis.Y)
				direction = direction.getOpposite();
			else if(block instanceof SlabBlock)
				against = Direction.UP;

			setSuccess(item.place(new NotStupidDirectionalPlaceContext(source.getLevel(), pos, direction, stack, against)) == InteractionResult.SUCCESS);

			return stack;
		}

	}

	// DirectionPlaceContext results in infinite loops when using slabs
	private static class NotStupidDirectionalPlaceContext extends DirectionalPlaceContext {

		protected boolean replaceClicked;
		protected Direction direction;

		public NotStupidDirectionalPlaceContext(Level worldIn, BlockPos pos, Direction facing, ItemStack stack, Direction against) {
			super(worldIn, pos, facing, stack, against);
			replaceClicked = worldIn.getBlockState(getHitResult().getBlockPos()).canBeReplaced(this); // getHitResult = getRayTraceResult
			this.direction = facing;
		}

		@Override
		public boolean canPlace() {
			return replaceClicked;
		}

		@Nonnull
		@Override
		public Direction getNearestLookingDirection() {
			return direction.getOpposite();
		}

	}

}
