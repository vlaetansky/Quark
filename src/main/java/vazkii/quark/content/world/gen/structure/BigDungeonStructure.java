package vazkii.quark.content.world.gen.structure;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.feature.JigsawFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import vazkii.quark.base.Quark;
import vazkii.quark.base.world.JigsawRegistryHelper;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonChestProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonSpawnerProcessor;
import vazkii.quark.content.world.gen.structure.processor.BigDungeonWaterProcessor;
import vazkii.quark.content.world.module.BigDungeonModule;

import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

public class BigDungeonStructure extends JigsawFeature {

	private static final List<MobSpawnSettings.SpawnerData> ENEMIES = Lists.newArrayList(
			new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 8, 1, 3),
			new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 8, 1, 3),
			new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 8, 1, 3),
			new MobSpawnSettings.SpawnerData(EntityType.WITCH, 4, 1, 1),
			new MobSpawnSettings.SpawnerData(EntityType.ILLUSIONER, 10, 1, 1)
			);

	private static final String NAMESPACE = "big_dungeon";

	private static final String STARTS_DIR = "starts";
	private static final Set<String> STARTS = ImmutableSet.of(
			"3x3_pillars", "3x3_tnt", "3x3_water",
			"plus_barricade", "plus_ores", "plus_plain",
			"triplex_3sect", "triplex_lava", "triplex_plain");

	private static final String ROOMS_DIR = "rooms";
	private static final Set<String> ROOMS = ImmutableSet.of(
			"4room_plain", "4room_trapped",
			"ascend_intersection", "ascend_ruined", "ascend_plain",
			"climb_parkour", "climb_redstone", "climb_plain",
			"double_hall_plain", "double_hall_silverfish",
			"laddered_bridge", "laddered_tnt", "laddered_plain",
			"triple_library", "triple_plain",
			"connector_base", "connector_bush", "connector_fountain", "connector_melon", "connector_room");

	private static final String CORRIDORS_DIR = "corridors";
	private static final Set<String> CORRIDORS = ImmutableSet.of(
			"forward_cobweb", "forward_plain",
			"left_cobweb", "left_plain",
			"right_cobweb", "right_plain",
			"t_cobweb", "t_plain");

	private static final String ENDPOINT = "misc/endpoint";

	public static StructureTemplatePool startPattern;
	
	private static final BigDungeonChestProcessor CHEST_PROCESSOR = new BigDungeonChestProcessor();
	private static final BigDungeonSpawnerProcessor SPAWN_PROCESSOR = new BigDungeonSpawnerProcessor();
	private static final BigDungeonWaterProcessor WATER_PROCESSOR = new BigDungeonWaterProcessor();

	private static Codec<BigDungeonChestProcessor> CHEST_CODEC = Codec.unit(CHEST_PROCESSOR);
	private static Codec<BigDungeonSpawnerProcessor> SPAWN_CODEC = Codec.unit(SPAWN_PROCESSOR);
	private static Codec<BigDungeonWaterProcessor> WATER_CODEC = Codec.unit(WATER_PROCESSOR);

	public static StructureProcessorType<BigDungeonChestProcessor> CHEST_PROCESSOR_TYPE = () -> CHEST_CODEC;
	public static StructureProcessorType<BigDungeonSpawnerProcessor> SPAWN_PROCESSOR_TYPE = () -> SPAWN_CODEC;
	public static StructureProcessorType<BigDungeonWaterProcessor> WATER_PROCESSOR_TYPE = () -> WATER_CODEC;

	static {
		startPattern = JigsawRegistryHelper.pool(NAMESPACE, STARTS_DIR)
		.processor(CHEST_PROCESSOR, WATER_PROCESSOR)
		.addMult(STARTS_DIR, STARTS, 1)
		.register(Projection.RIGID);

		JigsawRegistryHelper.pool(NAMESPACE, ROOMS_DIR)
		.processor(CHEST_PROCESSOR, SPAWN_PROCESSOR, WATER_PROCESSOR)
		.addMult(ROOMS_DIR, ROOMS, 1)
		.register(Projection.RIGID);

		JigsawRegistryHelper.pool(NAMESPACE, CORRIDORS_DIR)
		.processor(WATER_PROCESSOR)
		.addMult(CORRIDORS_DIR, CORRIDORS, 1)
		.register(Projection.RIGID);

		final int roomWeight = 100;
		final int corridorWeight = 120;
		final double endpointWeightMult = 1.2;

		JigsawRegistryHelper.pool(NAMESPACE, "rooms_or_endpoint")
		.processor(CHEST_PROCESSOR, SPAWN_PROCESSOR, WATER_PROCESSOR)
		.addMult(ROOMS_DIR, ROOMS, roomWeight)
		.addMult(CORRIDORS_DIR, CORRIDORS, corridorWeight)
		.add(ENDPOINT, (int) ((ROOMS.size() * roomWeight + CORRIDORS.size() * corridorWeight) * endpointWeightMult))
		.register(Projection.RIGID);
	}

	public BigDungeonStructure(Codec<JigsawConfiguration> codec) {
		super(codec, 40, false, true);
		setRegistryName(Quark.MOD_ID, NAMESPACE);
	}

	public void setup() {
		Registry.register(Registry.STRUCTURE_PROCESSOR, Quark.MOD_ID + ":big_dungeon_chest", CHEST_PROCESSOR_TYPE);
		Registry.register(Registry.STRUCTURE_PROCESSOR, Quark.MOD_ID + ":big_dungeon_spawner", SPAWN_PROCESSOR_TYPE);
		Registry.register(Registry.STRUCTURE_PROCESSOR, Quark.MOD_ID + ":big_dungeon_water", WATER_PROCESSOR_TYPE);
	}

	@Override
	public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
		return ENEMIES;
	}

	@Override
	public Decoration step() {
		return Decoration.UNDERGROUND_STRUCTURES;
	}

	@Override // hasStartAt
	protected boolean isFeatureChunk(ChunkGenerator chunkGen, BiomeSource biomeProvider, long seed, WorldgenRandom rand, int chunkPosX, int chunkPosZ, Biome biome, ChunkPos chunkpos, JigsawConfiguration config) { 
		if(chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z && chunkGen.getSettings().structureConfig().containsKey(this) && BigDungeonModule.biomeConfig.canSpawn(biome)) {
			int i = chunkPosX >> 4;
			int j = chunkPosZ >> 4;
			rand.setSeed((long)(i ^ j << 4) ^ seed);
			rand.nextInt();
			return rand.nextDouble() < BigDungeonModule.spawnChance;
		}

		return !isNearVillage(chunkGen, seed, rand, chunkPosX, chunkPosZ);
	}

	// copy from PillagerOutpostStructure, seems to check village distancing
	private boolean isNearVillage(ChunkGenerator p_242782_1_, long p_242782_2_, WorldgenRandom p_242782_4_, int p_242782_5_, int p_242782_6_) {
		StructureFeatureConfiguration structureseparationsettings = p_242782_1_.getSettings().getConfig(StructureFeature.VILLAGE);
		if (structureseparationsettings == null) {
			return false;
		} else {
			for(int i = p_242782_5_ - 10; i <= p_242782_5_ + 10; ++i) {
				for(int j = p_242782_6_ - 10; j <= p_242782_6_ + 10; ++j) {
					ChunkPos chunkpos = StructureFeature.VILLAGE.getPotentialFeatureChunk(structureseparationsettings, p_242782_2_, p_242782_4_, i, j);
					if (i == chunkpos.x && j == chunkpos.z) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public StructureStartFactory<JigsawConfiguration> getStartFactory() {
		return (s, x, z, bb, r, sd) -> new BigDungeonStructure.Start(this, x, z, bb, r, sd);
	}

	@Override
	public String getFeatureName() {
		return getRegistryName().toString();
	}

	//	@Override
	//	public int getSize() {
	//		return (int) Math.ceil((double) BigDungeonModule.maxRooms / 1.5);
	//	}

	public static class Start extends JigsawFeature.FeatureStart {

		public Start(JigsawFeature structureIn, int chunkX, int chunkZ, BoundingBox boundsIn, int referenceIn, long seed) {
			super(structureIn, chunkX, chunkZ, boundsIn, referenceIn, seed);
		}

		@Override // init
		public void generatePieces(RegistryAccess dynreg, ChunkGenerator generator, StructureManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, JigsawConfiguration config) {
			super.generatePieces(dynreg, generator, templateManagerIn, chunkX, chunkZ, biomeIn, config);

			int maxTop = 60;
			if(boundingBox.y1 >= maxTop) {
				int shift = 5 + (boundingBox.y1 - maxTop);
				boundingBox.move(0, -shift, 0);
				pieces.forEach(p -> p.move(0, -shift, 0));
			}

			if(boundingBox.y0 < 6) {
				int shift = 6 - boundingBox.y0;
				boundingBox.move(0, shift, 0);
				pieces.forEach(p -> p.move(0, shift, 0));
			}

			pieces.removeIf(c -> c.getBoundingBox().y1 >= maxTop);			
		}

	}

//	public static class Piece extends AbstractVillagePiece {
//
//		public static IStructurePieceType PIECE_TYPE = Registry.register(Registry.STRUCTURE_PIECE, "bigdungeon", BigDungeonStructure.Piece::new);
//
//		public Piece(TemplateManager templateManagerIn, JigsawPiece jigsawPieceIn, BlockPos posIn, int p_i50560_4_, Rotation rotationIn, MutableBoundingBox boundsIn) {
//			super(PIECE_TYPE, templateManagerIn, jigsawPieceIn, posIn, p_i50560_4_, rotationIn, boundsIn);
//		}
//
//		public Piece(TemplateManager templateManagerIn, CompoundNBT nbt) {
//			super(templateManagerIn, nbt, PIECE_TYPE);
//		}
//
//	}

}
