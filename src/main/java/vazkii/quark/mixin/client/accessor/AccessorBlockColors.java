package vazkii.quark.mixin.client.accessor;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockColors.class)
public interface AccessorBlockColors {

	@Accessor("blockColors")
	Map<IRegistryDelegate<Block>, BlockColor> quark$getBlockColors();

}
