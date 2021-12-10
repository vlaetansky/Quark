package vazkii.quark.content.world.gen.structure;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import vazkii.quark.base.Quark;
import vazkii.quark.content.world.module.BigDungeonModule;

public class BigDungeonStructure extends StructureFeature<JigsawConfiguration> {

	private static final String NAMESPACE = "big_dungeon";

	public BigDungeonStructure(Codec<JigsawConfiguration> codec) {
		super(codec, context -> {
			if(!checkLocation(context))
				return Optional.empty();

			return createGenerator(context);
		}, PostPlacementProcessor.NONE);
	}

	@Override
	public Decoration step() {
		return Decoration.UNDERGROUND_STRUCTURES;
	}

	private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
		int i = context.chunkPos().x >> 4;
		int j = context.chunkPos().z >> 4;
		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		worldgenrandom.nextInt();
		if (worldgenrandom.nextDouble() < BigDungeonModule.spawnChance) {
			return true;
		} else {
			return !isNearVillage(context.chunkGenerator(), context.seed(), context.chunkPos());
		}
	}

	private static boolean isNearVillage(ChunkGenerator gen, long seed, ChunkPos chunkPos) {
		StructureFeatureConfiguration structurefeatureconfiguration = gen.getSettings().getConfig(StructureFeature.VILLAGE);
		if (structurefeatureconfiguration == null) {
			return false;
		} else {
			int i = chunkPos.x;
			int j = chunkPos.z;

			for(int k = i - 10; k <= i + 10; ++k) {
				for(int l = j - 10; l <= j + 10; ++l) {
					ChunkPos chunkpos = StructureFeature.VILLAGE.getPotentialFeatureChunk(structurefeatureconfiguration, seed, k, l);
					if (k == chunkpos.x && l == chunkpos.z) {
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public String getFeatureName() {
		return getRegistryName().toString();
	}

	public static Optional<PieceGenerator<JigsawConfiguration>> createGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
		int i = context.chunkPos().x >> 4;
		int j = context.chunkPos().z >> 4;
		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		worldgenrandom.nextInt();
		int y = BigDungeonModule.minStartY + worldgenrandom.nextInt(BigDungeonModule.maxStartY - BigDungeonModule.minStartY); 
		
		BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(y);
		JigsawConfiguration newConfig = new JigsawConfiguration(() -> context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY) .get(new ResourceLocation(Quark.MOD_ID, NAMESPACE + "/starts")), BigDungeonModule.maxRooms);

		PieceGeneratorSupplier.Context<JigsawConfiguration> newContext = new PieceGeneratorSupplier.Context<>(
				context.chunkGenerator(),
				context.biomeSource(),
				context.seed(),
				context.chunkPos(),
				newConfig,
				context.heightAccessor(),
				context.validBiome(),
				context.structureManager(),
				context.registryAccess()
				);

		Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(newContext, PoolElementStructurePiece::new, blockpos, false, false);
		
		return structurePiecesGenerator;
	}

}
