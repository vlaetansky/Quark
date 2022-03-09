package vazkii.quark.addons.oddities.block.be;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.addons.oddities.inventory.EnchantmentMatrix;
import vazkii.quark.addons.oddities.inventory.EnchantmentMatrix.Piece;
import vazkii.quark.addons.oddities.inventory.MatrixEnchantingMenu;
import vazkii.quark.addons.oddities.module.MatrixEnchantingModule;
import vazkii.quark.api.IEnchantmentInfluencer;
import vazkii.quark.api.IModifiableEnchantmentInfluencer;

import javax.annotation.Nonnull;

public class MatrixEnchantingTableBlockEntity extends AbstractEnchantingTableBlockEntity implements MenuProvider {

	public static final int OPER_ADD = 0;
	public static final int OPER_PLACE = 1;
	public static final int OPER_REMOVE = 2;
	public static final int OPER_ROTATE = 3;
	public static final int OPER_MERGE = 4;

	public static final String TAG_STACK_MATRIX = "quark:enchantingMatrix";

	private static final String TAG_MATRIX = "matrix";
	private static final String TAG_MATRIX_UUID_LESS = "uuidLess";
	private static final String TAG_MATRIX_UUID_MOST = "uuidMost";
	private static final String TAG_CHARGE = "charge";

	public EnchantmentMatrix matrix;
	private boolean matrixDirty = false;
	public boolean clientMatrixDirty = false;
	private UUID matrixId;

	public final Map<Enchantment, Integer> influences = new HashMap<>();
	public int bookshelfPower, enchantability, charge;

	public MatrixEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
		super(MatrixEnchantingModule.blockEntityType, pos, state);
	}

	public static void tick(Level level, BlockPos pos, BlockState state, MatrixEnchantingTableBlockEntity be) {
		be.tick();
	}

	@Override
	public void tick() {
		super.tick();

		ItemStack item = getItem(0);
		if(item.isEmpty()) {
			if(matrix != null) {
				matrixDirty = true;
				matrix = null;
			}
		} else {
			loadMatrix(item);

			if(level.getGameTime() % 20 == 0 || matrixDirty)
				updateEnchantPower();
		}

		if(charge <= 0 && !level.isClientSide) {
			ItemStack lapis = getItem(1);
			if(!lapis.isEmpty()) {
				lapis.shrink(1);
				charge += MatrixEnchantingModule.chargePerLapis;
				sync();
			}
		}

		if(matrixDirty) {
			makeOutput();
			matrixDirty = false;
		}
	}

	public void onOperation(Player player, int operation, int arg0, int arg1, int arg2) {
		if(matrix == null)
			return;

		switch(operation) {
		case OPER_ADD:
			apply(m -> generateAndPay(m, player));
			break;
		case OPER_PLACE:
			apply(m -> m.place(arg0, arg1, arg2));
			break;
		case OPER_REMOVE:
			apply(m -> m.remove(arg0));
			break;
		case OPER_ROTATE:
			apply(m -> m.rotate(arg0));
			break;
		case OPER_MERGE:
			apply(m -> m.merge(arg0, arg1));
			break;
		}
	}

	private void apply(Predicate<EnchantmentMatrix> oper) {
		if(oper.test(matrix)) {
			ItemStack item = getItem(0);
			commitMatrix(item);
		}
	}

	private boolean generateAndPay(EnchantmentMatrix matrix, Player player) {
		if(matrix.canGeneratePiece(bookshelfPower, enchantability) && matrix.validateXp(player, bookshelfPower)) {
			boolean creative = player.isCreative();
			int cost = matrix.getNewPiecePrice();
			if(charge > 0 || creative) {
				if (matrix.generatePiece(influences, bookshelfPower)) {
					if (!creative) {
						player.giveExperienceLevels(-cost);
						charge = Math.max(charge - 1, 0);
					}
				}
			}
		}

		return true;
	}

	private void makeOutput() {
		if(level.isClientSide)
			return;

		setItem(2, ItemStack.EMPTY);
		ItemStack in = getItem(0);
		if(!in.isEmpty() && matrix != null && !matrix.placedPieces.isEmpty()) {
			ItemStack out = in.copy();
			boolean book = false;
			if(out.getItem() == Items.BOOK) {
				out = new ItemStack(Items.ENCHANTED_BOOK);
				book = true;
			}

			Map<Enchantment, Integer> enchantments = new HashMap<>();

			for(int i : matrix.placedPieces) {
				Piece p = matrix.pieces.get(i);

				if (p != null && p.enchant != null) {
					for (Enchantment o : enchantments.keySet())
						if (o == p.enchant || !p.enchant.isCompatibleWith(o) || !o.isCompatibleWith(p.enchant))
							return; // Incompatible

					enchantments.put(p.enchant, p.level);
				}
			}

			if(book)
				for(Entry<Enchantment, Integer> e : enchantments.entrySet())
					EnchantedBookItem.addEnchantment(out, new EnchantmentInstance(e.getKey(), e.getValue()));
			else {
				EnchantmentHelper.setEnchantments(enchantments, out);
				ItemNBTHelper.getNBT(out).remove(TAG_STACK_MATRIX);
			}

			setItem(2, out);
		}
	}

	private void loadMatrix(ItemStack stack) {
		if(matrix == null || matrix.target != stack) {
			if(matrix != null)
				matrixDirty = true;
			matrix = null;

			if(stack.isEnchantable()) {
				matrix = new EnchantmentMatrix(stack, level.random);
				matrixDirty = true;
				makeUUID();

				if(ItemNBTHelper.verifyExistence(stack, TAG_STACK_MATRIX)) {
					CompoundTag cmp = ItemNBTHelper.getCompound(stack, TAG_STACK_MATRIX, true);
					if(cmp != null)
						matrix.readFromNBT(cmp);
				}
			}
		}
	}

	private void commitMatrix(ItemStack stack) {
		if(level.isClientSide)
			return;

		CompoundTag cmp = new CompoundTag();
		matrix.writeToNBT(cmp);
		ItemNBTHelper.setCompound(stack, TAG_STACK_MATRIX, cmp);

		matrixDirty = true;
		makeUUID();
		sync();
		setChanged();
	}

	private void makeUUID() {
		if(!level.isClientSide)
			matrixId = UUID.randomUUID();
	}

	private void updateEnchantPower() {
		ItemStack item = getItem(0);
		influences.clear();
		if(item.isEmpty())
			return;

		enchantability = item.getItem().getItemEnchantability(item);

		boolean allowWater = MatrixEnchantingModule.allowUnderwaterEnchanting;
		float power = 0;
		for (int j = -1; j <= 1; ++j) {
			for (int k = -1; k <= 1; ++k) {
				if(isAirGap(j, k, allowWater)) {
					power += getEnchantPowerAt(level, worldPosition.offset(k * 2, 0, j * 2));
					power += getEnchantPowerAt(level, worldPosition.offset(k * 2, 1, j * 2));
					if (k != 0 && j != 0) {
						power += getEnchantPowerAt(level, worldPosition.offset(k * 2, 0, j));
						power += getEnchantPowerAt(level, worldPosition.offset(k * 2, 1, j));
						power += getEnchantPowerAt(level, worldPosition.offset(k, 0, j * 2));
						power += getEnchantPowerAt(level, worldPosition.offset(k, 1, j * 2));
					}
				}
			}
		}

		bookshelfPower = Math.min((int) power, MatrixEnchantingModule.maxBookshelves);
	}

	private boolean isAirGap(int j, int k, boolean allowWater) {
		if(j != 0 || k != 0) {
			BlockPos test = worldPosition.offset(k, 0, j);
			BlockPos testUp = test.above();

			return (level.isEmptyBlock(test) || (allowWater && level.getBlockState(test).getBlock() == Blocks.WATER))
					&& (level.isEmptyBlock(testUp) || (allowWater && level.getBlockState(testUp).getBlock() == Blocks.WATER));
		}

		return false;
	}

	private float getEnchantPowerAt(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(MatrixEnchantingModule.allowInfluencing) {
			Block block = state.getBlock();

			IEnchantmentInfluencer influencer = null;
			if(block instanceof IEnchantmentInfluencer)
				influencer = (IEnchantmentInfluencer) block;
			else influencer = CandleInfluencer.forBlock(block);

			if(influencer != null) {
				DyeColor ord = influencer.getEnchantmentInfluenceColor(world, pos, state);
				int count = influencer.getInfluenceStack(world, pos, state);

				if(ord != null && count > 0) {
					List<Enchantment> influencedEnchants = MatrixEnchantingModule.candleInfluences.get(ord);
					if(influencedEnchants != null) {
					    if(influencer instanceof IModifiableEnchantmentInfluencer) {
					        IModifiableEnchantmentInfluencer modifiableInfluencer = (IModifiableEnchantmentInfluencer) influencer;
                            influencedEnchants = modifiableInfluencer.getModifiedEnchantments(world, pos, state, getItem(0), new ArrayList<>(influencedEnchants));
                        }
                        for(Enchantment e : influencedEnchants) {
                            int curr = influences.getOrDefault(e, 0);
                            if(curr < MatrixEnchantingModule.influenceMax)
                                influences.put(e, Math.min(MatrixEnchantingModule.influenceMax, curr + count));
                        }

                        return 1;
                    }
				}
			}
		}

		return state.getEnchantPowerBonus(world, pos);
	}

	@Override
	public void writeSharedNBT(CompoundTag cmp) {
		super.writeSharedNBT(cmp);

		CompoundTag matrixCmp = new CompoundTag();
		if(matrix != null) {
			matrix.writeToNBT(matrixCmp);

			cmp.put(TAG_MATRIX, matrixCmp);
			if(matrixId != null) {
				cmp.putLong(TAG_MATRIX_UUID_LESS, matrixId.getLeastSignificantBits());
				cmp.putLong(TAG_MATRIX_UUID_MOST, matrixId.getMostSignificantBits());
			}
		}
		cmp.putInt(TAG_CHARGE, charge);
	}

	@Override
	public void readSharedNBT(CompoundTag cmp) {
		super.readSharedNBT(cmp);

		if(cmp.contains(TAG_MATRIX)) {
			long least = cmp.getLong(TAG_MATRIX_UUID_LESS);
			long most = cmp.getLong(TAG_MATRIX_UUID_MOST);
			UUID newId = new UUID(most, least);

			if(!newId.equals(matrixId)) {
				CompoundTag matrixCmp = cmp.getCompound(TAG_MATRIX);
				matrixId = newId;
				matrix = new EnchantmentMatrix(getItem(0), new Random());
				matrix.readFromNBT(matrixCmp);
			}
			clientMatrixDirty = true;
		} else matrix = null;

		charge = cmp.getInt(TAG_CHARGE);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
		return new MatrixEnchantingMenu(id, inv, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return getName();
	}

	private static class CandleInfluencer implements IEnchantmentInfluencer {

		private static final List<Block> CANDLES = Lists.newArrayList(Blocks.WHITE_CANDLE, Blocks.ORANGE_CANDLE, Blocks.MAGENTA_CANDLE, Blocks.LIGHT_BLUE_CANDLE, Blocks.YELLOW_CANDLE, Blocks.LIME_CANDLE, Blocks.PINK_CANDLE, Blocks.GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE, Blocks.CYAN_CANDLE, Blocks.PURPLE_CANDLE, Blocks.BLUE_CANDLE, Blocks.BROWN_CANDLE, Blocks.GREEN_CANDLE, Blocks.RED_CANDLE, Blocks.BLACK_CANDLE);
		private static final CandleInfluencer INSTANCE = new CandleInfluencer();

		public static CandleInfluencer forBlock(Block block) {
			if(CANDLES.contains(block))
				return INSTANCE;

			return null;
		}

		@Override
		public DyeColor getEnchantmentInfluenceColor(BlockGetter world, BlockPos pos, BlockState state) {
			int index = CANDLES.indexOf(state.getBlock());
			return index >= 0 ? DyeColor.values()[index] : null;
		}

		@Override
		public int getInfluenceStack(BlockGetter world, BlockPos pos, BlockState state) {
			return state.getValue(CandleBlock.LIT) ? state.getValue(CandleBlock.CANDLES) : 0;
		}

	}

}
