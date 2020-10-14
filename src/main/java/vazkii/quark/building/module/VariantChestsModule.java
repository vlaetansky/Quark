package vazkii.quark.building.module;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.Module;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.building.block.VariantChestBlock;
import vazkii.quark.building.block.VariantTrappedChestBlock;
import vazkii.quark.building.client.render.VariantChestTileEntityRenderer;
import vazkii.quark.building.tile.VariantChestTileEntity;
import vazkii.quark.building.tile.VariantTrappedChestTileEntity;

@LoadModule(category = ModuleCategory.BUILDING, hasSubscriptions = true)
public class VariantChestsModule extends Module {

	private static final String DONK_CHEST = "Quark:DonkChest";

	private static final ImmutableSet<String> OVERWORLD_WOODS = ImmutableSet.copyOf(MiscUtil.OVERWORLD_WOOD_TYPES);
	private static final ImmutableSet<String> NETHER_WOODS = ImmutableSet.copyOf(MiscUtil.NETHER_WOOD_TYPES);
	
	private static final ImmutableSet<String> MOD_WOODS = ImmutableSet.of();

	public static TileEntityType<VariantChestTileEntity> chestTEType;
	public static TileEntityType<VariantTrappedChestTileEntity> trappedChestTEType;

	private static List<Supplier<Block>> chestTypes = new LinkedList<>();
	private static List<Supplier<Block>> trappedChestTypes = new LinkedList<>();
	
	// TODO track trapped chests too
	private static List<Block> allChests = new LinkedList<>();

	private static Structure<?> currentStructure;
	
	@Config
	private static boolean replaceWorldgenChests = true;
	
	@Override
	public void construct() {
		OVERWORLD_WOODS.forEach(s -> addChest(s, Blocks.CHEST));
		NETHER_WOODS.forEach(s -> addChest(s, Blocks.CHEST));
		MOD_WOODS.forEach(s -> addModChest(s, Blocks.CHEST));

		addChest("nether_brick", Blocks.NETHER_BRICKS);
		addChest("purpur", Blocks.PURPUR_BLOCK);
		addChest("prismarine", Blocks.PRISMARINE);
		addChest("mushroom", Blocks.RED_MUSHROOM_BLOCK);

		chestTEType = registerChests(VariantChestTileEntity::new, chestTypes);
		trappedChestTEType = registerChests(VariantTrappedChestTileEntity::new, trappedChestTypes);

		RegistryHelper.register(chestTEType, "variant_chest");
		RegistryHelper.register(trappedChestTEType, "variant_trapped_chest");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		ClientRegistry.bindTileEntityRenderer(chestTEType, VariantChestTileEntityRenderer::new);
		ClientRegistry.bindTileEntityRenderer(trappedChestTEType, VariantChestTileEntityRenderer::new);
	}
	
	public static void setActiveStructure(Structure<?> structure) {
		currentStructure = structure;
	}
	
	public static BlockState getGenerationChestBlockState(BlockState current) {
		if(replaceWorldgenChests && current.getBlock() == Blocks.CHEST && currentStructure != null) {
			ResourceLocation name = currentStructure.getRegistryName();
			
			int index = -1;
			switch(name.toString()) {
			case "minecraft:igloo":
				index = 1; // spruce
				break;
			case "minecraft:desert_pyramid":
				index = 2; // birch
				break;
			case "minecraft:jungle_pyramid":
				index = 3; // jungle
				break;
			case "minecraft:mansion":
			case "minecraft:pillager_outpost":
				index = 5; // dark oak
				break;
			case "minecraft:ruined_portal":
			case "minecraft:bastion_remnant":
				index = 6; // crimson
				break;
			case "minecraft:fortress":
				index = 8; // nether brick
				break;
			case "minecraft:endcity":
				index = 9; // purpur
				break;
			}
			
			if(index != -1)
				return allChests.get(index).getDefaultState().with(ChestBlock.FACING, current.get(ChestBlock.FACING));
		}
		
		return current;
	}

	private void addChest(String name, Block from) {
		addChest(name, Block.Properties.from(from));
	}

	private void addChest(String name, Block.Properties props) {
		chestTypes.add(() -> new VariantChestBlock(name, this, () -> chestTEType, props));
		trappedChestTypes.add(() -> new VariantTrappedChestBlock(name, this, () -> trappedChestTEType, props));
	}

	private void addModChest(String nameRaw, Block from) {
		String[] toks = nameRaw.split(":");
		String name = toks[1];
		String mod = toks[0];
		addModChest(name, mod, Block.Properties.from(from));
	}

	private void addModChest(String name, String mod, Block.Properties props) {
		chestTypes.add(() -> new VariantChestBlock.Compat(name, mod, this, () -> chestTEType, props));
		trappedChestTypes.add(() -> new VariantTrappedChestBlock.Compat(name, mod, this, () -> trappedChestTEType, props));
	}

	public static <T extends TileEntity> TileEntityType<T> registerChests(Supplier<? extends T> factory, List<Supplier<Block>> list) {
		List<Block> blockTypes = list.stream().map(Supplier::get).collect(Collectors.toList());
		allChests.addAll(blockTypes);
		return TileEntityType.Builder.<T>create(factory, blockTypes.toArray(new Block[blockTypes.size()])).build(null);
	}
	
	@Override
	public void textureStitch(TextureStitchEvent.Pre event) {
		if(event.getMap().getTextureLocation().toString().equals("minecraft:textures/atlas/chest.png")) {
			for(Block b : allChests)
				VariantChestTileEntityRenderer.accept(event, b);
		}
	}
	
	@SubscribeEvent
	public void onClickEntity(PlayerInteractEvent.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getHeldItem(event.getHand());

		if (!held.isEmpty() && target instanceof AbstractChestedHorseEntity) {
			AbstractChestedHorseEntity horse = (AbstractChestedHorseEntity) target;

			if (!horse.hasChest() && held.getItem() != Items.CHEST) {
				if (held.getItem().isIn(Tags.Items.CHESTS_WOODEN)) {
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.SUCCESS);

					if (!target.world.isRemote) {
						ItemStack copy = held.copy();
						copy.setCount(1);
						held.shrink(1);

						horse.getPersistentData().put(DONK_CHEST, copy.serializeNBT());

						horse.setChested(true);
						horse.initHorseChest();
						horse.playChestEquipSound();
					}
				}
			}
		}
	}

	private static final ThreadLocal<ItemStack> WAIT_TO_REPLACE_CHEST = new ThreadLocal<>();

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		Entity target = event.getEntityLiving();
		if (target instanceof AbstractChestedHorseEntity) {
			AbstractChestedHorseEntity horse = (AbstractChestedHorseEntity) target;
			ItemStack chest = ItemStack.read(horse.getPersistentData().getCompound(DONK_CHEST));
			if (!chest.isEmpty() && horse.hasChest())
				WAIT_TO_REPLACE_CHEST.set(chest);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		Entity target = event.getEntity();
		if (target instanceof ItemEntity && ((ItemEntity) target).getItem().getItem() == Items.CHEST) {
			ItemStack local = WAIT_TO_REPLACE_CHEST.get();
			if (local != null && !local.isEmpty())
				((ItemEntity) target).setItem(local);
			WAIT_TO_REPLACE_CHEST.remove();
		}
	}
	
	public static interface IChestTextureProvider {
		String getChestTexturePath();
		boolean isTrap();
	}

}
