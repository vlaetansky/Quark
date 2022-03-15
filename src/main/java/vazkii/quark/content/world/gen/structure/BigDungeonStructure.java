package vazkii.quark.content.world.gen.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.content.world.module.BigDungeonModule;

import java.util.Optional;

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
		if(!ModuleLoader.INSTANCE.isModuleEnabled(BigDungeonModule.class))
			return false;

		ChunkPos chunkpos = context.chunkPos();
		int i = chunkpos.getMiddleBlockX();
		int j = chunkpos.getMiddleBlockZ();

		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		worldgenrandom.nextInt();
		if (worldgenrandom.nextDouble() < BigDungeonModule.spawnChance) {
			return true;
		} else {
			return !context.chunkGenerator().hasFeatureChunkInRange(BuiltinStructureSets.VILLAGES, context.seed(), chunkpos.x, chunkpos.z, 10);
		}
	}

	public static Optional<PieceGenerator<JigsawConfiguration>> createGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
		int i = context.chunkPos().x >> 4;
		int j = context.chunkPos().z >> 4;
		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setSeed((long)(i ^ j << 4) ^ context.seed());
		worldgenrandom.nextInt();
		int y = BigDungeonModule.minStartY + worldgenrandom.nextInt(BigDungeonModule.maxStartY - BigDungeonModule.minStartY);

		BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(y);
		JigsawConfiguration newConfig = new JigsawConfiguration(Holder.direct(context.registryAccess().ownedRegistryOrThrow(Registry.TEMPLATE_POOL_REGISTRY) .get(new ResourceLocation(Quark.MOD_ID, NAMESPACE + "/starts"))), BigDungeonModule.maxRooms);

		PieceGeneratorSupplier.Context<JigsawConfiguration> newContext = new PieceGeneratorSupplier.Context<>(
				context.chunkGenerator(),
				context.biomeSource(),
				context.seed(),
				context.chunkPos(),
				newConfig,
				context.heightAccessor(),
				BigDungeonModule.biomeConfig::canSpawn,
				context.structureManager(),
				context.registryAccess()
				);

		Optional<PieceGenerator<JigsawConfiguration>> structurePiecesGenerator = JigsawPlacement.addPieces(newContext, PoolElementStructurePiece::new, blockpos, false, false);

		return structurePiecesGenerator;
	}

}
