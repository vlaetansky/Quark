package vazkii.quark.content.experimental.shiba.entity;

import java.util.UUID;

import javax.annotation.Nonnull;

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
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.content.experimental.module.ShibaModule;

public class ShibaEntity extends TameableEntity {

	private static final DataParameter<Integer> COLLAR_COLOR = EntityDataManager.createKey(ShibaEntity.class, DataSerializers.VARINT);

	public ShibaEntity(EntityType<? extends ShibaEntity> type, World worldIn) {
		super(type, worldIn);
		setTamed(false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new SwimGoal(this));
		goalSelector.addGoal(2, new SitGoal(this));
		goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		goalSelector.addGoal(4, new TemptGoal(this, 1, Ingredient.fromItems(Items.BONE), false));
		goalSelector.addGoal(5, new BreedGoal(this, 1.0D));
		goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.addGoal(8, new LookRandomlyGoal(this));
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		Item item = stack.getItem();
		return item.isFood() && item.getFood().isMeat();
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
	
	@Override
	protected void registerData() {
		super.registerData();
		dataManager.register(COLLAR_COLOR, DyeColor.RED.getId());
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.dataManager.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor collarcolor) {
		this.dataManager.set(COLLAR_COLOR, collarcolor.getId());
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 8;
	}

	@Override
	public boolean canMateWith(AnimalEntity otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTamed()) {
			return false;
		} else if (!(otherAnimal instanceof ShibaEntity)) {
			return false;
		} else {
			ShibaEntity wolfentity = (ShibaEntity) otherAnimal;
			if (!wolfentity.isTamed()) {
				return false;
			} else if (wolfentity.isSleeping()) {
				return false;
			} else {
				return this.isInLove() && wolfentity.isInLove();
			}
		}
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putByte("CollarColor", (byte)this.getCollarColor().getId());
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		if (compound.contains("CollarColor", 99)) {
			this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));
		}
	}

	@Override
	public ActionResultType func_230254_b_(PlayerEntity p_230254_1_, Hand p_230254_2_) {
		ItemStack itemstack = p_230254_1_.getHeldItem(p_230254_2_);
		Item item = itemstack.getItem();
		if (this.world.isRemote) {
			boolean flag = this.isOwner(p_230254_1_) || this.isTamed() || item == Items.BONE && !this.isTamed();
			return flag ? ActionResultType.CONSUME : ActionResultType.PASS;
		} else {
			if (this.isTamed()) {
				if (this.isBreedingItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
					if (!p_230254_1_.abilities.isCreativeMode) {
						itemstack.shrink(1);
					}

					this.heal((float)item.getFood().getHealing());
					return ActionResultType.SUCCESS;
				}

				if (!(item instanceof DyeItem)) {
					ActionResultType actionresulttype = super.func_230254_b_(p_230254_1_, p_230254_2_);
					if ((!actionresulttype.isSuccessOrConsume() || this.isChild()) && this.isOwner(p_230254_1_)) {
						this.func_233687_w_(!this.isSitting());
						this.isJumping = false;
						this.navigator.clearPath();
						this.setAttackTarget((LivingEntity)null);
						return ActionResultType.SUCCESS;
					}

					return actionresulttype;
				}

				DyeColor dyecolor = ((DyeItem)item).getDyeColor();
				if (dyecolor != this.getCollarColor()) {
					this.setCollarColor(dyecolor);
					if (!p_230254_1_.abilities.isCreativeMode) {
						itemstack.shrink(1);
					}

					return ActionResultType.SUCCESS;
				}
			} else if (item == Items.BONE) {
				if (!p_230254_1_.abilities.isCreativeMode) {
					itemstack.shrink(1);
				}

				if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_230254_1_)) {
					this.setTamedBy(p_230254_1_);
					this.navigator.clearPath();
					this.setAttackTarget((LivingEntity)null);
					this.func_233687_w_(true);
					this.world.setEntityState(this, (byte)7);
				} else {
					this.world.setEntityState(this, (byte)6);
				}

				return ActionResultType.SUCCESS;
			}

			return super.func_230254_b_(p_230254_1_, p_230254_2_);
		}
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

	@Override // make baby
	public AgeableEntity func_241840_a(ServerWorld world, AgeableEntity mate) {
		ShibaEntity wolfentity = ShibaModule.shibaType.create(world);
		UUID uuid = this.getOwnerId();
		if (uuid != null) {
			wolfentity.setOwnerId(uuid);
			wolfentity.setTamed(true);
		}

		return wolfentity;
	}

}
