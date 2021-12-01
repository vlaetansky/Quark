package vazkii.quark.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import vazkii.quark.content.tools.module.AmbientDiscsModule;

@Mixin(JukeboxBlockEntity.class)
public class JukeboxTileEntityMixin {

	@Inject(method = "read", at = @At("TAIL"))
	public void read(BlockState state, CompoundTag nbt, CallbackInfo info) {
		AmbientDiscsModule.onJukeboxLoad((JukeboxBlockEntity) (Object) this);
	}
	
}
