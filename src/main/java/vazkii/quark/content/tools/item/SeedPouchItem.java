package vazkii.quark.content.tools.item;

import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.api.ITrowelable;
import vazkii.quark.api.IUsageTickerOverride;
import vazkii.quark.base.item.QuarkItem;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.content.tools.module.SeedPouchModule;

public class SeedPouchItem extends QuarkItem implements IUsageTickerOverride, ITrowelable {

	public static final String TAG_STORED_ITEM = "storedItem";
	public static final String TAG_COUNT = "itemCount";

	public SeedPouchItem(QuarkModule module) {
		super("seed_pouch", module, 
				new Item.Properties()
				.stacksTo(1)
				.durability(SeedPouchModule.maxItems + 1)
				.tab(CreativeModeTab.TAB_TOOLS));
	}
	
    @OnlyIn(Dist.CLIENT)
    public static float itemFraction(ItemStack stack, ClientLevel world, LivingEntity entityIn) {
    	if(entityIn instanceof Player) {
    		Player player = (Player) entityIn;
    		ItemStack held = player.inventory.getCarried();
    		
    		if(canTakeItem(stack, held))
    			return 0F;
    	} 
    	
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents == null)
			return 0F;
		
		return (float) contents.getRight() / (float) SeedPouchModule.maxItems;
    }

    public static Pair<ItemStack, Integer> getContents(ItemStack stack) {
		CompoundTag nbt = ItemNBTHelper.getCompound(stack, TAG_STORED_ITEM, true);
		if(nbt == null)
			return null;

		ItemStack contained = ItemStack.of(nbt);
		int count = ItemNBTHelper.getInt(stack, TAG_COUNT, 0);
		return Pair.of(contained, count);
	}
    
    public static boolean canTakeItem(ItemStack stack, ItemStack incoming) {
		Pair<ItemStack, Integer> contents = getContents(stack);
		
		if(contents == null)
			return incoming.getItem().is(SeedPouchModule.seedPouchHoldableTag);
		
		return contents.getRight() < SeedPouchModule.maxItems && ItemStack.isSame(incoming, contents.getLeft());
    }

	public static void setItemStack(ItemStack stack, ItemStack target) {
		ItemStack copy = target.copy();
		copy.setCount(1);

		CompoundTag nbt = new CompoundTag();
		copy.save(nbt);

		ItemNBTHelper.setCompound(stack, TAG_STORED_ITEM, nbt);
		setCount(stack, target.getCount());
	}
	
	public static void setCount(ItemStack stack, int count) {
		if(count <= 0) {
			stack.getTag().remove(TAG_STORED_ITEM);
			stack.setDamageValue(0);

			return;
		}
		
		ItemNBTHelper.setInt(stack, TAG_COUNT, count);
		stack.setDamageValue(SeedPouchModule.maxItems + 1 - count);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0x00FF00;
	}
	
	@Override
	public Component getName(ItemStack stack) {
		Component base = super.getName(stack);
	
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents == null)
			return base;
		
		MutableComponent comp = base.copy();
		comp.append(new TextComponent(" ("));
		comp.append(contents.getLeft().getHoverName());
		comp.append(new TextComponent(")"));
		return comp;
}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		ItemStack stack = context.getItemInHand();
		Pair<ItemStack, Integer> contents = getContents(stack);
		if(contents != null) {
			ItemStack target = contents.getLeft().copy();
			int total = contents.getRight();
			
			target.setCount(Math.min(target.getMaxStackSize(), total));
			
			Player player = context.getPlayer();
			if(!player.isShiftKeyDown())
				return placeSeed(context, target, context.getClickedPos(), total);
			
			else {
				InteractionResult bestRes = InteractionResult.FAIL;
				
				int range = SeedPouchModule.shiftRange;
				int blocks = range * range;
				int shift = -((int) Math.floor(range / 2));
						
				for(int i = 0; i < blocks; i++) {
					int x = shift + i % range;
					int z = shift + i / range;
					
					InteractionResult res = placeSeed(context, target, context.getClickedPos().offset(x, 0, z), total);
					contents = getContents(stack);
					if(contents == null)
						break;
					total = contents.getRight();
					
					if(!bestRes.consumesAction())
						bestRes = res;
				}
				
				return bestRes;
			}
		}
		
		return super.useOn(context);
	}
	
	private InteractionResult placeSeed(UseOnContext context, ItemStack target, BlockPos pos, int total) {
		InteractionResult res = target.getItem().useOn(new PouchItemUseContext(context, target, pos));
		int diff = res == InteractionResult.CONSUME ? 1 : 0;
		if(diff > 0 && !context.getPlayer().isCreative())
			setCount(context.getItemInHand(), total - diff);
		
		return res;
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		super.fillItemCategory(group, items);
		
		if(SeedPouchModule.showAllVariantsInCreative && isEnabled() && allowdedIn(group)) {
			List<Item> tagItems = null;
			
			try {
				tagItems = SeedPouchModule.seedPouchHoldableTag.getValues();
			} catch(IllegalStateException e) { // Tag not bound yet
				return;
			}
			
			for(Item i : tagItems) {
				if(!ModuleLoader.INSTANCE.isItemEnabled(i))
					continue;
				
				ItemStack stack = new ItemStack(this);
				setItemStack(stack, new ItemStack(i));
				setCount(stack, SeedPouchModule.maxItems);
				items.add(stack);
			}
		}
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
	
	class PouchItemUseContext extends UseOnContext {

		protected PouchItemUseContext(UseOnContext parent, ItemStack stack, BlockPos targetPos) {
			super(parent.getLevel(), parent.getPlayer(), parent.getHand(), stack, 
					new BlockHitResult(parent.getClickLocation(), parent.getClickedFace(), targetPos, parent.isInside()));
		}

	}

}
