package vazkii.quark.content.management.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import vazkii.quark.content.management.module.ChestsInBoatsModule;

import javax.annotation.Nonnull;

public class ChestPassenger extends Entity implements Container, MenuProvider {

	private final NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

	private static final EntityDataAccessor<ItemStack> CHEST_TYPE = SynchedEntityData.defineId(ChestPassenger.class, EntityDataSerializers.ITEM_STACK);
	private static final String TAG_CHEST_TYPE = "chestType";

	public ChestPassenger(EntityType<? extends ChestPassenger> type, Level worldIn) {
		super(type, worldIn);
		noPhysics = true;
	}

	public ChestPassenger(Level worldIn, ItemStack stack) {
		this(ChestsInBoatsModule.chestPassengerEntityType, worldIn);

		ItemStack newStack = stack.copy();
		newStack.setCount(1);
		entityData.set(CHEST_TYPE, newStack);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(CHEST_TYPE, new ItemStack(Blocks.CHEST));
	}

	@Override
	public void tick() {
		super.tick();

		if(!isAlive())
			return;

		if(!isPassenger() && !level.isClientSide)
			discard();

		Entity riding = getVehicle();
		if (riding != null) {
			setYRot(riding.yRotO);
		}
	}

	@Override
	public boolean canTrample(@Nonnull BlockState state, @Nonnull BlockPos pos, float fallDistance) {
		return false;
	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack itemstack : items)
			if(!itemstack.isEmpty())
				return false;

		return true;
	}

	@Nonnull
	@Override
	public ItemStack getItem(int index) {
		return items.get(index);
	}

	@Nonnull
	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(items, index, count);
	}

	@Nonnull
	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack itemstack = items.get(index);

		if(itemstack.isEmpty())
			return ItemStack.EMPTY;
		else {
			items.set(index, ItemStack.EMPTY);
			return itemstack;
		}
	}

	@Override
	public void setItem(int index, @Nonnull ItemStack stack) {
		items.set(index, stack);
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public void setChanged() {
		// NO-OP
	}

	@Override
	public boolean stillValid(@Nonnull Player player) {
		return isAlive() && player.distanceToSqr(this) <= 64;
	}

	@Override
	public void startOpen(@Nonnull Player player) {
		// NO-OP
	}

	@Override
	public void stopOpen(@Nonnull Player player) {
		// NO-OP
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container.chest");
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
		return ChestMenu.threeRows(id, inventory, this);
	}

	@Override
	public boolean canPlaceItem(int index, @Nonnull ItemStack stack) {
		return true;
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
		ContainerHelper.loadAllItems(compound, items);

		CompoundTag itemCmp = compound.getCompound(TAG_CHEST_TYPE);
		ItemStack stack = ItemStack.of(itemCmp);
		if(!stack.isEmpty())
			entityData.set(CHEST_TYPE, stack);

	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
		ContainerHelper.saveAllItems(compound, items);

		CompoundTag itemCmp = new CompoundTag();
		entityData.get(CHEST_TYPE).save(itemCmp);
		compound.put(TAG_CHEST_TYPE, itemCmp);

	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void remove(@Nonnull RemovalReason reason) {
		if(!level.isClientSide) {
			Containers.dropContents(level, this, this);
			spawnAtLocation(getChestType());
		}

		super.remove(reason);
	}

	public ItemStack getChestType() {
		return entityData.get(CHEST_TYPE);
	}

}
