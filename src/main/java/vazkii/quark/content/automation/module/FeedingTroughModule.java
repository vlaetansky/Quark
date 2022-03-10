package vazkii.quark.content.automation.module;

import java.util.Objects;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.content.automation.block.FeedingTroughBlock;
import vazkii.quark.content.automation.block.be.FeedingTroughBlockEntity;

/**
 * @author WireSegal
 * Created at 9:48 AM on 9/20/19.
 */
@LoadModule(category = ModuleCategory.AUTOMATION, hasSubscriptions = true)
public class FeedingTroughModule extends QuarkModule {
	public static BlockEntityType<FeedingTroughBlockEntity> blockEntityType;
	public static PoiType feedingTroughPoi;

	@Config(description = "How long, in game ticks, between animals being able to eat from the trough")
	@Config.Min(1)
	public static int cooldown = 30;

	@Config(description = "The maximum amount of animals allowed around the trough's range for an animal to enter love mode")
	public static int maxAnimals = 32;

	@Config(description = "The chance (between 0 and 1) for an animal to enter love mode when eating from the trough")
	@Config.Min(value = 0.0, exclusive = true)
	@Config.Max(1.0)
	public static double loveChance = 0.333333333;

	@Config public static double range = 10;

	private static final ThreadLocal<Boolean> breedingOccurred = ThreadLocal.withInitial(() -> false);

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onBreed(BabyEntitySpawnEvent event) {
		if (event.getCausedByPlayer() == null && event.getParentA().level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))
			breedingOccurred.set(true);
	}

	@SubscribeEvent
	public void onOrbSpawn(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof ExperienceOrb && breedingOccurred.get()) {
			event.setCanceled(true);
			breedingOccurred.remove();
		}
	}

	public static Player temptWithTroughs(TemptGoal goal, Player found, ServerLevel level) {
		if (!ModuleLoader.INSTANCE.isModuleEnabled(FeedingTroughModule.class) ||
				(found != null && (goal.items.test(found.getMainHandItem()) || goal.items.test(found.getOffhandItem()))))
			return found;

		if (!(goal.mob instanceof Animal animal) ||
				!animal.canFallInLove() ||
				animal.getAge() != 0)
			return found;

		Vec3 position = animal.position();
		Pair<BlockPos, FakePlayer> pair = level.getPoiManager().findAllClosestFirst(
					feedingTroughPoi.getPredicate(), p -> p.distSqr(new Vec3i(position.x, position.y, position.z)) <= range * range,
						animal.blockPosition(), (int) range, PoiManager.Occupancy.ANY)
				.map(pos -> level.getBlockEntity(pos) instanceof FeedingTroughBlockEntity trough ? trough : null)
				.filter(Objects::nonNull)
				.map(trough -> Pair.of(trough.getBlockPos(), trough.getFoodHolder(goal)))
				.filter(p -> p.getSecond() != null)
				.findFirst()
				.orElse(null);

		if (pair != null) {
			BlockPos location = pair.getFirst();
			Vec3 eyesPos = goal.mob.position().add(0, goal.mob.getEyeHeight(), 0);
			Vec3 targetPos = new Vec3(location.getX(), location.getY(), location.getZ()).add(0.5, 0.0625, 0.5);
			BlockHitResult ray = goal.mob.level.clip(new ClipContext(eyesPos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, goal.mob));

			if (ray.getType() == HitResult.Type.BLOCK && ray.getBlockPos().equals(location))
				return pair.getSecond();
		}

		return found;
	}

	@Override
	public void register() {
		Block feedingTrough = new FeedingTroughBlock("feeding_trough", this, CreativeModeTab.TAB_DECORATIONS,
				Block.Properties.of(Material.WOOD).strength(0.6F).sound(SoundType.WOOD));
		blockEntityType = BlockEntityType.Builder.of(FeedingTroughBlockEntity::new, feedingTrough).build(null);
		RegistryHelper.register(blockEntityType, "feeding_trough");
		feedingTroughPoi = new PoiType("quark:feeding_trough", PoiType.getBlockStates(feedingTrough), 1, 32);
		RegistryHelper.register(feedingTroughPoi, "feeding_trough");
	}
}
