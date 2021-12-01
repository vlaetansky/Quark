package vazkii.quark.content.tweaks.module;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;

@LoadModule(category = ModuleCategory.TWEAKS, hasSubscriptions = true)
public class SnowGolemPlayerHeadsModule extends QuarkModule {

	@SubscribeEvent
	public void onDrops(LivingDropsEvent event) {
		Entity e = event.getEntity();

		if(e.hasCustomName() && e instanceof SnowGolem && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Witch) {
			SnowGolem snowman = (SnowGolem) e;
			if(snowman.hasPumpkin()) { 
				ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
				ItemNBTHelper.setString(stack, "SkullOwner", e.getCustomName().getString());
				Vec3 pos = e.position();
				event.getDrops().add(new ItemEntity(e.getCommandSenderWorld(), pos.x, pos.y, pos.z, stack));
			}
		}
	}
	
}
