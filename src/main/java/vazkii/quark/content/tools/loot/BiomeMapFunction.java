package vazkii.quark.content.tools.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3;
import vazkii.quark.content.tools.module.PathfinderMapsModule;

import javax.annotation.Nonnull;
import java.util.Set;

public class BiomeMapFunction extends LootItemConditionalFunction {

	private final TagKey<Biome> destination;
	private final boolean underground;

	public BiomeMapFunction(LootItemCondition[] conditions, TagKey<Biome> destination, boolean underground) {
		super(conditions);
		this.destination = destination;
		this.underground = underground;
	}

	@Override
	@Nonnull
	public LootItemFunctionType getType() {
		return PathfinderMapsModule.pathfinderMapType;
	}

	@Nonnull
	@Override
	public ItemStack run(ItemStack stack, @Nonnull LootContext context) {
		if (stack.is(Items.MAP)) {
			Vec3 vec = context.getParam(LootContextParams.ORIGIN);
			if (underground)
				vec = vec.subtract(0, vec.y + 1, 0);
			return PathfinderMapsModule.createMap(context.getLevel(), new BlockPos(vec), (it) -> it.is(destination));

		}
		return stack;
	}

	@Override
	@Nonnull
	public Set<LootContextParam<?>> getReferencedContextParams() {
		return ImmutableSet.of(LootContextParams.ORIGIN);
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<BiomeMapFunction> {
		@Override
		public void serialize(@Nonnull JsonObject object, @Nonnull BiomeMapFunction function, @Nonnull JsonSerializationContext serializationContext) {
			object.addProperty("destination", function.destination.location().toString());
			object.addProperty("underground", function.underground);
		}

		@Override
		@Nonnull
		public BiomeMapFunction deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext, @Nonnull LootItemCondition[] conditionsIn) {
			String key = GsonHelper.getAsString(object, "destination");
			TagKey<Biome> destination = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(key));

			boolean underground = GsonHelper.getAsBoolean(object, "underground");

			return new BiomeMapFunction(conditionsIn, destination, underground);
		}
	}
}
