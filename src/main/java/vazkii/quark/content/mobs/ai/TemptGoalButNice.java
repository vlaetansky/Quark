/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jun 24, 2019, 22:47 AM (EST)]
 */
package vazkii.quark.content.mobs.ai;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.quark.content.mobs.module.FrogsModule;

public class TemptGoalButNice extends TemptGoal {
	private final Ingredient temptItemNice;

	public TemptGoalButNice(PathfinderMob temptedEntityIn, double speedIn, boolean scaredByPlayerMovementIn, Ingredient temptItemIn, Ingredient temptItemNice) {
		super(temptedEntityIn, speedIn, scaredByPlayerMovementIn, temptItemIn);
		this.temptItemNice = temptItemNice;
	}

	@Override
	public boolean shouldFollowItem(@Nonnull ItemStack stack) {
		LocalDate date = LocalDate.now();
		return FrogsModule.enableBigFunny && DayOfWeek.from(date) == DayOfWeek.WEDNESDAY ?
				temptItemNice.test(stack) : super.shouldFollowItem(stack);
	}
}
