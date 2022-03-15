package vazkii.quark.addons.oddities.block.be;


import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.block.be.SimpleInventoryBlockEntity;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.module.TinyPotatoModule;
import vazkii.quark.base.handler.MiscUtil;
import vazkii.quark.base.handler.QuarkSounds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public class TinyPotatoBlockEntity extends SimpleInventoryBlockEntity implements Nameable {
	public static final String TAG_NAME = "name";
	public static final String TAG_ANGRY = TinyPotatoBlock.ANGRY;
	private static final int JUMP_EVENT = 0;

	public int jumpTicks = 0;
	public Component name = new TextComponent("");
	private int nextDoIt = 0;
	public boolean angry = false;

	public TinyPotatoBlockEntity(BlockPos pos, BlockState state) {
		super(TinyPotatoModule.blockEntityType, pos, state);
	}

	public void interact(Player player, InteractionHand hand, ItemStack stack, Direction side) {
		int index = side.get3DDataValue();
		if (index >= 0) {
			ItemStack stackAt = getItem(index);
			if (!stackAt.isEmpty() && stack.isEmpty()) {
				player.setItemInHand(hand, stackAt);
				setItem(index, ItemStack.EMPTY);
			} else if (!stack.isEmpty()) {
				ItemStack copy = stack.split(1);

				if (stack.isEmpty()) {
					player.setItemInHand(hand, stackAt);
				} else if (!stackAt.isEmpty()) {
					player.getInventory().placeItemBackInInventory(stackAt);
				}

				setItem(index, copy);
			}
		}

		if (level != null && !level.isClientSide) {
			jump();

			if (name.getString().toLowerCase(Locale.ROOT).trim().endsWith("shia labeouf") && nextDoIt == 0) {
				nextDoIt = 40;
				level.playSound(null, worldPosition, QuarkSounds.BLOCK_POTATO_DO_IT, SoundSource.BLOCKS, 1F, 1F);
			}

			for (int i = 0; i < getContainerSize(); i++) {
				ItemStack stackAt = getItem(i);
				if (!stackAt.isEmpty() && stackAt.is(TinyPotatoModule.tiny_potato.asItem())) {
					player.sendMessage(new TranslatableComponent("quark.misc.my_son"), Util.NIL_UUID);
					return;
				}
			}
		}
	}

	private void jump() {
		if (level != null && jumpTicks == 0) {
			level.blockEvent(getBlockPos(), getBlockState().getBlock(), JUMP_EVENT, 20);
		}
	}

	@Override
	public boolean triggerEvent(int id, int param) {
		if (id == JUMP_EVENT) {
			jumpTicks = param;
			return true;
		} else {
			return super.triggerEvent(id, param);
		}
	}

	public static void commonTick(Level level, BlockPos pos, BlockState state, TinyPotatoBlockEntity self) {
		if (self.jumpTicks > 0) {
			self.jumpTicks--;
		}

		if (!level.isClientSide) {
			if (level.random.nextInt(100) == 0) {
				self.jump();
			}
			if (self.nextDoIt > 0) {
				self.nextDoIt--;
			}
		}
	}

	@Override
	public void inventoryChanged(int i) {
		sync();
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null && !level.isClientSide) {
			sync();
		}
	}

	@Override
	public void sync() {
		MiscUtil.syncTE(this);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void readSharedNBT(CompoundTag cmp) {
		super.readSharedNBT(cmp);
		name = Component.Serializer.fromJson(cmp.getString(TAG_NAME));
		angry = cmp.getBoolean(TAG_ANGRY);
	}

	@Override
	public void writeSharedNBT(CompoundTag cmp) {
		super.writeSharedNBT(cmp);
		cmp.putString(TAG_NAME, Component.Serializer.toJson(name));
		cmp.putBoolean(TAG_ANGRY, angry);
	}

	@Override
	public int getContainerSize() {
		return 6;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean canPlaceItem(int slot, @Nonnull ItemStack itemstack) {
		return this.getItem(slot).isEmpty();
	}

	@Nonnull
	@Override
	public Component getName() {
		return new TranslatableComponent(TinyPotatoModule.tiny_potato.getDescriptionId());
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return name.getString().isEmpty() ? null : name;
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		if (hasCustomName()) {
			Component customName = getCustomName();
			if (customName != null)
				return customName;
		}
		return getName();
	}
}
