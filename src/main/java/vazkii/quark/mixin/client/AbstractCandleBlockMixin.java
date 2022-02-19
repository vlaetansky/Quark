package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import vazkii.quark.content.client.module.SoulCandlesModule;

@Mixin(AbstractCandleBlock.class)
public class AbstractCandleBlockMixin {

	@Redirect(method = "addParticlesAndSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"))
	private static void addParticlesAndSound(Level level, ParticleOptions options, double x, double y, double z, double mx, double my, double mz) {
		ParticleOptions newOptions = SoulCandlesModule.getParticleOptions(options, level, x, y, z);
		level.addParticle(newOptions, x, y, z, mx, my, mz);
	}

}
