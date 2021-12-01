package vazkii.quark.base.client.config.gui;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.client.config.gui.widget.CheckboxButton;
import vazkii.quark.base.client.config.gui.widget.ColorTextButton;
import vazkii.quark.base.client.config.gui.widget.IconButton;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.module.ModuleCategory;

public class QuarkConfigHomeScreen extends AbstractQScreen {

	public QuarkConfigHomeScreen(Screen parent) {
		super(parent);
	}

	@Override
	protected void init() {
		super.init();

		int pad = 10;
		int vpad = 23;
		int bWidth = 150;
		int left = width / 2 - (bWidth + pad);
		int vStart = 60; 

		int i = 0;
		for(ModuleCategory category : ModuleCategory.values()) {
			int x = left + (bWidth + pad) * (i % 2);
			int y = vStart + (i / 2) * vpad;

			IConfigCategory configCategory = IngameConfigHandler.INSTANCE.getConfigCategory(category);
			Component comp = componentFor(configCategory);
			
			Button icon = new IconButton(x, y, bWidth - 20, 20, comp, new ItemStack(category.item), categoryLink(configCategory));
			Button checkbox = new CheckboxButton(x + bWidth - 20, y, IngameConfigHandler.INSTANCE.getCategoryEnabledObject(category)); 

			addRenderableWidget(icon);
			addRenderableWidget(checkbox);
			
			if(category.requiredMod != null && !ModList.get().isLoaded(category.requiredMod)) {
				icon.active = false;
				checkbox.active = false;
			}
			
			i++;
		}

		boolean addExternal = ExternalConfigHandler.instance.hasAny();
		int count = addExternal ? 3 : 2;
		int pads = 0;
		
		pad = 3;
		vpad = 23;
		bWidth = (366 / count);
		left = (width - (bWidth + pad) * count) / 2;
		vStart = height - 30;

		IConfigCategory cat = IngameConfigHandler.INSTANCE.getConfigCategory(null);
		addRenderableWidget(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, componentFor(cat), categoryLink(cat)));
		pads++;
		
		if(addExternal) {
			cat = ExternalConfigHandler.instance.mockCategory;
			addRenderableWidget(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, componentFor(cat), categoryLink(cat)));
			pads++;
		}
		
		addRenderableWidget(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, new TranslatableComponent("quark.gui.config.save"), this::commit));

		bWidth = 71;
		left = (width - (bWidth + pad) * 5) / 2;

		addRenderableWidget(new ColorTextButton(left, vStart - vpad, bWidth, 20, new TranslatableComponent("quark.gui.config.social.website"), 0x48ddbc, webLink("https://quark.vazkii.net")));
		addRenderableWidget(new ColorTextButton(left + bWidth + pad, vStart - vpad, bWidth, 20, new TranslatableComponent("quark.gui.config.social.discord"), 0x7289da, webLink("https://vazkii.net/discord")));
		addRenderableWidget(new ColorTextButton(left + (bWidth + pad) * 2, vStart - vpad, bWidth, 20, new TranslatableComponent("quark.gui.config.social.patreon"), 0xf96854, webLink("https://patreon.com/vazkii")));
		addRenderableWidget(new ColorTextButton(left + (bWidth + pad) * 3, vStart - vpad, bWidth, 20, new TranslatableComponent("quark.gui.config.social.reddit"), 0xff4400, webLink("https://reddit.com/r/quarkmod")));
		addRenderableWidget(new ColorTextButton(left + (bWidth + pad) * 4, vStart - vpad, bWidth, 20, new TranslatableComponent("quark.gui.config.social.twitter"), 0x1da1f2, webLink("https://twitter.com/VazkiiMods")));
	}
	
	private static Component componentFor(IConfigCategory c) {
		TranslatableComponent comp = new TranslatableComponent("quark.category." + c.getName());

		if(c.isDirty())
			comp.append(new TextComponent("*").withStyle(ChatFormatting.GOLD));
		
		return comp;
	}
	
	public void commit(Button button) {
		IngameConfigHandler.INSTANCE.commit();
		returnToParent(button);
	}

	@Override
	public void render(PoseStack mstack, int mouseX, int mouseY, float pticks) {
		renderBackground(mstack);
		super.render(mstack, mouseX, mouseY, pticks);

		drawCenteredString(mstack, font, ChatFormatting.BOLD + I18n.get("quark.gui.config.header", WordUtils.capitalizeFully(Quark.MOD_ID)), width / 2, 15, 0x48ddbc);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader1", ChatFormatting.GOLD, ContributorRewardHandler.featuredPatron, ChatFormatting.RESET), width / 2, 28, 0xf96854);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader2"), width / 2, 38, 0xf96854);
	}

}
