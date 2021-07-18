package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.JukeboxTileEntity;
import vazkii.quark.content.tools.module.AmbientMusicDiscModule;

@Mixin(JukeboxTileEntity.class)
public class JukeboxTileEntityMixin {

	@Inject(method = "read", at = @At("TAIL"))
	public void read(BlockState state, CompoundNBT nbt, CallbackInfo info) {
		AmbientMusicDiscModule.onJukeboxLoad((JukeboxTileEntity) (Object) this);
	}
	
}
