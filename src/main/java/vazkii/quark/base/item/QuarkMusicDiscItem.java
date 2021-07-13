package vazkii.quark.base.item;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.QuarkModule;

public class QuarkMusicDiscItem extends MusicDiscItem implements IQuarkItem {

	private final QuarkModule module;
	private final boolean isAmbient; 
	private BooleanSupplier enabledSupplier = () -> true;

	public QuarkMusicDiscItem(int comparatorValue, Supplier<SoundEvent> sound, String name, QuarkModule module, boolean isAmbient) {
		super(comparatorValue, sound, (new Item.Properties()).maxStackSize(1).group(ItemGroup.MISC).rarity(Rarity.RARE));

		RegistryHelper.registerItem(this, "music_disc_" + name);
		this.module = module;
		this.isAmbient = isAmbient;
	}

	@Override
	public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
		if(isEnabled() || group == ItemGroup.SEARCH)
			super.fillItemGroup(group, items);
	}

	@Override
	public QuarkMusicDiscItem setCondition(BooleanSupplier enabledSupplier) {
		this.enabledSupplier = enabledSupplier;
		return this;
	}

	@Override
	public QuarkModule getModule() {
		return module;
	}

	@Override
	public boolean doesConditionApply() {
		return enabledSupplier.getAsBoolean();
	}

	@OnlyIn(Dist.CLIENT)
	public void onPlayed(@Nullable SoundEvent soundIn, BlockPos pos, WorldRenderer render, CallbackInfo info) {
		if(isAmbient) {
			SimpleSound simplesound = new SimpleSound(soundIn.getName(), SoundCategory.RECORDS, 4.0F, 1.0F, true, 0, ISound.AttenuationType.LINEAR, pos.getX(), pos.getY(), pos.getZ(), false);
	        render.mapSoundPositions.put(pos, simplesound);
	        Minecraft.getInstance().getSoundHandler().play(simplesound);
			
			info.cancel();
		}
	}

}
