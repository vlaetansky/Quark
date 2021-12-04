package vazkii.quark.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import vazkii.quark.base.handler.StructureBlockReplacementHandler;

@Mixin(StructureStart.class)
public class StructureStartMixin {

	@Shadow
	@Final
	protected PiecesContainer pieceContainer;
	
	@Shadow
	@Final
	private StructureFeature<?> feature;
	
	@Inject(method = "placeInChunk", at = @At("HEAD"))
	public void injectReference(WorldGenLevel p_230366_1_, StructureFeatureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, BoundingBox p_230366_5_, ChunkPos p_230366_6_, CallbackInfo callback) {
		StructureBlockReplacementHandler.setActiveStructure(feature, pieceContainer);
	}
	
	@Inject(method = "placeInChunk", at = @At("RETURN"))
	public void resetReference(WorldGenLevel p_230366_1_, StructureFeatureManager p_230366_2_, ChunkGenerator p_230366_3_, Random p_230366_4_, BoundingBox p_230366_5_, ChunkPos p_230366_6_, CallbackInfo callback) {
		StructureBlockReplacementHandler.setActiveStructure(null, null);
	}
	
}