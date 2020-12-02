package vazkii.quark.tools.item;

import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.IUsageTickerOverride;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.tools.module.SeedPouchModule;

public class SeedPouchItem extends QuarkItem implements IUsageTickerOverride {

	public static final String TAG_STORED_ITEM = "storedItem";
	public static final String TAG_COUNT = "itemCount";

	public SeedPouchItem(QuarkModule module) {
		super("seed_pouch", module, 
				new Item.Properties()
				.maxStackSize(1)
				.maxDamage(SeedPouchModule.maxItems + 1)
				.group(ItemGroup.TOOLS));
	}

	public static Pair<ItemStack, Integer> getContents(ItemStack stack) {
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack, TAG_STORED_ITEM, true);
		if(nbt == null)
			return null;

		ItemStack contained = ItemStack.read(nbt);
		int count = ItemNBTHelper.getInt(stack, TAG_COUNT, 0);
		return Pair.of(contained, count);
	}

	public static void setItemStack(ItemStack stack, ItemStack target) {
		ItemStack copy = target.copy();
		copy.setCount(1);

		CompoundNBT nbt = new CompoundNBT();
		copy.write(nbt);

		ItemNBTHelper.setCompound(stack, TAG_STORED_ITEM, nbt);
		setCount(stack, target.getCount());
	}
	
	public static void setCount(ItemStack stack, int count) {
		if(count <= 0) {
			stack.getTag().remove(TAG_STORED_ITEM);
			stack.setDamage(0);

			return;
		}
		
		ItemNBTHelper.setInt(stack, TAG_COUNT, count);
		stack.setDamage(SeedPouchModule.maxItems + 1 - count);
	}
	
	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		ITextComponent base = super.getDisplayName(stack);
	
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents == null)
			return base;
		
		IFormattableTextComponent comp = base.deepCopy();
		comp.append(new StringTextComponent(" ("));
		comp.append(contents.getLeft().getDisplayName());
		comp.append(new StringTextComponent(")"));
		return comp;
}

	@Override
	public ActionResultType onItemUse(ItemUseContext context) {
		ItemStack stack = context.getItem();
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents != null) {
			ItemStack target = contents.getLeft().copy();
			int total = contents.getRight();
			
			target.setCount(Math.min(target.getMaxStackSize(), total));
			
			PlayerEntity player = context.getPlayer();
			if(!player.isSneaking())
				return placeSeed(context, target, context.getPos(), total);
			
			else {
				ActionResultType bestRes = ActionResultType.FAIL;
				
				for(int i = 0; i < 9; i++) {
					int x = -1 + i % 3;
					int z = -1 + i / 3;
					
					ActionResultType res = placeSeed(context, target, context.getPos().add(x, 0, z), total);
					contents = getContents(stack);
					if(contents == null)
						break;
					total = contents.getRight();
					
					if(!bestRes.isSuccessOrConsume())
						bestRes = res;
				}
				
				return bestRes;
			}
		}
		
		return super.onItemUse(context);
	}
	
	private ActionResultType placeSeed(ItemUseContext context, ItemStack target, BlockPos pos, int total) {
		ActionResultType res = target.getItem().onItemUse(new PouchItemUseContext(context, target, pos));
		int diff = res == ActionResultType.CONSUME ? 1 : 0;
		if(diff > 0 && !context.getPlayer().isCreative())
			setCount(context.getItem(), total - diff);
		
		return res;
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		super.fillItemGroup(group, items);
		
		if(isEnabled() || group == ItemGroup.SEARCH) {
			items.add(makeOf(Items.WHEAT_SEEDS));
			items.add(makeOf(Items.BEETROOT_SEEDS));
			items.add(makeOf(Items.MELON_SEEDS));
			items.add(makeOf(Items.PUMPKIN_SEEDS));
		}
	}
	
	private ItemStack makeOf(Item seed) {
		ItemStack stack = new ItemStack(SeedPouchModule.seed_pouch);
		setItemStack(stack, new ItemStack(seed));
		setCount(stack, SeedPouchModule.maxItems);
		return stack;
	}
	
	@Override
	public ItemStack getUsageTickerItem(ItemStack stack) {
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents != null)
			return contents.getLeft();
		
		return stack;
	}
	

	@Override
	public int getUsageTickerCountForItem(ItemStack stack, Predicate<ItemStack> target) {
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents != null && target.test(contents.getLeft()))
			return contents.getRight();
		
		return 0;
	}
	
	class PouchItemUseContext extends ItemUseContext {

		protected PouchItemUseContext(ItemUseContext parent, ItemStack stack, BlockPos targetPos) {
			super(parent.getWorld(), parent.getPlayer(), parent.getHand(), stack, 
					new BlockRayTraceResult(parent.getHitVec(), parent.getFace(), targetPos, parent.isInside()));
		}

	}

	
}
