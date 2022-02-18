package vazkii.quark.base.item.boat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

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

public class QuarkBoat extends Boat {

	public static record QuarkBoatType(Item boat, Block planks) {}
	private static Map<String, QuarkBoatType> quarkBoatTypes = new HashMap<>();
	
	private static final EntityDataAccessor<String> DATA_QUARK_TYPE = SynchedEntityData.defineId(QuarkBoat.class, EntityDataSerializers.STRING);
	
	public QuarkBoat(EntityType<? extends Boat> p_38290_, Level p_38291_) {
		super(p_38290_, p_38291_);
	}

	public QuarkBoat(Level p_38293_, double p_38294_, double p_38295_, double p_38296_) {
		this(WoodSetHandler.quarkBoatEntityType, p_38293_);
		this.setPos(p_38294_, p_38295_, p_38296_);
		this.xo = p_38294_;
		this.yo = p_38295_;
		this.zo = p_38296_;
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
	protected void addAdditionalSaveData(CompoundTag p_38359_) {
		super.addAdditionalSaveData(p_38359_);
		p_38359_.putString("QuarkType", getQuarkBoatType());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag p_38338_) {
		super.readAdditionalSaveData(p_38338_);
		if (p_38338_.contains("QuarkType", 8)) {
			setQuarkBoatType(p_38338_.getString("QuarkType"));
		}
	}

	@Override
	public ItemEntity spawnAtLocation(ItemLike p_19999_) {
		if(p_19999_.asItem().getRegistryName().getPath().contains("_planks"))
			return super.spawnAtLocation(getTypeRecord(getQuarkBoatType()).planks);
		return super.spawnAtLocation(p_19999_);
	}
	
	@Override
	public Item getDropItem() {
		return getTypeRecord(getQuarkBoatType()).boat;
	}

	@Override
	public Type getBoatType() {
		return Boat.Type.OAK;
	}

	@Override
	public void setType(Type p_38333_) { 
		// NO-OP
	}

}
