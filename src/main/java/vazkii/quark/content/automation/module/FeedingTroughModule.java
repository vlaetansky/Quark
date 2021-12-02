package vazkii.quark.content.automation.module;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
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

    private static final ThreadLocal<Set<FeedingTroughBlockEntity>> loadedTroughs = ThreadLocal.withInitial(HashSet::new);

    @SubscribeEvent
    public void buildTroughSet(TickEvent.WorldTickEvent event) {
        Set<FeedingTroughBlockEntity> troughs = loadedTroughs.get();
        if (event.side == LogicalSide.SERVER) {
            if (event.phase == TickEvent.Phase.START) {
                breedingOccurred.remove();
                for (TickingBlockEntity ticking : event.world.blockEntityTickers) { 
                	BlockEntity tile = event.world.getBlockEntity(ticking.getPos());
                    if (tile instanceof FeedingTroughBlockEntity)
                        troughs.add((FeedingTroughBlockEntity) tile);
                }
            } else {
                troughs.clear();
            }
        }
    }

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

    public static Player temptWithTroughs(TemptGoal goal, Player found) {
        if (!ModuleLoader.INSTANCE.isModuleEnabled(FeedingTroughModule.class) ||
                (found != null && (goal.items.test(found.getMainHandItem()) || goal.items.test(found.getOffhandItem()))))
            return found;

        if (!(goal.mob instanceof Animal) ||
                !((Animal) goal.mob).canFallInLove() ||
                ((Animal) goal.mob).getAge() != 0)
            return found;

        double shortestDistanceSq = Double.MAX_VALUE;
        BlockPos location = null;
        FakePlayer target = null;

        Set<FeedingTroughBlockEntity> troughs = loadedTroughs.get();
        for (FeedingTroughBlockEntity tile : troughs) {
            BlockPos pos = tile.getBlockPos();
            double distanceSq = pos.distSqr(goal.mob.position(), true);
            if (distanceSq <= range * range && distanceSq < shortestDistanceSq) {
                FakePlayer foodHolder = tile.getFoodHolder(goal);
                if (foodHolder != null) {
                    shortestDistanceSq = distanceSq;
                    target = foodHolder;
                    location = pos.immutable();
                }
            }
        }

        if (target != null) {
        	Vec3 eyesPos = goal.mob.position().add(0, goal.mob.getEyeHeight(), 0);
            Vec3 targetPos = new Vec3(location.getX(), location.getY(), location.getZ()).add(0.5, 0.0625, 0.5);
            BlockHitResult ray = goal.mob.level.clip(new ClipContext(eyesPos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, goal.mob));

            if (ray.getType() == HitResult.Type.BLOCK && ray.getBlockPos().equals(location))
                return target;
        }

        return found;
    }

    @Override
    public void construct() {
        Block feedingTrough = new FeedingTroughBlock("feeding_trough", this, CreativeModeTab.TAB_DECORATIONS,
                Block.Properties.of(Material.WOOD).strength(0.6F).sound(SoundType.WOOD));
        blockEntityType = BlockEntityType.Builder.of(FeedingTroughBlockEntity::new, feedingTrough).build(null);
        RegistryHelper.register(blockEntityType, "feeding_trough");
    }
}
