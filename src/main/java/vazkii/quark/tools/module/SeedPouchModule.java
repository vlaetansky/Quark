package vazkii.quark.tools.module;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.handler.SimilarBlockTypeHandler;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.management.capability.ShulkerBoxDropIn;
import vazkii.quark.tools.capability.SeedPouchDropIn;
import vazkii.quark.tools.item.SeedPouchItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class SeedPouchModule extends QuarkModule {

    private static final ResourceLocation SEED_POUCH_CAP = new ResourceLocation(Quark.MOD_ID, "seed_pouch_drop_in");
	
	public static Item seed_pouch;
	
	@Override
	public void construct() {
		seed_pouch = new SeedPouchItem(this);
	}
	
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == seed_pouch)
            event.addCapability(SEED_POUCH_CAP, new SeedPouchDropIn());
    }
	
}
