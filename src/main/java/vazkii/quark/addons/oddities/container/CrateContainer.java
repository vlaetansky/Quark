package vazkii.quark.addons.oddities.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ScrollCrateMessage;

public class CrateContainer extends AbstractContainerMenu {

	public final CrateTileEntity crate;
	public final Inventory playerInv;

	public static final int numRows = 6;
	public static final int numCols = 9;
	public static final int displayedSlots = numCols * numRows;

	public int scroll = 0;
	private final ContainerData crateData;

	public CrateContainer(int id, Inventory inv, CrateTileEntity crate) {
		this(id, inv, crate, new SimpleContainerData(2));
	}

	public CrateContainer(int id, Inventory inv, CrateTileEntity crate, ContainerData data) {
		super(CrateModule.containerType, id);
		crate.startOpen(inv.player);

		this.crate = crate;
		this.playerInv = inv;
		this.crateData = data;

		int i = (numRows - 4) * 18;

		for(int j = 0; j < numRows; ++j)
			for(int k = 0; k < numCols; ++k)
				addSlot(new CrateSlot(k + j * numCols, 8 + k * 18, 18 + j * 18));

		for(int l = 0; l < 3; ++l)
			for(int j1 = 0; j1 < 9; ++j1)
				addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));

		for(int i1 = 0; i1 < 9; ++i1)
			addSlot(new Slot(inv, i1, 8 + i1 * 18, 161 + i));

		addDataSlots(crateData);
	}

	public int getTotal() {
		return crateData.get(0);
	}

	public int getStackCount() {
		return crateData.get(1);
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if(slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			boolean empty = false;

			if (index < displayedSlots) {
				if(!this.moveItemStackTo(itemstack1, displayedSlots, slots.size(), true))
					empty = true;
				crate.setChanged();
			} else {
				if(MiscUtil.canPutIntoInv(itemstack, crate, Direction.UP, true)) {
					MiscUtil.putIntoInv(itemstack, crate, Direction.UP, false, false);
					itemstack1.setCount(0);
					empty = true;
				} else return ItemStack.EMPTY;
			}

			if(itemstack1.isEmpty()) {
				if(slot instanceof CrateSlot) {
					CrateSlot cslot = (CrateSlot) slot;
					int target = cslot.getTarget();
					crate.removeItemNoUpdate(target);
				}
				else slot.set(ItemStack.EMPTY);
			} else if(!empty) {
				slot.setChanged();
				forceSync();
			}

			if(empty)
				return ItemStack.EMPTY;
		}

		return itemstack;
	}

	public static CrateContainer fromNetwork(int windowId, Inventory playerInventory, FriendlyByteBuf buf) {
		BlockPos pos = buf.readBlockPos();
		CrateTileEntity te = (CrateTileEntity) playerInventory.player.level.getBlockEntity(pos);
		return new CrateContainer(windowId, playerInventory, te);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return crate.stillValid(playerIn);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		crate.stopOpen(playerIn);
	}

	private void forceSync() {
		Level world = crate.getLevel();
		if(!world.isClientSide)
			for(ContainerListener icontainerlistener : containerListeners)
				icontainerlistener.refreshContainer(this, getItems());
	}

	public void scroll(boolean down, boolean packet) {
		boolean did = false;

		if(down) {
			int maxScroll = (getStackCount() / numCols) * numCols;

			int target = scroll + numCols;
			if(target <= maxScroll) {
				scroll = target;
				did = true;
			}
		} else {
			int target = scroll - numCols;
			if(target >= 0) {
				scroll = target;
				did = true;
			}
		}

		if(did) {
			broadcastChanges();

			if(packet)
				QuarkNetwork.sendToServer(new ScrollCrateMessage(down));
		}
	}

	private class CrateSlot extends Slot {

		private final int index;

		public CrateSlot(int index, int xPosition, int yPosition) {
			super(crate, index, xPosition, yPosition);

			this.index = index;
		}

		private int getTarget() {
			Level world = crate.getLevel();
			return (world.isClientSide ? index : (index + scroll));
		}

		@Override
		public ItemStack getItem() {
			int targetIndex = getTarget();
			int size = crate.getContainerSize();
			if(targetIndex >= size)
				return ItemStack.EMPTY;

			return crate.getItem(targetIndex);
		}

		@Override
		public void set(ItemStack stack) {
			int targetIndex = getTarget();
			container.setItem(targetIndex, stack);

			setChanged();
			forceSync();
		}

		@Override
		public ItemStack remove(int amount) {
			return container.removeItem(getTarget(), amount);
		}
		
		@Override
		public boolean mayPlace(ItemStack stack) {
			return stack.getCount() + getTotal() <= CrateModule.maxItems;
		}

	}
}
