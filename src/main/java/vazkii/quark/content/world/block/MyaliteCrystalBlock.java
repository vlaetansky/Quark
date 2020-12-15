package vazkii.quark.content.world.block;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import vazkii.arl.interf.IBlockColorProvider;
import vazkii.arl.util.ClientTicker;
import vazkii.quark.base.block.QuarkGlassBlock;
import vazkii.quark.base.handler.RenderLayerHandler;
import vazkii.quark.base.handler.RenderLayerHandler.RenderTypeSkeleton;
import vazkii.quark.base.module.QuarkModule;

public class MyaliteCrystalBlock extends QuarkGlassBlock implements IBlockColorProvider {

	public MyaliteCrystalBlock(QuarkModule module) {
		super("myalite_crystal", module, ItemGroup.DECORATIONS,
				Block.Properties.create(Material.GLASS, MaterialColor.PURPLE)
				.hardnessAndResistance(0.5F, 1200F)
				.sound(SoundType.GLASS)
				.setLightLevel(b -> 14)
				.harvestTool(ToolType.PICKAXE)
				.setRequiresTool()
				.harvestLevel(3)
				.tickRandomly()
				.notSolid());

		RenderLayerHandler.setRenderType(this, RenderTypeSkeleton.TRANSLUCENT);
	}
	
	private static int getColor(double t) {
    	double sp = 0.05;
    	double range = 0.28;
    	double shift = -0.025;
    	
    	double h = (Math.sin(t * sp) + 1.0) * (range / 2) - range + shift;
    	
		return Color.HSBtoRGB((float) h, 0.7F, 0.8F);
	}
	
    private static int getColor(BlockPos pos) {
		return getColor(pos.getX() + pos.getY() + pos.getZ());
    }
    
    private static float[] decompColor(int color) {
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = color & 0xFF;
        return new float[] { (float) r / 255.0F, (float) g / 255.0F, (float) b / 255.0F };
    }
    
	@Nullable
	@Override
	public float[] getBeaconColorMultiplier(BlockState state, IWorldReader world, BlockPos pos, BlockPos beaconPos) {
		return decompColor(getColor(pos));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Vector3d getFogColor(BlockState state, IWorldReader world, BlockPos pos, Entity entity, Vector3d originalColor, float partialTicks) {
		float[] color = decompColor(getColor(pos));
		return new Vector3d(color[0], color[1], color[2]);
	}
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public IBlockColor getBlockColor() {
		return (state, world, pos, tintIndex) -> getColor(pos);
	}
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public IItemColor getItemColor() {
		return (stack, tintIndex) -> getColor(ClientTicker.total);
	}
	
}
