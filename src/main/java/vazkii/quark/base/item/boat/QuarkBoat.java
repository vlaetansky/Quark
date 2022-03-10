package vazkii.quark.base.item.boat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import vazkii.quark.base.handler.WoodSetHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class QuarkBoat extends Boat {

	public record QuarkBoatType(Item boat, Block planks) {}
	private static final Map<String, QuarkBoatType> quarkBoatTypes = new HashMap<>();

	private static final EntityDataAccessor<String> DATA_QUARK_TYPE = SynchedEntityData.defineId(QuarkBoat.class, EntityDataSerializers.STRING);

	public QuarkBoat(EntityType<? extends Boat> entityType, Level world) {
		super(entityType, world);
	}

	public QuarkBoat(Level world, double x, double y, double z) {
		this(WoodSetHandler.quarkBoatEntityType, world);
		this.setPos(x, y, z);
		this.xo = x;
		this.yo = y;
		this.zo = z;
	}

	public static void addQuarkBoatType(String name, QuarkBoatType type) {
		quarkBoatTypes.put(name, type);
	}

	public static QuarkBoatType getTypeRecord(String name) {
		return quarkBoatTypes.get(name);
	}

	public static Stream<String> boatTypes() {
		return quarkBoatTypes.keySet().stream();
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(DATA_QUARK_TYPE, "blossom");
	}

	public String getQuarkBoatType() {
		return entityData.get(DATA_QUARK_TYPE);
	}

	public void setQuarkBoatType(String type) {
		entityData.set(DATA_QUARK_TYPE, type);
	}

	@Override
	protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putString("QuarkType", getQuarkBoatType());
	}

	@Override
	protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.contains("QuarkType", 8)) {
			setQuarkBoatType(tag.getString("QuarkType"));
		}
	}

	@Override
	public ItemEntity spawnAtLocation(ItemLike itemLike) {
		if(itemLike.asItem().getRegistryName().getPath().contains("_planks"))
			return super.spawnAtLocation(getTypeRecord(getQuarkBoatType()).planks);
		return super.spawnAtLocation(itemLike);
	}

	@Nonnull
	@Override
	public Item getDropItem() {
		return getTypeRecord(getQuarkBoatType()).boat;
	}

	@Nonnull
	@Override
	public Type getBoatType() {
		return Boat.Type.OAK;
	}

	@Override
	public void setType(@Nonnull Type type) {
		// NO-OP
	}

}
