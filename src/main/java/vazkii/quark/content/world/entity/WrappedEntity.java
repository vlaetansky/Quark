package vazkii.quark.content.world.entity;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WrappedEntity extends Zombie {

	public static final ResourceLocation WRAPPED_LOOT_TABLE = new ResourceLocation("quark", "entities/wrapped");
	
	public WrappedEntity(EntityType<? extends WrappedEntity> type, Level worldIn) {
		super(type, worldIn);
	}

	@Override
	public boolean doHurtTarget(Entity entityIn) {
		boolean flag = super.doHurtTarget(entityIn);
		if (flag && this.getMainHandItem().isEmpty() && entityIn instanceof LivingEntity) {
			float f = this.level.getCurrentDifficultyAt(new BlockPos(getX(), getY(), getY())).getEffectiveDifficulty();
			((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 140 * (int)f));
		}

		return flag;
	}
	
	@Nonnull
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return WRAPPED_LOOT_TABLE;
	}

}
