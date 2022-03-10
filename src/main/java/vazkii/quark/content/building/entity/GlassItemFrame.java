package vazkii.quark.content.building.entity;

import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.base.util.MovableFakePlayer;
import vazkii.quark.content.building.module.GlassItemFrameModule;

public class GlassItemFrame extends ItemFrame implements IEntityAdditionalSpawnData {

	public static final EntityDataAccessor<Boolean> IS_SHINY = SynchedEntityData.defineId(GlassItemFrame.class, EntityDataSerializers.BOOLEAN);

	private static final String TAG_SHINY = "isShiny";

	private boolean didHackery = false;
	private FakePlayer fakePlayer = null;

	public GlassItemFrame(EntityType<? extends GlassItemFrame> type, Level worldIn) {
		super(type, worldIn);
	}

	public GlassItemFrame(Level worldIn, BlockPos blockPos, Direction face) {
		super(GlassItemFrameModule.glassFrameEntity, worldIn);
		pos = blockPos;
		this.setDirection(face);
	}

	@Nonnull
	@Override
	public InteractionResult interact(Player player, @Nonnull InteractionHand hand) {
		ItemStack item = getItem();
		if(!player.isShiftKeyDown() && !item.isEmpty() && !(item.getItem() instanceof BannerItem)) {
			BlockPos behind = getBehindPos();
			BlockEntity tile = level.getBlockEntity(behind);

			if(tile != null && tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent()) {
				BlockState behindState = level.getBlockState(behind);
				InteractionResult result = behindState.use(level, player, hand, new BlockHitResult(new Vec3(getX(), getY(), getZ()), direction, behind, true));

				if(result.consumesAction())
					return result;
			}
		}

		return super.interact(player, hand);
	}

	@Override
	public void tick() {
		super.tick();

		if(GlassItemFrameModule.glassItemFramesUpdateMaps) {
			ItemStack stack = getItem();
			if(stack.getItem() instanceof MapItem && level instanceof ServerLevel sworld) {
				ItemStack clone = stack.copy();

				MapItemSavedData data = MapItem.getSavedData(clone, level);
				if(data != null && !data.locked) {
					if(fakePlayer == null)
						fakePlayer = new MovableFakePlayer(sworld, new GameProfile(UUID.randomUUID(), "ItemFrame"));

					MapItem item = (MapItem) stack.getItem();

					clone.setEntityRepresentation(null);
					fakePlayer.setPos(getX(), getY(), getZ());
					fakePlayer.getInventory().setItem(0, clone);

					item.update(level, fakePlayer, data);
				}
			}
		}
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();

		entityData.define(IS_SHINY, false);
	}

	@Override
	public boolean survives() {
		return super.survives() || isOnSign();
	}

	public BlockPos getBehindPos() {
		return pos.relative(direction.getOpposite());
	}

	public boolean isOnSign() {
		BlockState blockstate = level.getBlockState(getBehindPos());
		return blockstate.is(BlockTags.STANDING_SIGNS);
	}

	@Nullable
	@Override
	public ItemEntity spawnAtLocation(@Nonnull ItemStack stack, float offset) {
		if (stack.getItem() == Items.ITEM_FRAME && !didHackery) {
			stack = new ItemStack(getDroppedItem());
			didHackery = true;
		}

		return super.spawnAtLocation(stack, offset);
	}

	@Nonnull
	@Override
	public ItemStack getPickedResult(HitResult target) {
		ItemStack held = getItem();
		if (held.isEmpty())
			return new ItemStack(getDroppedItem());
		else
			return held.copy();
	}

	private Item getDroppedItem() {
		return entityData.get(IS_SHINY) ? GlassItemFrameModule.glowingGlassFrame : GlassItemFrameModule.glassFrame;
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);

		cmp.putBoolean(TAG_SHINY, entityData.get(IS_SHINY));
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);

		entityData.set(IS_SHINY, cmp.getBoolean(TAG_SHINY));
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.pos);
		buffer.writeVarInt(this.direction.get3DDataValue());
	}

	@Override
	public void readSpawnData(FriendlyByteBuf buffer) {
		this.pos = buffer.readBlockPos();
		this.setDirection(Direction.from3DDataValue(buffer.readVarInt()));
	}
}
