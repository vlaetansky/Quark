package vazkii.quark.base.handler;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Predicates;

import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.screen.AbstractQScreen;

@EventBusSubscriber(modid = Quark.MOD_ID)
public class MiscUtil {

	public static final ResourceLocation GENERAL_ICONS = new ResourceLocation(Quark.MOD_ID, "textures/gui/general_icons.png");

	public static final int BASIC_GUI_TEXT_COLOR = 0x404040;

	public static final Direction[] HORIZONTALS = new Direction[] {
			Direction.NORTH,
			Direction.SOUTH,
			Direction.WEST,
			Direction.EAST
	};

	public static final String[] OVERWORLD_VARIANT_WOOD_TYPES = new String[] {
			"spruce",
			"birch",
			"jungle",
			"acacia", 
			"dark_oak"
	};

	public static final String[] OVERWORLD_WOOD_TYPES = new String[] {
			"oak",
			"spruce",
			"birch",
			"jungle",
			"acacia", 
			"dark_oak"
	};

	public static final String[] NETHER_WOOD_TYPES = new String[] {
			"crimson",
			"warped"
	};
	
	public static final Block[] OVERWORLD_WOOD_OBJECTS = new Block[] {
			Blocks.OAK_PLANKS,
			Blocks.SPRUCE_PLANKS,
			Blocks.BIRCH_PLANKS,
			Blocks.JUNGLE_PLANKS,
			Blocks.ACACIA_PLANKS, 
			Blocks.DARK_OAK_PLANKS
	};

	public static final Block[] NETHER_WOOD_OBJECTS = new Block[] {
			Blocks.CRIMSON_PLANKS,
			Blocks.WARPED_PLANKS
	};

	public static void addToLootTable(LootTable table, LootPoolEntryContainer entry) {
		List<LootPool> pools = table.pools;
		if (!pools.isEmpty()) {
			LootPool firstPool = pools.get(0);
			LootPoolEntryContainer[] entries = firstPool.entries;
			
			LootPoolEntryContainer[] newEntries = new LootPoolEntryContainer[entries.length + 1];
			for(int i = 0; i < entries.length; i++)
				newEntries[i] = entries[i];
			
			newEntries[entries.length] = entry;
			firstPool.entries = newEntries;
		}
	}

	public static void damageStack(Player player, InteractionHand hand, ItemStack stack, int dmg) {
		stack.hurtAndBreak(dmg, player, (p) -> p.broadcastBreakEvent(hand));
	}

	public static <T, V> void editFinalField(Class<T> clazz, String fieldName, Object obj, V value) {
		Field f = ObfuscationReflectionHelper.findField(clazz, fieldName);
		editFinalField(f, obj, value);
	}

	public static <T> void editFinalField(Field f, Object obj, T value) {
		try {
			f.setAccessible(true);

			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);

			f.set(obj, value);
		} catch(ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void initializeEnchantmentList(Iterable<String> enchantNames, List<Enchantment> enchants) {
		enchants.clear();
		for(String s : enchantNames) {
			Registry.ENCHANTMENT.getOptional(new ResourceLocation(s)).ifPresent(enchants::add);
		}
	}

	public static Vec2 getMinecraftAngles(Vec3 direction) {
		// <sin(-y) * cos(p), -sin(-p), cos(-y) * cos(p)>

		direction = direction.normalize();

		double pitch = Math.asin(direction.y);
		double yaw = Math.asin(direction.x / Math.cos(pitch));

		return new Vec2((float) (pitch * 180 / Math.PI), (float) (-yaw * 180 / Math.PI));
	}

	public static boolean isEntityInsideOpaqueBlock(Entity entity) {
		BlockPos pos = entity.blockPosition();
		return !entity.noPhysics && entity.level.getBlockState(pos).isSuffocating(entity.level, pos);
	}

	public static boolean validSpawnLight(ServerLevelAccessor world, BlockPos pos, Random rand) {
		if (world.getBrightness(LightLayer.SKY, pos) > rand.nextInt(32)) {
			return false;
		} else {
			int light = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
			return light <= rand.nextInt(8);
		}
	}

	public static boolean validSpawnLocation(@Nonnull EntityType<? extends Mob> type, @Nonnull LevelAccessor world, MobSpawnType reason, BlockPos pos) {
		BlockPos below = pos.below();
		if (reason == MobSpawnType.SPAWNER)
			return true;
		BlockState state = world.getBlockState(below);
		return state.getMaterial() == Material.STONE && state.isValidSpawn(world, below, type);
	}

	public static <T> List<T> massRegistryGet(Collection<String> coll, Registry<T> registry) {
		return coll.stream().map(ResourceLocation::new).map(name -> registry.getOptional(name)).filter(Optional::isPresent).map(Optional::get).filter(Predicates.notNull()).collect(Collectors.toList());
	}

	public static void syncTE(BlockEntity tile) {
		Packet<ClientGamePacketListener> packet = tile.getUpdatePacket();

		if(packet != null && tile.getLevel() instanceof ServerLevel) {
			((ServerChunkCache) tile.getLevel().getChunkSource()).chunkMap
			.getPlayers(new ChunkPos(tile.getBlockPos()), false)
			.forEach(e -> e.connection.send(packet));
		}
	}

	public static BlockPos locateBiome(ServerLevel world, ResourceLocation biomeToFind, BlockPos start, int searchRadius, int searchIncrement) {
		Biome biome = world.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(biomeToFind).orElse(null);

		return biome == null ? null : world.findNearestBiome(biome, start, searchRadius, searchIncrement);
	}
	
	public static ItemStack putIntoInv(ItemStack stack, BlockEntity tile, Direction face, boolean simulate, boolean doSimulation) {
		IItemHandler handler = null;
		
		LazyOptional<IItemHandler> opt = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face); 
		if(opt.isPresent())
			handler = opt.orElse(null);
		else if(tile instanceof WorldlyContainer)
			handler = new SidedInvWrapper((WorldlyContainer) tile, face);
		else if(tile instanceof Container)
			handler = new InvWrapper((Container) tile);

		if(handler != null)
			return (simulate && !doSimulation) ? ItemStack.EMPTY : ItemHandlerHelper.insertItem(handler, stack, simulate);
		
		return stack;
	}
	
	public static boolean canPutIntoInv(ItemStack stack, BlockEntity tile, Direction face, boolean doSimulation) {
		return putIntoInv(stack, tile, face, true, doSimulation).isEmpty();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static int getGuiTextColor(String name) {
		return getGuiTextColor(name, BASIC_GUI_TEXT_COLOR);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static int getGuiTextColor(String name, int base) {
		int ret = base;
		
		String hex = I18n.get("quark.gui.color." + name);
		if(hex.matches("\\#[A-F0-9]{6}"))
			ret = Integer.valueOf(hex.substring(1), 16);
		return ret;
	}

	private static int progress;
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onKeystroke(KeyboardKeyPressedEvent.Pre event) {
		final String[] ids = new String[] {
				"-FCYE87P5L0","mybsDDymrsc","6a4BWpBJppI","thpTOAS1Vgg","ZNcBZM5SvbY","_qJEoSa3Ie0", 
				"RWeyOyY_puQ","VBbeuXW8Nko","LIDe-yTxda0","BVVfMFS3mgc","m5qwcYL8a0o","UkY8HvgvBJ8",
				"4K4b9Z9lSwc","tyInv6RWL0Q","tIWpr3tHzII","AFJPFfnzZ7w","846cjX0ZTrk","XEOCbFJjRw0",
				"GEo5bmUKFvI","b6li05zh3Kg", "_EEo-iE5u_A"
		};
		final int[] keys = new int[] { 265, 265, 264, 264, 263, 262, 263, 262, 66, 65 };
		if(event.getScreen() instanceof AbstractQScreen) {
			if(keys[progress] == event.getKeyCode()) {
				progress++;

				if(progress >= keys.length) {
					progress = 0;
					Util.getPlatform().openUri("https://www.youtube.com/watch?v=" + ids[new Random().nextInt(ids.length)]);
				}
			} else progress = 0;
		}
	}

}
