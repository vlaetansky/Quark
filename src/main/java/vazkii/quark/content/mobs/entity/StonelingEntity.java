package vazkii.quark.content.mobs.entity;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeWorldServer;
import net.minecraftforge.fml.network.NetworkHooks;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.base.util.IfFlagGoal;
import vazkii.quark.content.mobs.ai.ActWaryGoal;
import vazkii.quark.content.mobs.ai.FavorBlockGoal;
import vazkii.quark.content.mobs.ai.RunAndPoofGoal;
import vazkii.quark.content.mobs.module.StonelingsModule;

public class StonelingEntity extends PathfinderMob {

	public static final ResourceLocation CARRY_LOOT_TABLE = new ResourceLocation("quark", "entities/stoneling_carry");

	private static final EntityDataAccessor<ItemStack> CARRYING_ITEM = SynchedEntityData.defineId(StonelingEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Byte> VARIANT = SynchedEntityData.defineId(StonelingEntity.class, EntityDataSerializers.BYTE);
	private static final EntityDataAccessor<Float> HOLD_ANGLE = SynchedEntityData.defineId(StonelingEntity.class, EntityDataSerializers.FLOAT);

	private static final String TAG_CARRYING_ITEM = "carryingItem";
	private static final String TAG_VARIANT = "variant";
	private static final String TAG_HOLD_ANGLE = "itemAngle";
	private static final String TAG_PLAYER_MADE = "playerMade";

	private ActWaryGoal waryGoal;

	private boolean isTame;

	public StonelingEntity(EntityType<? extends StonelingEntity> type, Level worldIn) {
		super(type, worldIn);
		this.setPathfindingMalus(BlockPathTypes.DAMAGE_CACTUS, 1.0F);
		this.setPathfindingMalus(BlockPathTypes.DANGER_CACTUS, 1.0F);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(CARRYING_ITEM, ItemStack.EMPTY);
		entityData.define(VARIANT, (byte) 0);
		entityData.define(HOLD_ANGLE, 0F);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.2, 0.98F));
		goalSelector.addGoal(4, new FavorBlockGoal(this, 0.2, s -> s.getBlock().is(Tags.Blocks.ORES_DIAMOND)));
		goalSelector.addGoal(3, new IfFlagGoal(new TemptGoal(this, 0.6, Ingredient.of(Tags.Items.GEMS_DIAMOND), false), () -> StonelingsModule.enableDiamondHeart && !StonelingsModule.tamableStonelings));
		goalSelector.addGoal(2, new RunAndPoofGoal<>(this, Player.class, 4, 0.5, 0.5));
		goalSelector.addGoal(1, waryGoal = new ActWaryGoal(this, 0.1, 6, () -> StonelingsModule.cautiousStonelings));
		goalSelector.addGoal(0, new IfFlagGoal(new TemptGoal(this, 0.6, Ingredient.of(Tags.Items.GEMS_DIAMOND), false), () -> StonelingsModule.tamableStonelings));

	}

	public static AttributeSupplier.Builder prepareAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1D);
    }

	@Override
	public void tick() {
		super.tick();

		if (wasTouchingWater)
			maxUpStep = 1F;
		else
			maxUpStep = 0.6F;

		if (!level.isClientSide && level.getDifficulty() == Difficulty.PEACEFUL && !isTame) {
			remove();
			for (Entity passenger : getIndirectPassengers())
				if (!(passenger instanceof Player))
					passenger.remove();
		}

		this.yBodyRotO = this.yRotO;
		this.yBodyRot = this.yRot;
	}

	@Override
	public MobCategory getClassification(boolean forSpawnCount) {
		if (isTame)
			return MobCategory.CREATURE;
		return MobCategory.MONSTER;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return !isTame;
	}
	
	@Override
	public void checkDespawn() {
		boolean wasAlive = isAlive();
		super.checkDespawn();
		if (!isAlive() && wasAlive)
			for (Entity passenger : getIndirectPassengers())
				if (!(passenger instanceof Player))
					passenger.remove();
	}

	@Override // processInteract
	public InteractionResult mobInteract(Player player, @Nonnull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if(!stack.isEmpty() && stack.getItem() == Items.NAME_TAG)
			return stack.getItem().interactLivingEntity(stack, player, this, hand);
		else
			return super.mobInteract(player, hand);
	}

	@Nonnull
	@Override
	public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
		if(hand == InteractionHand.MAIN_HAND && isAlive()) {
			ItemStack playerItem = player.getItemInHand(hand);
			Vec3 pos = position();

			if(!level.isClientSide) {
				if (isPlayerMade()) {
					if (!player.isDiscrete() && !playerItem.isEmpty()) {

						EnumStonelingVariant currentVariant = getVariant();
						EnumStonelingVariant targetVariant = null;
						Block targetBlock = null;
						mainLoop: for (EnumStonelingVariant variant : EnumStonelingVariant.values()) {
							for (Block block : variant.getBlocks()) {
								if (block.asItem() == playerItem.getItem()) {
									targetVariant = variant;
									targetBlock = block;
									break mainLoop;
								}
							}
						}

						if (targetVariant != null) {
							if (level instanceof ServerLevel) {
								((ServerLevel) level).sendParticles(ParticleTypes.HEART, pos.x, pos.y + getBbHeight(), pos.z, 1, 0.1, 0.1, 0.1, 0.1);
								if (targetVariant != currentVariant)
									((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, targetBlock.defaultBlockState()), pos.x, pos.y + getBbHeight() / 2, pos.z, 16, 0.1, 0.1, 0.1, 0.25);
							}

							if (targetVariant != currentVariant) {
								playSound(QuarkSounds.ENTITY_STONELING_EAT, 1F, 1F);
								entityData.set(VARIANT, targetVariant.getIndex());
							}

							playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + level.random.nextFloat() * 1F);

							heal(1);

							if (!player.abilities.instabuild)
								playerItem.shrink(1);

							return InteractionResult.SUCCESS;
						}

						return InteractionResult.PASS;
					}

					ItemStack stonelingItem = entityData.get(CARRYING_ITEM);

					if (!stonelingItem.isEmpty() || !playerItem.isEmpty()) {
						player.setItemInHand(hand, stonelingItem.copy());
						entityData.set(CARRYING_ITEM, playerItem.copy());

						if (playerItem.isEmpty())
							playSound(QuarkSounds.ENTITY_STONELING_GIVE, 1F, 1F);
						else playSound(QuarkSounds.ENTITY_STONELING_TAKE, 1F, 1F);
					}
				} else if (StonelingsModule.tamableStonelings && playerItem.getItem().is(Tags.Items.GEMS_DIAMOND)) {
					heal(8);

					setPlayerMade(true);

					playSound(QuarkSounds.ENTITY_STONELING_PURR, 1F, 1F + level.random.nextFloat() * 1F);

					if (!player.abilities.instabuild)
						playerItem.shrink(1);

					if (level instanceof ServerLevel)
						((ServerLevel) level).sendParticles(ParticleTypes.HEART, pos.x, pos.y + getBbHeight(), pos.z, 4, 0.1, 0.1, 0.1, 0.1);

					return InteractionResult.SUCCESS;
				}
			}
		}

		return InteractionResult.PASS;
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData data, @Nullable CompoundTag compound) {
		byte variant;
		if (data instanceof EnumStonelingVariant)
			variant = ((EnumStonelingVariant) data).getIndex();
		else
			variant = (byte) world.getRandom().nextInt(EnumStonelingVariant.values().length);

		entityData.set(VARIANT, variant);
		entityData.set(HOLD_ANGLE, world.getRandom().nextFloat() * 90 - 45);

		if(!isTame && !world.isClientSide() && world instanceof IForgeWorldServer) {
			List<ItemStack> items = ((IForgeWorldServer) world).getWorldServer().getServer().getLootTables()
					.get(CARRY_LOOT_TABLE).getRandomItems(new LootContext.Builder((ServerLevel) world).create(LootContextParamSets.EMPTY));
			if (!items.isEmpty())
				entityData.set(CARRYING_ITEM, items.get(0));
		}

		return super.finalizeSpawn(world, difficulty, spawnReason, data, compound);
	}


	@Override
	public boolean isInvulnerableTo(@Nonnull DamageSource source) {
		return source == DamageSource.CACTUS || source.isProjectile() || super.isInvulnerableTo(source);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}


	@Override
	public boolean checkSpawnObstruction(LevelReader worldReader) {
		return worldReader.isUnobstructed(this, Shapes.create(getBoundingBox()));
	}

	@Override
	public double getPassengersRidingOffset() {
		return this.getBbHeight();
	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected int decreaseAirSupply(int air) {
		return air;
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier) {
		return false;
	}

	@Override
	protected void actuallyHurt(@Nonnull DamageSource damageSrc, float damageAmount) {
		super.actuallyHurt(damageSrc, damageAmount);

		if(!isPlayerMade() && damageSrc.getEntity() instanceof Player) {
			startle();
			for (Entity entity : level.getEntities(this,
					getBoundingBox().inflate(16))) {
				if (entity instanceof StonelingEntity) {
					StonelingEntity stoneling = (StonelingEntity) entity;
					if (!stoneling.isPlayerMade() && stoneling.getSensing().canSee(this)) {
						startle();
					}
				}
			}
		}
	}

	public boolean isStartled() {
		return waryGoal.isStartled();
	}

	public void startle() {
		waryGoal.startle();
		Set<WrappedGoal> entries = Sets.newHashSet(goalSelector.availableGoals);

		for (WrappedGoal task : entries)
			if (task.getGoal() instanceof TemptGoal)
				goalSelector.removeGoal(task.getGoal());
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource damage, int looting, boolean wasRecentlyHit) {
		super.dropCustomDeathLoot(damage, looting, wasRecentlyHit);

		ItemStack stack = getCarryingItem();
		if(!stack.isEmpty())
			spawnAtLocation(stack, 0F);
	}

	public void setPlayerMade(boolean value) {
		isTame = value;
	}

	public ItemStack getCarryingItem() {
		return entityData.get(CARRYING_ITEM);
	}

	public EnumStonelingVariant getVariant() {
		return EnumStonelingVariant.byIndex(entityData.get(VARIANT));
	}

	public float getItemAngle() {
		return entityData.get(HOLD_ANGLE);
	}

	public boolean isPlayerMade() {
		return isTame;
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		if(compound.contains(TAG_CARRYING_ITEM, 10)) {
			CompoundTag itemCmp = compound.getCompound(TAG_CARRYING_ITEM);
			ItemStack stack = ItemStack.of(itemCmp);
			entityData.set(CARRYING_ITEM, stack);
		}

		entityData.set(VARIANT, compound.getByte(TAG_VARIANT));
		entityData.set(HOLD_ANGLE, compound.getFloat(TAG_HOLD_ANGLE));
		setPlayerMade(compound.getBoolean(TAG_PLAYER_MADE));
	}

	@Override
	public boolean canSee(Entity entityIn) {
		Vec3 pos = position();
		Vec3 epos = entityIn.position();
		
		Vec3 origin = new Vec3(pos.x, pos.y + getEyeHeight(), pos.z);
		float otherEyes = entityIn.getEyeHeight();
		for (float height = 0; height <= otherEyes; height += otherEyes / 8) {
			if (this.level.clip(new ClipContext(origin, epos.add(0, height, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS)
				return true;
		}

		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.put(TAG_CARRYING_ITEM, getCarryingItem().serializeNBT());

		compound.putByte(TAG_VARIANT, getVariant().getIndex());
		compound.putFloat(TAG_HOLD_ANGLE, getItemAngle());
		compound.putBoolean(TAG_PLAYER_MADE, isPlayerMade());
	}

	public static boolean spawnPredicate(EntityType<? extends StonelingEntity> type, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, Random rand) {
		return pos.getY() <= StonelingsModule.maxYLevel && MiscUtil.validSpawnLight(world, pos, rand) && MiscUtil.validSpawnLocation(type, world, reason, pos);
	}

	@Override
	public boolean checkSpawnRules(@Nonnull LevelAccessor world, MobSpawnType reason) {
		BlockState state = world.getBlockState(new BlockPos(position()).below());
		if (state.getMaterial() != Material.STONE)
			return false;
		
		return StonelingsModule.dimensions.canSpawnHere(world) && super.checkSpawnRules(world, reason);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return QuarkSounds.ENTITY_STONELING_CRY;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_STONELING_DIE;
	}

	@Override
	public int getAmbientSoundInterval() {
		return 1200;
	}

	@Override
	public void playAmbientSound() {
		SoundEvent sound = this.getAmbientSound();

		if (sound != null) this.playSound(sound, this.getSoundVolume(), 1f);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		if (hasCustomName()) {
			String customName = getName().getString();
			if (customName.equalsIgnoreCase("michael stevens") || customName.equalsIgnoreCase("vsauce"))
				return QuarkSounds.ENTITY_STONELING_MICHAEL;
		}

		return null;
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader world) {
		return 0.5F - world.getBrightness(pos);
	}
}
