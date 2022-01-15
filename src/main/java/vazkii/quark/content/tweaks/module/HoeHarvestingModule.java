package vazkii.quark.content.tweaks.module;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class HoeHarvestingModule extends QuarkModule {

	@Config
	public static boolean hoesCanHaveFortune = true;

	public static Tag<Item> bigHarvestingHoesTag;

	public static int getRange(ItemStack hoe) {
		if(!ModuleLoader.INSTANCE.isModuleEnabled(HoeHarvestingModule.class))
			return 1;

		if(!isHoe(hoe))
			return 1;
		else if (hoe.is(bigHarvestingHoesTag))
			return 3;
		else
			return 2;
	}
	
	public static boolean isHoe(ItemStack itemStack) {
		return !itemStack.isEmpty() && itemStack.getItem() instanceof HoeItem;
	}

	public static boolean canFortuneApply(Enchantment enchantment, ItemStack stack) {
		return enchantment == Enchantments.BLOCK_FORTUNE && hoesCanHaveFortune && isHoe(stack);
	}

	@Override
	public void setup() {
		bigHarvestingHoesTag = ItemTags.createOptional(new ResourceLocation(Quark.MOD_ID, "big_harvesting_hoes"));
	}

	@SubscribeEvent
	public void onBlockBroken(BlockEvent.BreakEvent event) {
		LevelAccessor world = event.getWorld();
		if(!(world instanceof Level))
			return;

		Player player = event.getPlayer();
		BlockPos basePos = event.getPos();
		ItemStack stack = player.getMainHandItem();
		if (isHoe(stack) && canHarvest(player, world, basePos, event.getState())) {
			int range = getRange(stack);

			for (int i = 1 - range; i < range; i++)
				for (int k = 1 - range; k < range; k++) {
					if (i == 0 && k == 0)
						continue;

					BlockPos pos = basePos.offset(i, 0, k);
					BlockState state = world.getBlockState(pos);
					if (canHarvest(player, world, pos, state)) {
						Block block = state.getBlock();
						if (block.canHarvestBlock(state, world, pos, player))
							block.playerDestroy((Level) world, player, pos, state, world.getBlockEntity(pos), stack);
						world.destroyBlock(pos, false);
						world.levelEvent(2001, pos, Block.getId(state));
					}
				}

			MiscUtil.damageStack(player, InteractionHand.MAIN_HAND, stack, 1);
		}
	}

	private boolean canHarvest(Player player, LevelAccessor world, BlockPos pos, BlockState state) {
		Block block = state.getBlock();
		if(block instanceof IPlantable) {
			IPlantable plant = (IPlantable) block;
			PlantType type = plant.getPlantType(world, pos);
			return type != PlantType.WATER && type != PlantType.DESERT;
		}

		Material mat = state.getMaterial();
		boolean isHarvestableMaterial =
					mat == Material.PLANT ||
					mat == Material.REPLACEABLE_FIREPROOF_PLANT ||
					mat == Material.REPLACEABLE_PLANT ||
					mat == Material.WATER_PLANT;
		return isHarvestableMaterial &&
				state.canBeReplaced(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(new Vec3(0.5, 0.5, 0.5), Direction.DOWN, pos, false))));
	}

}
