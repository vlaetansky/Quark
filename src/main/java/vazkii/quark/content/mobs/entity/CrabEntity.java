/**
 * This class was created by <WireSegal>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 * <p>
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 * <p>
 * File Created @ [Jul 13, 2019, 19:51 AM (EST)]
 */
package vazkii.quark.content.mobs.entity;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.base.handler.QuarkSounds;
import vazkii.quark.content.mobs.ai.RaveGoal;
import vazkii.quark.content.mobs.module.CrabsModule;

public class CrabEntity extends Animal implements IEntityAdditionalSpawnData {

	public static final ResourceLocation CRAB_LOOT_TABLE = new ResourceLocation("quark", "entities/crab");

	private static final EntityDataAccessor<Float> SIZE_MODIFIER = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(CrabEntity.class, EntityDataSerializers.INT);

	private static int lightningCooldown;
	private Ingredient temptationItems;

	private boolean noSpike;
	private boolean crabRave;
	private BlockPos jukeboxPosition;

	public CrabEntity(EntityType<? extends CrabEntity> type, Level worldIn) {
		this(type, worldIn, 1);
	}

	public CrabEntity(EntityType<? extends CrabEntity> type, Level worldIn, float sizeModifier) {
		super(type, worldIn);
		this.setPathfindingMalus(BlockPathTypes.LAVA, -1.0F);
		if (sizeModifier != 1)
			entityData.set(SIZE_MODIFIER, sizeModifier);
	}

	public static boolean spawnPredicate(EntityType<? extends Animal> type, LevelAccessor world, MobSpawnType reason, BlockPos pos, Random random) {
		return world.getBlockState(pos.below()).getBlock().is(CrabsModule.crabSpawnableTag) && world.getMaxLocalRawBrightness(pos) > 8;
	}

	public static void rave(LevelAccessor world, BlockPos pos, boolean raving) {
		for(CrabEntity crab : world.getEntitiesOfClass(CrabEntity.class, (new AABB(pos)).inflate(3.0D)))
			crab.party(pos, raving);
	}

	@Override
	public float getWalkTargetValue(BlockPos pos, LevelReader world) {
		return world.getBlockState(pos.below()).getBlock().is(CrabsModule.crabSpawnableTag) ? 10.0F : world.getBrightness(pos) - 0.5F;
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Nonnull
	@Override
	public MobType getMobType() {
		return MobType.ARTHROPOD;
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(SIZE_MODIFIER, 1f);
		entityData.define(VARIANT, -1);
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return QuarkSounds.ENTITY_CRAB_IDLE;
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return QuarkSounds.ENTITY_CRAB_DIE;
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return QuarkSounds.ENTITY_CRAB_HURT;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions size) {
		return 0.2f * size.height;
	}

	public float getSizeModifier() {
		return entityData.get(SIZE_MODIFIER);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
		this.goalSelector.addGoal(2, new RaveGoal(this));
		this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(4, new TemptGoal(this, 1.2D, false, getTemptationItems()));
		this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
		this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder prepareAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ARMOR, 3.0D)
				.add(Attributes.ARMOR_TOUGHNESS, 2.0D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.5D);
	}

//	@Override
//	public boolean isEntityInsideOpaqueBlock() {
//		return MiscUtil.isEntityInsideOpaqueBlock(this);
//	}

	@Override
	public void tick() {
		super.tick();

		if(!level.isClientSide && entityData.get(VARIANT) == -1) {
			int variant = 0;
			if(random.nextBoolean()) {
				variant += random.nextInt(2) + 1;
			}

			entityData.set(VARIANT, variant);
		}

		if (wasTouchingWater)
			maxUpStep = 1F;
		else
			maxUpStep = 0.6F;

		if (lightningCooldown > 0) {
			lightningCooldown--;
			clearFire();
		}

		Vec3 pos = position();
		if(isRaving() && (jukeboxPosition == null || jukeboxPosition.distSqr(pos.x, pos.y, pos.z, true) > 24.0D || level.getBlockState(jukeboxPosition).getBlock() != Blocks.JUKEBOX))
			party(null, false);

		if(isRaving() && level.isClientSide && tickCount % 10 == 0) {
			BlockPos below = blockPosition().below();
			BlockState belowState = level.getBlockState(below);
			if(belowState.getMaterial() == Material.SAND)
				level.levelEvent(2001, below, Block.getId(belowState));
		}
	}

	@Nonnull
	@Override
	public EntityDimensions getDimensions(Pose poseIn) {
		return super.getDimensions(poseIn).scale(this.getSizeModifier());
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
	public boolean isInvulnerableTo(@Nonnull DamageSource source) {
		return super.isInvulnerableTo(source) ||
				source == DamageSource.LIGHTNING_BOLT ||
				getSizeModifier() > 1 && source.isFire();
	}
	
	@Override
	public void thunderHit(ServerLevel sworld, LightningBolt lightningBolt) { // onStruckByLightning
		if (lightningCooldown > 0 || level.isClientSide)
			return;

		float sizeMod = getSizeModifier();
		if (sizeMod <= 15) {

			this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Lightning Bonus", 0.5, Operation.ADDITION));
			this.getAttribute(Attributes.MOVEMENT_SPEED).addPermanentModifier(new AttributeModifier("Lightning Debuff", -0.05, Operation.ADDITION));
			this.getAttribute(Attributes.ARMOR).addPermanentModifier(new AttributeModifier("Lightning Bonus", 0.125, Operation.ADDITION));

			float sizeModifier = Math.min(sizeMod + 1, 16);
			this.entityData.set(SIZE_MODIFIER, sizeModifier);
			refreshDimensions();

			lightningCooldown = 150;
		}
	}

	@Override
	public void push(@Nonnull Entity entityIn) {
		if (getSizeModifier() <= 1)
			super.push(entityIn);
	}

	@Override
	protected void doPush(Entity entityIn) {
		super.doPush(entityIn);
		if (level.getDifficulty() != Difficulty.PEACEFUL && !noSpike) {
			if (entityIn instanceof LivingEntity && !(entityIn instanceof CrabEntity))
				entityIn.hurt(DamageSource.CACTUS, 1f);
		}
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return !stack.isEmpty() && getTemptationItems().test(stack);
	}

	private Ingredient getTemptationItems() {
		if(temptationItems == null)
			temptationItems =  Ingredient.merge(Lists.newArrayList(
					Ingredient.of(Items.WHEAT, Items.CHICKEN),
					Ingredient.of(ItemTags.FISHES)
					));
		
		return temptationItems;
	}

	@Nullable
	@Override // createChild
	public AgeableMob getBreedOffspring(ServerLevel sworld, @Nonnull AgeableMob other) {
		return new CrabEntity(CrabsModule.crabType, level);
	}
	
	@Nonnull
	@Override
	protected ResourceLocation getDefaultLootTable() {
		return CRAB_LOOT_TABLE;
	}

	public int getVariant() {
		return Math.max(0, entityData.get(VARIANT));
	}

	public void party(BlockPos pos, boolean isPartying) {
		// A separate method, due to setPartying being side-only.
		jukeboxPosition = pos;
		crabRave = isPartying;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setRecordPlayingNearby(BlockPos pos, boolean isPartying) {
		party(pos, isPartying);
	}

	public boolean isRaving() {
		return crabRave;
	}

	@Override
	public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> parameter) {
		if (parameter.equals(SIZE_MODIFIER))
			refreshDimensions();

		super.onSyncedDataUpdated(parameter);
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeFloat(getSizeModifier());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		entityData.set(SIZE_MODIFIER, buffer.readFloat());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		lightningCooldown = compound.getInt("LightningCooldown");
		noSpike = compound.getBoolean("NoSpike");

		if (compound.contains("EnemyCrabRating")) {
			float sizeModifier = compound.getFloat("EnemyCrabRating");
			entityData.set(SIZE_MODIFIER, sizeModifier);
		}

		if(compound.contains("Variant"))
			entityData.set(VARIANT, compound.getInt("Variant"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putFloat("EnemyCrabRating", getSizeModifier());
		compound.putInt("LightningCooldown", lightningCooldown);
		compound.putInt("Variant", entityData.get(VARIANT));
		compound.putBoolean("NoSpike", noSpike);
	}


}
