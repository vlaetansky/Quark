package vazkii.quark.base.handler;

import com.google.common.collect.HashBiMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import vazkii.quark.base.Quark;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = Quark.MOD_ID)
public final class ToolInteractionHandler {

	private static final Map<Block, Block> cleanToWaxMap = HashBiMap.create();
	private static final Map<ToolAction, Map<Block, Block>> interactionMaps = new HashMap<>();

	public static void registerWaxedBlock(Block clean, Block waxed) {
		cleanToWaxMap.put(clean, waxed);
		registerInteraction(ToolActions.AXE_WAX_OFF, waxed, clean);
	}

	public static void registerInteraction(ToolAction action, Block in, Block out) {
		if(!interactionMaps.containsKey(action))
			interactionMaps.put(action, new HashMap<>());

		Map<Block, Block> map = interactionMaps.get(action);
		map.put(in, out);
	}

	@SubscribeEvent
	public static void toolActionEvent(BlockEvent.BlockToolModificationEvent event) {
		ToolAction action = event.getToolAction();

		if(interactionMaps.containsKey(action)) {
			Map<Block, Block> map = interactionMaps.get(action);
			BlockState state = event.getState();
			Block block = state.getBlock();

			if(map.containsKey(block)) {
				Block finalBlock = map.get(block);
				event.setFinalState(copyState(state, finalBlock));
			}
		}
	}

	@SubscribeEvent
	public static void itemUse(PlayerInteractEvent.RightClickBlock event) {
		ItemStack stack = event.getItemStack();

		if(stack.getItem() == Items.HONEYCOMB) {
			BlockPos pos = event.getPos();
			Level world = event.getWorld();
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if(cleanToWaxMap.containsKey(block)) {
				Block alternate = cleanToWaxMap.get(block);

				if(!world.isClientSide)
					world.setBlockAndUpdate(pos, copyState(state, alternate));
				world.levelEvent(event.getPlayer(), 3003, pos, 0);

				if(!event.getPlayer().getAbilities().instabuild)
					stack.setCount(stack.getCount() - 1);

				event.setCanceled(true);
				event.setCancellationResult(InteractionResult.SUCCESS);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static BlockState copyState(BlockState original, Block newBlock) {
		BlockState retState = newBlock.defaultBlockState();
		for(Property prop : original.getProperties())
			if(retState.hasProperty(prop))
				retState = retState.setValue(prop, original.getValue(prop));

		return retState;

	}

}
