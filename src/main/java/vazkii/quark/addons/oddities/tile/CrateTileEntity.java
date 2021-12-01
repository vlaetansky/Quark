package vazkii.quark.addons.oddities.tile;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.Containers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import vazkii.quark.addons.oddities.block.CrateBlock;
import vazkii.quark.addons.oddities.container.CrateContainer;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.base.handler.SortingHandler;

public class CrateTileEntity extends BaseContainerBlockEntity implements WorldlyContainer, TickableBlockEntity {

	private int totalItems = 0;
	private int numPlayersUsing;
	private List<ItemStack> stacks = new ArrayList<>();

	private LazyOptional<SidedInvWrapper> wrapper = LazyOptional.of(() -> new SidedInvWrapper(this, Direction.UP));

	private int[] visibleSlots = new int[0];
	boolean needsUpdate = false;

	protected final ContainerData crateData = new ContainerData() {
		@Override
		public int get(int index) {
			return index == 0 ? totalItems : stacks.size();
		}

		@Override
		public void set(int index, int value) {
			// NO-OP
		}
		
		@Override
		public int getCount() {
			return 2;
		}
	};

	public CrateTileEntity() {
		super(CrateModule.crateType);
	}

	public void spillTheTea() {
		SortingHandler.mergeStacks(stacks);

		for(ItemStack stack : stacks)
			if(!stack.isEmpty())
				Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
	}

	@Override
	public void tick() {
		if(needsUpdate) {
			stacks.removeIf(ItemStack::isEmpty);
			needsUpdate = false;
		}
	}

	@Override
	public CompoundTag save(CompoundTag compound) {
		compound.putInt("totalItems", totalItems);

		ListTag list = new ListTag();
		for(ItemStack stack : stacks) {
			CompoundTag stackCmp = new CompoundTag();
			stack.save(stackCmp);
			list.add(stackCmp);
		}
		compound.put("stacks", list);

		return super.save(compound);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt) {
		totalItems = nbt.getInt("totalItems");

		ListTag list = nbt.getList("stacks", 10);
		stacks = new ArrayList<>(list.size());
		for(int i = 0; i < list.size(); i++)
			stacks.add(ItemStack.of(list.getCompound(i)));

		super.load(state, nbt);
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		if(slot < stacks.size()) {
			ItemStack stack = getItem(slot);
			totalItems -= stack.getCount();
			needsUpdate = true;

			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		ItemStack stackAt = getItem(slot);

		if(slot >= stacks.size()) {
			stacks.add(stack);
			totalItems += stack.getCount();
		} else {
			int sizeDiff = stack.getCount() - stackAt.getCount();
			totalItems += sizeDiff;
			stacks.set(slot, stack);
		}
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = getItem(slot);
		ItemStack retstack = stack.split(count);
		totalItems -= count;

		if(stack.isEmpty())
			needsUpdate = true;

		return retstack;
	}

	@Override
	public void setChanged() {
		super.setChanged();

		totalItems = 0;
		for(ItemStack stack : stacks)
			totalItems += stack.getCount();
	}

	@Override
	public int getContainerSize() {
		return Math.min(CrateModule.maxItems, stacks.size() + 1);
	}

	@Override
	public void clearContent() {
		stacks.clear();
		totalItems = 0;
	}

	@Override
	public boolean isEmpty() {
		return totalItems == 0;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction dir) {
		return (totalItems + stack.getCount()) <= CrateModule.maxItems;
	}

	@Override
	public int[] getSlotsForFace(Direction arg0) {
		if(visibleSlots.length != (stacks.size() + 1)) {
			visibleSlots = new int[stacks.size() + 1];
			for(int i = 0; i < visibleSlots.length; i++)
				visibleSlots[i] = i;
		}

		return visibleSlots;
	}

	@Override
	protected Component getDefaultName() {
		return new TranslatableComponent(CrateModule.crate.getDescriptionId());
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return new CrateContainer(id, player, this, crateData);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if(!remove && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return wrapper.cast();

		return super.getCapability(capability, facing);
	}

	// Vaniller copy =========================

	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	@Override
	public void startOpen(Player player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			BlockState blockstate = this.getBlockState();
			boolean flag = blockstate.getValue(CrateBlock.PROPERTY_OPEN);
			if (!flag) {
				this.playSound(blockstate, SoundEvents.BARREL_OPEN);
				this.setOpenProperty(blockstate, true);
			}

			this.scheduleTick();
		}

	}

	private void scheduleTick() {
		this.level.getBlockTicks().scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 5);
	}

	public void crateTick() {
		int i = this.worldPosition.getX();
		int j = this.worldPosition.getY();
		int k = this.worldPosition.getZ();
		this.numPlayersUsing = calculatePlayersUsing(this.level, this, i, j, k);
		if (this.numPlayersUsing > 0) {
			this.scheduleTick();
		} else {
			BlockState blockstate = this.getBlockState();
			if (!blockstate.is(CrateModule.crate)) {
				this.setRemoved();
				return;
			}

			boolean flag = blockstate.getValue(CrateBlock.PROPERTY_OPEN);
			if (flag) {
				this.playSound(blockstate, SoundEvents.BARREL_CLOSE);
				this.setOpenProperty(blockstate, false);
			}
		}
	}

	public static int calculatePlayersUsing(Level p_213976_0_, BaseContainerBlockEntity p_213976_1_, int p_213976_2_, int p_213976_3_, int p_213976_4_) {
		int i = 0;

		for(Player playerentity : p_213976_0_.getEntitiesOfClass(Player.class, new AABB((double)((float)p_213976_2_ - 5.0F), (double)((float)p_213976_3_ - 5.0F), (double)((float)p_213976_4_ - 5.0F), (double)((float)(p_213976_2_ + 1) + 5.0F), (double)((float)(p_213976_3_ + 1) + 5.0F), (double)((float)(p_213976_4_ + 1) + 5.0F)))) {
			if (playerentity.containerMenu instanceof CrateContainer) {
				Container iinventory = ((CrateContainer)playerentity.containerMenu).crate;
				if (iinventory == p_213976_1_) {
					++i;
				}
			}
		}

		return i;
	}

	@Override
	public void stopOpen(Player player) {
		if (!player.isSpectator()) {
			--this.numPlayersUsing;
		}

	}

	private void setOpenProperty(BlockState state, boolean open) {
		this.level.setBlock(this.getBlockPos(), state.setValue(CrateBlock.PROPERTY_OPEN, Boolean.valueOf(open)), 3);
	}

	private void playSound(BlockState state, SoundEvent sound) {
		double d0 = (double)this.worldPosition.getX() + 0.5D;
		double d1 = (double)this.worldPosition.getY() + 0.5D;
		double d2 = (double)this.worldPosition.getZ() + 0.5D;
		this.level.playSound((Player)null, d0, d1, d2, sound, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
	}

}
