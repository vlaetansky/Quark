package vazkii.quark.content.automation.entity;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.content.automation.module.GravisandModule;

public class GravisandEntity extends FallingBlockEntity {

	private static final EntityDataAccessor<Float> DIRECTION = SynchedEntityData.defineId(GravisandEntity.class, EntityDataSerializers.FLOAT);

	private static final String TAG_DIRECTION = "fallDirection";

	private final BlockState fallTile = GravisandModule.gravisand.defaultBlockState();

	public GravisandEntity(EntityType<? extends GravisandEntity> type, Level world) {
		super(type, world);
	}

	public GravisandEntity(Level world, double x, double y, double z, float direction) {
		this(GravisandModule.gravisandType, world);
		this.blocksBuilding = true;
		this.setPos(x, y + (double)((1.0F - this.getBbHeight()) / 2.0F), z);
		this.setDeltaMovement(Vec3.ZERO);
		this.xo = x;
		this.yo = y;
		this.zo = z;
		this.setStartPos(new BlockPos(position()));
		entityData.set(DIRECTION, direction);
	}


	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(DIRECTION, 0F);
	}

	// Mostly vanilla copy but supporting directional falling
	@Override
	public void tick() {
		Vec3 pos = position();
		if (this.fallTile.isAir() || pos.y > 400 || pos.y < -200) {
			this.discard();
		} else {
			this.xo = pos.x;
			this.yo = pos.y;
			this.zo = pos.z;
			Block block = this.fallTile.getBlock();
			if (this.time++ == 0) {
				BlockPos blockpos = new BlockPos(position());
				if (this.level.getBlockState(blockpos).getBlock() == block) {
					this.level.removeBlock(blockpos, false);
				} else if (!this.level.isClientSide) {
					this.discard();
					return;
				}
			}

			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.04D * getFallDirection(), 0.0D));
			}

			this.move(MoverType.SELF, this.getDeltaMovement());
			if (!this.level.isClientSide) {
				BlockPos fallTarget = new BlockPos(position());
				boolean flag = this.fallTile.getBlock() instanceof ConcretePowderBlock;
				boolean flag1 = flag && this.level.getFluidState(fallTarget).is(FluidTags.WATER);
				double d0 = this.getDeltaMovement().lengthSqr();
				if (flag && d0 > 1.0D) {
					BlockHitResult blockraytraceresult = this.level.clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this));
					if (blockraytraceresult.getType() != HitResult.Type.MISS && this.level.getFluidState(blockraytraceresult.getBlockPos()).is(FluidTags.WATER)) {
						fallTarget = blockraytraceresult.getBlockPos();
						flag1 = true;
					}
				}

				if (!verticalCollision && !flag1) {
					if (!this.level.isClientSide && (this.time > 100 && (fallTarget.getY() < 1 || fallTarget.getY() > 256) || this.time > 600)) {
						if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							this.spawnAtLocation(block);
						}

						this.discard();
					}
				} else {
					BlockState blockstate = this.level.getBlockState(fallTarget);
					this.setDeltaMovement(this.getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
					if (blockstate.getBlock() != Blocks.MOVING_PISTON) {
						this.discard();
						Direction facing = getFallDirection() < 0 ? Direction.DOWN : Direction.UP;
						boolean flag2 = blockstate.canBeReplaced(new DirectionalPlaceContext(this.level, fallTarget, facing, ItemStack.EMPTY, facing.getOpposite()));
						boolean flag3 = this.fallTile.canSurvive(this.level, fallTarget);
						if (flag2 && flag3) {
							this.level.setBlock(fallTarget, this.fallTile, 3);
						} else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
							this.spawnAtLocation(block);
						}
					} else if (this.dropItem && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
						this.spawnAtLocation(block);
					}
				}
			}
		}

		this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
	}

	@Override
	public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
		return false;
	}

	private float getFallDirection() {
		return entityData.get(DIRECTION);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);

		compound.putFloat(TAG_DIRECTION, getFallDirection());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);

		entityData.set(DIRECTION, compound.getFloat(TAG_DIRECTION));
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Nonnull
	@Override
	public BlockState getBlockState() {
		return fallTile;
	}

}
