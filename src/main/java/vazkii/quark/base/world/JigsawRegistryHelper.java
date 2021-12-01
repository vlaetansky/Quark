package vazkii.quark.base.world;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import vazkii.quark.base.Quark;

public class JigsawRegistryHelper {

	public static final FakeAirProcessor FAKE_AIR = new FakeAirProcessor();

	private static Codec<FakeAirProcessor> fakeAirCodec = Codec.unit(FAKE_AIR);
	private static StructureProcessorType<FakeAirProcessor> fakeAirType = () -> fakeAirCodec;

	public static PoolBuilder pool(String namespace, String name) {
		return new PoolBuilder(namespace, name);
	}

	public static void setup() {
		Registry.register(Registry.STRUCTURE_PROCESSOR, Quark.MOD_ID + ":fake_air", fakeAirType);
	}

	public static class PoolBuilder {

		private final String namespace, name;
		private final List<PiecePrototype> pieces = new LinkedList<>();
		private final List<StructureProcessor> globalProcessors = new LinkedList<>();

		private PoolBuilder(String namespace, String name) {
			this.namespace = namespace;
			this.name = name;

			globalProcessors.add(FAKE_AIR);
		}

		public PoolBuilder processor(StructureProcessor... processors) {
			for(StructureProcessor p : processors)
				globalProcessors.add(p);
			return this;
		}

		public PoolBuilder add(String name, int weight) {
			pieces.add(new PiecePrototype(name, weight));
			return this;
		}

		public PoolBuilder add(String name, int weight, StructureProcessor... processors) {
			pieces.add(new PiecePrototype(name, weight, processors));
			return this;
		}

		public PoolBuilder addMult(String dir, Iterable<String> names, int weight) {
			String pref = dir.isEmpty() ? "" : (dir + "/");
			for(String s : names)
				add(pref + s, weight);
			return this;
		}

		public StructureTemplatePool register(StructureTemplatePool.Projection placementBehaviour) {
			ResourceLocation resource = new ResourceLocation(Quark.MOD_ID, namespace + "/" + name);

			List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> createdPieces = new LinkedList<>();
			for(PiecePrototype proto : pieces) {
				Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> newPiece =
						Pair.of(StructurePoolElement.single((Quark.MOD_ID + ":" + namespace + "/" + proto.name), new StructureProcessorList(proto.processors)), proto.weight);
				createdPieces.add(newPiece);
			}

			StructureTemplatePool pattern = new StructureTemplatePool(resource, new ResourceLocation("empty"), createdPieces, placementBehaviour);
			return Pools.register(pattern);
		}

		private class PiecePrototype {
			final String name;
			final int weight;
			final List<StructureProcessor> processors;

			public PiecePrototype(String name, int weight) {
				this(name, weight, new StructureProcessor[0]);
			}

			public PiecePrototype(String name, int weight, StructureProcessor... processors) {
				this.name = name;
				this.weight = weight;
				this.processors = Streams.concat(Arrays.stream(processors), globalProcessors.stream()).collect(ImmutableList.toImmutableList());
			}
		}

	}

	private static class FakeAirProcessor extends StructureProcessor {

		public FakeAirProcessor() { 
			// NO-OP
		}

		@Override
		public StructureBlockInfo process(LevelReader worldReaderIn, BlockPos pos, BlockPos otherposidk, StructureBlockInfo p_215194_3_, StructureBlockInfo blockInfo, StructurePlaceSettings placementSettingsIn, StructureTemplate template) {
			if(blockInfo.state.getBlock() == Blocks.BARRIER)
				return new StructureBlockInfo(blockInfo.pos, Blocks.CAVE_AIR.defaultBlockState(), new CompoundTag());

			else if(blockInfo.state.getValues().containsKey(BlockStateProperties.WATERLOGGED) && blockInfo.state.getValue(BlockStateProperties.WATERLOGGED))
				return new StructureBlockInfo(blockInfo.pos, blockInfo.state.setValue(BlockStateProperties.WATERLOGGED, false), blockInfo.nbt);

			return blockInfo;
		}

		@Override
		protected StructureProcessorType<?> getType() {
			return fakeAirType;
		}

	}

}


