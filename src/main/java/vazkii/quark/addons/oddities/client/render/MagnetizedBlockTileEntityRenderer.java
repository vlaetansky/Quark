package vazkii.quark.addons.oddities.client.render;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import vazkii.quark.addons.oddities.tile.MagnetizedBlockTileEntity;
import vazkii.quark.content.automation.client.render.QuarkPistonTileEntityRenderer;

@OnlyIn(Dist.CLIENT)
public class MagnetizedBlockTileEntityRenderer extends BlockEntityRenderer<MagnetizedBlockTileEntity> {

	private BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
	
	public MagnetizedBlockTileEntityRenderer(BlockEntityRenderDispatcher d) {
		super(d);
	}

	@Override
	public void render(MagnetizedBlockTileEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		Level world = tileEntityIn.getLevel();
		if (world != null) {
			BlockPos truepos = tileEntityIn.getBlockPos();
			BlockPos blockpos = truepos.relative(tileEntityIn.getFacing().getOpposite());
			BlockState blockstate = tileEntityIn.getMagnetState();
			if (!blockstate.isAir() && !(tileEntityIn.getProgress(partialTicks) >= 1.0F)) {
				BlockEntity subTile = tileEntityIn.getSubTile();
				Vec3 offset = new Vec3(tileEntityIn.getOffsetX(partialTicks), tileEntityIn.getOffsetY(partialTicks), tileEntityIn.getOffsetZ(partialTicks));
				if(QuarkPistonTileEntityRenderer.renderTESafely(world, truepos, blockstate, subTile, tileEntityIn, partialTicks, offset, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn))
					return;
				
				ModelBlockRenderer.enableCaching();
				matrixStackIn.pushPose();
				matrixStackIn.translate(offset.x, offset.y, offset.z);
				if (blockstate.getBlock() == Blocks.PISTON_HEAD && tileEntityIn.getProgress(partialTicks) <= 4.0F) {
					blockstate = blockstate.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(true));
					renderStateModel(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				} else {
					renderStateModel(blockpos, blockstate, matrixStackIn, bufferIn, world, false, combinedOverlayIn);
				}

				matrixStackIn.popPose();
				ModelBlockRenderer.clearCache();
			}
		}
	}

	private void renderStateModel(BlockPos p_228876_1_, BlockState p_228876_2_, PoseStack p_228876_3_, MultiBufferSource p_228876_4_, Level p_228876_5_, boolean p_228876_6_, int p_228876_7_) {
		RenderType.chunkBufferLayers().stream().filter(t -> ItemBlockRenderTypes.canRenderInLayer(p_228876_2_, t)).forEach(rendertype -> {
			ForgeHooksClient.setRenderLayer(rendertype);
			VertexConsumer ivertexbuilder = p_228876_4_.getBuffer(rendertype);
			if (blockRenderer == null) 
				blockRenderer = Minecraft.getInstance().getBlockRenderer();
			
			blockRenderer.getModelRenderer().tesselateBlock(p_228876_5_, blockRenderer.getBlockModel(p_228876_2_), p_228876_2_, p_228876_1_, p_228876_3_, ivertexbuilder, p_228876_6_, new Random(), p_228876_2_.getSeed(p_228876_1_), p_228876_7_);
		});
		ForgeHooksClient.setRenderLayer(null);
	}
}
