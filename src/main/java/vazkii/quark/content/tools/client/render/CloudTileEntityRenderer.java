package vazkii.quark.content.tools.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.content.tools.tile.CloudTileEntity;

public class CloudTileEntityRenderer extends BlockEntityRenderer<CloudTileEntity> {

	public CloudTileEntityRenderer(BlockEntityRenderDispatcher p_i226006_1_) {
		super(p_i226006_1_);
	}

	@Override
	public void render(CloudTileEntity te, float pticks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
		Minecraft mc = Minecraft.getInstance();
		
		float scale = ((float) (te.liveTime - pticks + Math.sin(ClientTicker.total * 0.2F) * -10F) / 200F) * 0.6F;
		
		if(scale > 0) {
			matrix.translate(0.5, 0.5, 0.5);
			matrix.scale(scale, scale, scale);
			mc.getItemRenderer().renderStatic(new ItemStack(Blocks.WHITE_CONCRETE), TransformType.NONE, 240, OverlayTexture.NO_OVERLAY, matrix, buffer);
		}
	}

}
