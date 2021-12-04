package vazkii.quark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.quark.content.world.module.underground.CaveCrystalUndergroundBiomeModule;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

	// This captures the for loop inside tick that computes the beacon segments
	@ModifyConstant(method = "tick", constant = @Constant(intValue = 0))
	public int tick(int val, Level level, BlockPos pos, BlockState state, BeaconBlockEntity beacon) { // TODO FIX mixin 
		return CaveCrystalUndergroundBiomeModule.tickBeacon(beacon);
	}
	
}
