package vazkii.quark.content.automation.client.render;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import vazkii.quark.base.Quark;
import vazkii.quark.content.automation.module.PistonsMoveTileEntitiesModule;

public class QuarkPistonTileEntityRenderer {

	public static boolean renderPistonBlock(PistonMovingBlockEntity piston, float pTicks, PoseStack matrix, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if (!PistonsMoveTileEntitiesModule.staticEnabled || piston.getProgress(pTicks) > 1.0F) // TODO move this to a static get
			return false;

		BlockState state = piston.getMovedState();
		BlockPos truePos = piston.getBlockPos();
		BlockEntity tile = PistonsMoveTileEntitiesModule.getMovement(piston.getLevel(), truePos);
		Vec3 offset = new Vec3(piston.getXOff(pTicks), piston.getYOff(pTicks), piston.getZOff(pTicks));
		
		return renderTESafely(piston.getLevel(), truePos, state, tile, piston, pTicks, offset, matrix, bufferIn, combinedLightIn, combinedOverlayIn);
	}
	
	public static boolean renderTESafely(Level world, BlockPos truePos, BlockState state, BlockEntity tile, BlockEntity sourceTE, float pTicks, Vec3 offset, PoseStack matrix, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Block block = state.getBlock();
		String id = Objects.toString(block.getRegistryName());
		
		PoseStack.Pose currEntry = matrix.last();
		render: try {
			if(tile == null || (block == Blocks.PISTON_HEAD) || PistonsMoveTileEntitiesModule.renderBlacklist.contains(id))
				break render;
			
			matrix.pushPose();
			BlockEntityRenderer<BlockEntity> tileentityrenderer = BlockEntityRenderDispatcher.instance.getRenderer(tile);
			if(tileentityrenderer != null) {
				tile.setLevelAndPosition(sourceTE.getLevel(), sourceTE.getBlockPos());
				tile.clearRemoved();

				matrix.translate(offset.x, offset.y, offset.z);

				tile.blockState = state;
				tileentityrenderer.render(tile, pTicks, matrix, bufferIn, combinedLightIn, combinedOverlayIn);
			}
		} catch(Throwable e) {
			Quark.LOG.warn(id + " can't be rendered for piston TE moving", e);
			PistonsMoveTileEntitiesModule.renderBlacklist.add(id);
			return false;
		} finally {
			while(matrix.last() != currEntry)
				matrix.popPose();
		}
		
		return state.getRenderShape() != RenderShape.MODEL;
	}

}
