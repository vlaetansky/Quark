package vazkii.quark.addons.oddities.container;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import vazkii.quark.addons.oddities.module.CrateModule;
import vazkii.quark.addons.oddities.tile.CrateTileEntity;
import vazkii.quark.base.network.QuarkNetwork;
import vazkii.quark.base.network.message.ScrollCrateMessage;

public class CrateContainer extends Container {

	public final CrateTileEntity crate;
	public final PlayerInventory playerInv;

	public final int numRows = 6;
	public final int numCols = 9;

	public int scroll = 0;
	private final IIntArray crateData;

	public CrateContainer(int id, PlayerInventory inv, CrateTileEntity crate) {
		this(id, inv, crate, new IntArray(2));
	}

	public CrateContainer(int id, PlayerInventory inv, CrateTileEntity crate, IIntArray data) {
		super(CrateModule.containerType, id);

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
		
		trackIntArray(crateData);
	}
	
	public int getTotal() {
		return crateData.get(0);
	}
	
	public int getStackCount() {
		return crateData.get(1);
	}
	
	// TODO support shift click
	
	public static CrateContainer fromNetwork(int windowId, PlayerInventory playerInventory, PacketBuffer buf) {
		BlockPos pos = buf.readBlockPos();
		CrateTileEntity te = (CrateTileEntity) playerInventory.player.world.getTileEntity(pos);
		return new CrateContainer(windowId, playerInventory, te);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return crate.isUsableByPlayer(playerIn);
	}

	public void scroll(boolean down, boolean packet) {
		if(packet)
			QuarkNetwork.sendToServer(new ScrollCrateMessage(down));

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

		if(did)
			detectAndSendChanges();
	}

	private class CrateSlot extends Slot {

		private final int index;

		public CrateSlot(int index, int xPosition, int yPosition) {
			super(crate, index, xPosition, yPosition);

			this.index = index;
		}

		private int getTarget() {
			World world = crate.getWorld();
			return (world.isRemote ? index : (index + scroll));
		}

		@Override
		public ItemStack getStack() {
			int targetIndex = getTarget();
			int size = crate.getSizeInventory();
			if(targetIndex >= size)
				return ItemStack.EMPTY;

			return crate.getStackInSlot(targetIndex);
		}

		@Override
		public void putStack(ItemStack stack) {
			int targetIndex = getTarget();
			inventory.setInventorySlotContents(targetIndex, stack);

			onSlotChanged();

			World world = crate.getWorld();
			if(!world.isRemote) {
				List<IContainerListener> listeners = ObfuscationReflectionHelper.getPrivateValue(Container.class, CrateContainer.this, "listeners"); // TODO
				for(IContainerListener icontainerlistener : listeners)
					icontainerlistener.sendAllContents(CrateContainer.this, getInventory());
			}
		}

		@Override
		public ItemStack decrStackSize(int amount) {
			return inventory.decrStackSize(getTarget(), amount);
		}

	}
}
