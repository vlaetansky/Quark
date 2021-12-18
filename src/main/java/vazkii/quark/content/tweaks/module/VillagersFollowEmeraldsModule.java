package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

/**
 * @author WireSegal
 * Created at 11:25 AM on 9/2/19.
 */
@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class VillagersFollowEmeraldsModule extends QuarkModule {

    @SubscribeEvent
    public void onVillagerAppear(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof Villager) {
            Villager villager = (Villager) event.getEntity();
            boolean alreadySetUp = villager.goalSelector.getAvailableGoals().stream().anyMatch((goal) -> goal.getGoal() instanceof TemptGoal);

            if (!alreadySetUp)
            	try {
            		villager.goalSelector.addGoal(2, new TemptGoal(villager, 0.6, Ingredient.of(Items.EMERALD_BLOCK), false));
            	} catch(IllegalArgumentException e) {
            		// This appears to be a weird bug that happens when a villager is riding something and its chunk unloads
            	}
                
        }
    }
}
