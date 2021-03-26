package vazkii.quark.content.experimental.shiba.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ForgeEventFactory;

public class ShibaEntity extends TameableEntity {

	public ShibaEntity(EntityType<? extends ShibaEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new SitGoal(this));
		goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
		goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(7, new LookRandomlyGoal(this));
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		Item item = itemstack.getItem();
		boolean sneak = player.isSneaking();
		
		if(isTamed()) {
			if(sneak) {
                if(player.world instanceof ServerWorld) {
                	Vector3d pos = getPositionVec();
                    ((ServerWorld) player.world).spawnParticle(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
                    playSound(SoundEvents.ENTITY_WOLF_WHINE, 1F, 0.5F + (float) Math.random() * 0.5F);
                } else player.swingArm(Hand.MAIN_HAND);
                
                return ActionResultType.SUCCESS;
			} else {
	            ActionResultType actionresulttype = super.func_230254_b_(player, hand);
				
	            if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(player)) {
	               this.func_233687_w_(!this.isSitting());
	               this.isJumping = false;
	               this.navigator.clearPath();
	               this.setAttackTarget((LivingEntity)null);
	               return ActionResultType.SUCCESS;
	            }
	            
	            return actionresulttype;
			}
		} else if(item == Items.BONE) {
			if (!player.abilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			if (this.rand.nextInt(3) == 0 && !ForgeEventFactory.onAnimalTame(this, player)) {
				this.setTamedBy(player);
				this.navigator.clearPath();
				this.setAttackTarget((LivingEntity)null);
				this.func_233687_w_(true);
				this.world.setEntityState(this, (byte)7);
			} else {
				this.world.setEntityState(this, (byte)6);
			}

			return ActionResultType.SUCCESS;
		}
		
		return super.func_230254_b_(player, hand);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(rand.nextInt(3) == 0)
			return getHealth() < 10.0F ? SoundEvents.ENTITY_WOLF_WHINE : SoundEvents.ENTITY_WOLF_PANT;
		else
			return SoundEvents.ENTITY_WOLF_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ENTITY_WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override // make baby TODO
	public AgeableEntity func_241840_a(ServerWorld arg0, AgeableEntity arg1) {
		return null;
	}

}
