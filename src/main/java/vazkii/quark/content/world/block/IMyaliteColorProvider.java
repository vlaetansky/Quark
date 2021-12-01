package vazkii.quark.content.world.block;

import java.awt.Color;
import java.util.stream.IntStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.interf.IBlockColorProvider;

public interface IMyaliteColorProvider extends IBlockColorProvider {
	
	static final PerlinSimplexNoise NOISE = new PerlinSimplexNoise(new WorldgenRandom(4543543), IntStream.rangeClosed(-4, 4));
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public default BlockColor getBlockColor() {
		return (state, world, pos, tintIndex) -> getColor(pos, myaliteS(), myaliteB());
	}
	
	@Override
    @OnlyIn(Dist.CLIENT)
	public default ItemColor getItemColor() {
		return (stack, tintIndex) -> {
			Minecraft mc = Minecraft.getInstance();
			if(mc.player == null)
				return getColor(BlockPos.ZERO, myaliteS(), myaliteB());
			
			BlockPos pos = mc.player.blockPosition();
			HitResult res = mc.hitResult;
			if(res != null && res instanceof BlockHitResult)
				pos = ((BlockHitResult) res).getBlockPos();
			
			return getColor(pos, myaliteS(), myaliteB());
		};
	}
	
	default float myaliteS() { return 0.7F; }
	default float myaliteB() { return 0.8F; }
	
	public static int getColor(BlockPos pos, float s, float b) {
		final double sp = 0.15;
    	final double range = 0.3;
    	final double shift = 0.05;
	
    	if(pos == null)
    		pos = BlockPos.ZERO;
    	
		double x = pos.getX() * sp;
		double y = pos.getY() * sp;
		double z = pos.getZ() * sp;
		
		double xv = x + Math.sin(z) * 2;
		double zv = z + Math.cos(x) * 2;
		double yv = y + Math.sin(y + Math.PI / 4) * 2;
		
		double noise = NOISE.getValue(xv + yv, zv + (yv * 2), false);
		
    	double h = noise * (range / 2) - range + shift;

		return Color.HSBtoRGB((float) h, s, b);
    }
	
}