package vazkii.quark.addons.oddities.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import vazkii.quark.addons.oddities.container.CrateContainer;
import vazkii.quark.addons.oddities.module.CrateModule;

public class CrateTileEntity extends LockableTileEntity implements ISidedInventory, ITickableTileEntity {

	private int totalItems = 0;
	private List<ItemStack> stacks = new ArrayList<>();

	private int[] visibleSlots = new int[0];
	boolean needsUpdate = false;

	protected final IIntArray crateData = new IIntArray() {
		public int get(int index) {
			return index == 0 ? totalItems : stacks.size();
		}

		public void set(int index, int value) {
			// NO-OP
		}

		public int size() {
			return 2;
		}
	};

	public CrateTileEntity() {
		super(CrateModule.crateType);
	}

	public int getTotalItems() {
		return totalItems;
	}
	
	@Override
	public void tick() {
		if(needsUpdate) {
			stacks.removeIf(ItemStack::isEmpty);
			needsUpdate = false;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("totalItems", totalItems);

		ListNBT list = new ListNBT();
		for(ItemStack stack : stacks) {
			CompoundNBT stackCmp = new CompoundNBT();
			stack.write(stackCmp);
			list.add(stackCmp);
		}
		compound.put("stacks", list);

		return super.write(compound);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		totalItems = nbt.getInt("totalItems");

		ListNBT list = nbt.getList("stacks", 10);
		stacks = new ArrayList<>(list.size());
		for(int i = 0; i < list.size(); i++)
			stacks.add(ItemStack.read(list.getCompound(i)));

		super.read(state, nbt);
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot < stacks.size() ? stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		if(slot < stacks.size()) {
			ItemStack stack = getStackInSlot(slot);
			totalItems -= stack.getCount();
			needsUpdate = true;

			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if(slot >= stacks.size()) {
			stacks.add(stack);
			totalItems += stack.getCount();
		} else {
			ItemStack stackAt = getStackInSlot(slot);
			int sizeDiff = stack.getCount() - stackAt.getCount();
			totalItems += sizeDiff;
			stacks.set(slot, stack);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		ItemStack stack = getStackInSlot(slot);
		ItemStack retstack = stack.split(count);
		totalItems -= count;

		if(stack.isEmpty())
			needsUpdate = true;

		return retstack;
	}

	@Override
	public void markDirty() {
		super.markDirty();

		totalItems = 0;
		for(ItemStack stack : stacks)
			totalItems += stack.getCount();
	}

	@Override
	public int getSizeInventory() {
		return Math.min(CrateModule.maxItems, stacks.size() + 1);
	}

	@Override
	public void clear() {
		stacks.clear();
		totalItems = 0;
	}

	@Override
	public boolean isEmpty() {
		return totalItems == 0;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction dir) {
		return true;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction dir) {
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
	protected ITextComponent getDefaultName() {
		return CrateModule.crate.getTranslatedName();
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new CrateContainer(id, player, this, crateData);
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		} else {
			return !(player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) > 64.0D);
		}
	}

}
