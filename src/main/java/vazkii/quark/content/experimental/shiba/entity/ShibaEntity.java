package vazkii.quark.content.experimental.shiba.entity;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.content.experimental.module.ShibaModule;
import vazkii.quark.content.experimental.shiba.ai.DeliverFetchedItemGoal;
import vazkii.quark.content.experimental.shiba.ai.FetchArrowGoal;
import vazkii.quark.content.tweaks.ai.NuzzleGoal;
import vazkii.quark.content.tweaks.ai.WantLoveGoal;

public class ShibaEntity extends TamableAnimal {

	private static final EntityDataAccessor<Integer> COLLAR_COLOR = SynchedEntityData.defineId(ShibaEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<ItemStack> MOUTH_ITEM = SynchedEntityData.defineId(ShibaEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Integer> FETCHING = SynchedEntityData.defineId(ShibaEntity.class, EntityDataSerializers.INT);

	public ShibaEntity(EntityType<? extends ShibaEntity> type, Level worldIn) {
		super(type, worldIn);
		setTame(false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new FloatGoal(this));
		goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
		goalSelector.addGoal(3, new FetchArrowGoal(this));
		goalSelector.addGoal(4, new DeliverFetchedItemGoal(this, 1.1D, -1F, 32.0F, false));
		goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
		goalSelector.addGoal(6, new TemptGoal(this, 1, Ingredient.of(Items.BONE), false));
		goalSelector.addGoal(7, new BreedGoal(this, 1.0D));
		goalSelector.addGoal(8, new NuzzleGoal(this, 0.5F, 16, 2, SoundEvents.WOLF_WHINE));
		goalSelector.addGoal(9, new WantLoveGoal(this, 0.2F));
		goalSelector.addGoal(10, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(12, new RandomLookAroundGoal(this));
	}

	@Override
	public void tick() {
		super.tick();

		AbstractArrow fetching = getFetching();
		if(fetching != null && (isSleeping() || fetching.level != level || !fetching.isAlive() || fetching.pickup == Pickup.DISALLOWED))
			setFetching(null);

		if(!isSleeping() && !level.isClientSide && fetching == null && getMouthItem().isEmpty()) {
			LivingEntity owner = getOwner();
			if(owner != null) {
				AABB check = owner.getBoundingBox().inflate(2);
				List<AbstractArrow> arrows = level.getEntitiesOfClass(AbstractArrow.class, check, 
						a -> a.getOwner() == owner && a.pickup != Pickup.DISALLOWED);

				if(arrows.size() > 0) {
					AbstractArrow arrow = arrows.get(level.random.nextInt(arrows.size()));
					setFetching(arrow);
				}
			}
		}
	}

	public AbstractArrow getFetching() {
		int id = entityData.get(FETCHING);
		if(id == -1)
			return null;

		Entity e = level.getEntity(id);
		if(e == null || !(e instanceof AbstractArrow))
			return null;

		return (AbstractArrow) e;
	}

	public void setFetching(AbstractArrow e) {
		entityData.set(FETCHING, e == null ? -1 : e.getId());
	}

	@Override
	public boolean isFood(ItemStack stack) {
		Item item = stack.getItem();
		return item.isEdible() && item.getFoodProperties().isMeat();
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(COLLAR_COLOR, DyeColor.RED.getId());
		entityData.define(MOUTH_ITEM, ItemStack.EMPTY);
		entityData.define(FETCHING, -1);
	}

	public DyeColor getCollarColor() {
		return DyeColor.byId(this.entityData.get(COLLAR_COLOR));
	}

	public void setCollarColor(DyeColor collarcolor) {
		this.entityData.set(COLLAR_COLOR, collarcolor.getId());
	}

	public ItemStack getMouthItem() {
		return entityData.get(MOUTH_ITEM);
	}

	public void setMouthItem(ItemStack stack) {
		this.entityData.set(MOUTH_ITEM, stack);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 8;
	}

	@Override
	public boolean canMate(Animal otherAnimal) {
		if (otherAnimal == this) {
			return false;
		} else if (!this.isTame()) {
			return false;
		} else if (!(otherAnimal instanceof ShibaEntity)) {
			return false;
		} else {
			ShibaEntity wolfentity = (ShibaEntity) otherAnimal;
			if (!wolfentity.isTame()) {
				return false;
			} else if (wolfentity.isSleeping()) {
				return false;
			} else {
				return this.isInLove() && wolfentity.isInLove();
			}
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putByte("CollarColor", (byte)this.getCollarColor().getId());

		CompoundTag itemcmp = new CompoundTag();
		ItemStack holding = getMouthItem();
		if(!holding.isEmpty())
			holding.save(itemcmp);
		compound.put("MouthItem", itemcmp);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("CollarColor", 99))
			this.setCollarColor(DyeColor.byId(compound.getInt("CollarColor")));

		if(compound.contains("MouthItem")) {
			CompoundTag itemcmp = compound.getCompound("MouthItem");
			setMouthItem(ItemStack.of(itemcmp));
		}
	}

	@Override
	public InteractionResult mobInteract(Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		Item item = itemstack.getItem();
		if(player.isDiscrete() && player.getMainHandItem().isEmpty()) {
			if(hand == InteractionHand.MAIN_HAND && WantLoveGoal.canPet(this)) {
				if(player.level instanceof ServerLevel) {
					Vec3 pos = position();
					((ServerLevel) player.level).sendParticles(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
					playSound(SoundEvents.WOLF_WHINE, 0.6F, 0.5F + (float) Math.random() * 0.5F);
				} else player.swing(InteractionHand.MAIN_HAND);

				WantLoveGoal.setPetTime(this);
			}

			return InteractionResult.SUCCESS;
		} else
			if (this.level.isClientSide) {
				boolean flag = this.isOwnedBy(player) || this.isTame() || item == Items.BONE && !this.isTame();
				return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
			} else {
				if (this.isTame()) {
					ItemStack mouthItem = getMouthItem();
					if(!mouthItem.isEmpty()) {
						ItemStack copy = mouthItem.copy();
						if(!player.addItem(copy))
							spawnAtLocation(copy);

						if(player.level instanceof ServerLevel) {
							Vec3 pos = position();
							((ServerLevel) player.level).sendParticles(ParticleTypes.HEART, pos.x, pos.y + 0.5, pos.z, 1, 0, 0, 0, 0.1);
							playSound(SoundEvents.WOLF_WHINE, 0.6F, 0.5F + (float) Math.random() * 0.5F);
						}
						setMouthItem(ItemStack.EMPTY);
						return InteractionResult.SUCCESS;
					}

					if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
						if (!player.abilities.instabuild) {
							itemstack.shrink(1);
						}

						this.heal((float)item.getFoodProperties().getNutrition());
						return InteractionResult.SUCCESS;
					}

					if (!(item instanceof DyeItem)) {
						if(!itemstack.isEmpty() && mouthItem.isEmpty() && itemstack.getItem() instanceof SwordItem) {
							ItemStack copy = itemstack.copy();
							copy.setCount(1);
							itemstack.setCount(itemstack.getCount() - 1);

							setMouthItem(copy);
							return InteractionResult.SUCCESS;
						}

						InteractionResult actionresulttype = super.mobInteract(player, hand);
						if ((!actionresulttype.consumesAction() || this.isBaby()) && this.isOwnedBy(player)) {
							this.setOrderedToSit(!this.isOrderedToSit());
							this.jumping = false;
							this.navigation.stop();
							this.setTarget((LivingEntity)null);
							return InteractionResult.SUCCESS;
						}

						return actionresulttype;
					}

					DyeColor dyecolor = ((DyeItem)item).getDyeColor();
					if (dyecolor != this.getCollarColor()) {
						this.setCollarColor(dyecolor);
						if (!player.abilities.instabuild) {
							itemstack.shrink(1);
						}

						return InteractionResult.SUCCESS;
					}
				} else if (item == Items.BONE) {
					if (!player.abilities.instabuild) {
						itemstack.shrink(1);
					}

					if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
						WantLoveGoal.setPetTime(this);

						this.tame(player);
						this.navigation.stop();
						this.setTarget((LivingEntity)null);
						this.setOrderedToSit(true);
						this.level.broadcastEntityEvent(this, (byte)7);
					} else {
						this.level.broadcastEntityEvent(this, (byte)6);
					}

					return InteractionResult.SUCCESS;
				}

				return super.mobInteract(player, hand);
			}
	}

	@Override
	public void setTame(boolean tamed) {
		super.setTame(tamed);
		if(tamed) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(20);
			setHealth(20);
		} getAttribute(Attributes.MAX_HEALTH).setBaseValue(8);

		getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		if(random.nextInt(3) == 0)
			return getHealth() < 10.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
		else
			return SoundEvents.WOLF_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.WOLF_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.WOLF_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override // make baby
	public AgeableMob getBreedOffspring(ServerLevel world, AgeableMob mate) {
		ShibaEntity wolfentity = ShibaModule.shibaType.create(world);
		UUID uuid = this.getOwnerUUID();
		if (uuid != null) {
			wolfentity.setOwnerUUID(uuid);
			wolfentity.setTame(true);
		}

		return wolfentity;
	}

}
