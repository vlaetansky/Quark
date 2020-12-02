package vazkii.quark.tools.module;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;
import vazkii.quark.tools.capability.SeedPouchDropIn;
import vazkii.quark.tools.item.SeedPouchItem;

@LoadModule(category = ModuleCategory.TOOLS, hasSubscriptions = true)
public class SeedPouchModule extends QuarkModule {

    private static final ResourceLocation SEED_POUCH_CAP = new ResourceLocation(Quark.MOD_ID, "seed_pouch_drop_in");
	
	public static Item seed_pouch;
	
	@Config public static int maxItems = 640;
	
	@Override
	public void construct() {
		seed_pouch = new SeedPouchItem(this);
	}
	
    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if(event.getObject().getItem() == seed_pouch)
            event.addCapability(SEED_POUCH_CAP, new SeedPouchDropIn());
    }
    
    @SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void makeTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == seed_pouch) {
			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
			if(contents != null) {	
				List<ITextComponent> tooltip = event.getToolTip();
				
				int stacks = Math.max(1, (contents.getRight() - 1) / contents.getLeft().getMaxStackSize() + 1);
				int len = 16 + stacks * 8;
				
				String s = "";
				Minecraft mc = Minecraft.getInstance();
				while(mc.fontRenderer.getStringWidth(s) < len)
					s += " ";
				
				tooltip.add(1, new StringTextComponent(s));
				tooltip.add(1, new StringTextComponent(s));
			}
				
		}
	}
	
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public void renderTooltip(RenderTooltipEvent.PostText event) {
		ItemStack stack = event.getStack();
		if(stack.getItem() == seed_pouch) {
			Pair<ItemStack, Integer> contents = SeedPouchItem.getContents(stack);
			if(contents != null) {			
				ItemStack seed = contents.getLeft().copy();

				Minecraft mc = Minecraft.getInstance();
				ItemRenderer render = mc.getItemRenderer();
				
				int x = event.getX();
				int y = event.getY();
				
				int count = contents.getRight();
				int stacks = Math.max(1, (count - 1) / seed.getMaxStackSize() + 1);
				
				GlStateManager.pushMatrix();
				GlStateManager.translated(x, y + 12, 500);
				for(int i = 0; i < stacks; i++) {
					if(i == (stacks - 1))
						seed.setCount(count);
					
					GlStateManager.pushMatrix();
					GlStateManager.translated(8 * i, Math.sin(i * 498543) * 2, 0);

					render.renderItemAndEffectIntoGUI(seed, 0, 0);
					render.renderItemOverlays(mc.fontRenderer, seed, 0, 0);
					GlStateManager.popMatrix();
				}
				GlStateManager.popMatrix();
			}
		}
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
	public void onRenderHUD(RenderGameOverlayEvent.Pre event) {
    	if(event.getType() == ElementType.POTION_ICONS) {
    		
    	}
    }
	
}
