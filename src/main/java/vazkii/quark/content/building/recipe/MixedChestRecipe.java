package vazkii.quark.content.building.recipe;

import com.google.gson.JsonObject;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class MixedChestRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {
	
    public static final Serializer SERIALIZER = new Serializer();

	final ResourceLocation res;
	NonNullList<Ingredient> ingredients;
	
	final boolean log;
	final ItemStack output;
	final Tag.Named<Item> tag;
	final ItemStack placeholder;
	
	public MixedChestRecipe(ResourceLocation res, boolean log) {
		this.res = res;
		
		this.log = log;
		this.output = new ItemStack(Items.CHEST, (log ? 4 : 1));
		this.tag = (log ? ItemTags.LOGS : ItemTags.PLANKS);
		this.placeholder = new ItemStack(log ? Items.OAK_LOG : Items.OAK_PLANKS);
	}
	
	@Override
	public boolean canCraftInDimensions(int x, int y) {
		return x == 3 && y == 3;
	}

	@Override
	public ItemStack assemble(CraftingContainer arg0) {
		return output.copy();
	}

	@Override
	public ResourceLocation getId() {
		return res;
	}

	@Override
	public ItemStack getResultItem() {
		return output.copy();	
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}

	@Override
	public boolean matches(CraftingContainer inv, Level world) {
		if(inv.getItem(4).isEmpty()) {
			ItemStack first = null;
			boolean foundDifference = false;
			
			for(int i = 0; i < 9; i++)
				if(i != 4) { // ignore center
					ItemStack stack = inv.getItem(i);
					if(!stack.isEmpty() && stack.getItem().is(tag)) {
						if(first == null)
							first = stack;
						else if(!ItemStack.isSame(first, stack))
							foundDifference = true;
					} else return false;
				}
			
			return foundDifference;
		}
		
		return false;
	}

	@Override
	public int getRecipeWidth() {
		return 3;
	}

	@Override
	public int getRecipeHeight() {
		return 3;
	}
	
	@Override
	public NonNullList<Ingredient> getIngredients() {
		if(ingredients == null) {
			NonNullList<Ingredient> list = NonNullList.withSize(9, Ingredient.EMPTY);
			Ingredient ingr = Ingredient.of(placeholder);
			for(int i = 0; i < 8; i++)
				list.set(i < 4 ? i : i + 1, ingr);
			ingredients = list;
		}
		
		return ingredients;
	}
	
	@Override
	public boolean isSpecial() {
		return true;
	}
	
	private static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<MixedChestRecipe> {
		
        public Serializer() {
            setRegistryName("quark:mixed_chest");
        }

		@Override
		public MixedChestRecipe fromJson(ResourceLocation arg0, JsonObject arg1) {
			return new MixedChestRecipe(arg0, arg1.get("log").getAsBoolean());
		}

		@Override
		public MixedChestRecipe fromNetwork(ResourceLocation arg0, FriendlyByteBuf arg1) {
			return new MixedChestRecipe(arg0, arg1.readBoolean());
		}

		@Override
		public void toNetwork(FriendlyByteBuf arg0, MixedChestRecipe arg1) {
			arg0.writeBoolean(arg1.log);
		}
		
	}

}
