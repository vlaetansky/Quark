package vazkii.quark.base.recipe.ingredient;

import java.util.Objects;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import vazkii.quark.base.handler.BrewingHandler;

/**
 * @author WireSegal
 * Created at 5:10 PM on 9/23/19.
 */
public class PotionIngredient extends Ingredient {
    private final Item item;
    private final Potion potion;

    public PotionIngredient(Item item, Potion potion) {
        super(Stream.of(new Ingredient.ItemValue(BrewingHandler.of(item, potion))));
        this.item = item;
        this.potion = potion;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return item == input.getItem() && PotionUtils.getPotion(input) == potion;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return PotionIngredient.Serializer.INSTANCE;
    }

    @Nonnull
    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", Objects.toString(CraftingHelper.getID(PotionIngredient.Serializer.INSTANCE)));
        json.addProperty("item", Objects.toString(item.getRegistryName()));
        json.addProperty("potion", Objects.toString(potion.getRegistryName()));
        return json;
    }

    public static class Serializer implements IIngredientSerializer<PotionIngredient> {
        public static final PotionIngredient.Serializer INSTANCE = new PotionIngredient.Serializer();

        @Nonnull
        @Override
        public PotionIngredient parse(@Nonnull FriendlyByteBuf buffer) {
            Item item = Registry.ITEM.getOptional(buffer.readResourceLocation()).get();
            Potion potion = Registry.POTION.getOptional(buffer.readResourceLocation()).get();
            return new PotionIngredient(item, potion);
        }

        @Nonnull
        @Override
        public PotionIngredient parse(@Nonnull JsonObject json) {
            Item item = Registry.ITEM.getOptional(new ResourceLocation(json.getAsJsonPrimitive("item").getAsString())).get();
            Potion potion = Registry.POTION.getOptional(new ResourceLocation(json.getAsJsonPrimitive("item").getAsString())).get();
            return new PotionIngredient(item, potion);
        }

        @Override
        public void write(@Nonnull FriendlyByteBuf buffer, @Nonnull PotionIngredient ingredient) {
            buffer.writeUtf(Objects.toString(Registry.ITEM.getId(ingredient.item)));
            buffer.writeUtf(Objects.toString(Registry.POTION.getId(ingredient.potion)));
        }
    }
}
