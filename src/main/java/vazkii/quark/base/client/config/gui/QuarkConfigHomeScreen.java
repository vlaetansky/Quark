package vazkii.quark.base.client.config.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ModList;
import vazkii.quark.api.config.IConfigCategory;
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
			String name = I18n.format("quark.category." + configCategory.getName());

			if(configCategory.isDirty())
				name += TextFormatting.GOLD + "*";
			
			Button icon = new IconButton(x, y, bWidth - 20, 20, new TranslationTextComponent(name), new ItemStack(category.item), categoryLink(configCategory));
			Button checkbox = new CheckboxButton(x + bWidth - 20, y, IngameConfigHandler.INSTANCE.getCategoryEnabledObject(category)); 

			addButton(icon);
			addButton(checkbox);
			
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

		addButton(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, new TranslationTextComponent("quark.gui.config.general"), categoryLink(IngameConfigHandler.INSTANCE.getConfigCategory(null))));
		pads++;
		
		if(addExternal) {
			addButton(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, new TranslationTextComponent("quark.gui.config.friends"), categoryLink(ExternalConfigHandler.instance.mockCategory)));
			pads++;
		}
		
		addButton(new Button(left + (bWidth + pad) * pads, vStart, bWidth, 20, new TranslationTextComponent("quark.gui.config.save"), this::commit));

		bWidth = 71;
		left = (width - (bWidth + pad) * 5) / 2;

		addButton(new ColorTextButton(left, vStart - vpad, bWidth, 20, new TranslationTextComponent("quark.gui.config.social.website"), 0x48ddbc, webLink("https://quark.vazkii.net")));
		addButton(new ColorTextButton(left + bWidth + pad, vStart - vpad, bWidth, 20, new TranslationTextComponent("quark.gui.config.social.discord"), 0x7289da, webLink("https://vazkii.net/discord")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 2, vStart - vpad, bWidth, 20, new TranslationTextComponent("quark.gui.config.social.patreon"), 0xf96854, webLink("https://patreon.com/vazkii")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 3, vStart - vpad, bWidth, 20, new TranslationTextComponent("quark.gui.config.social.reddit"), 0xff4400, webLink("https://reddit.com/r/quarkmod")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 4, vStart - vpad, bWidth, 20, new TranslationTextComponent("quark.gui.config.social.twitter"), 0x1da1f2, webLink("https://twitter.com/VazkiiMods")));
	}
	
	public void commit(Button button) {
		IngameConfigHandler.INSTANCE.commit();
		returnToParent(button);
	}

	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		renderBackground(mstack);
		super.render(mstack, mouseX, mouseY, pticks);

		drawCenteredString(mstack, font, TextFormatting.BOLD + I18n.format("quark.gui.config.header"), width / 2, 15, 0x48ddbc);
		drawCenteredString(mstack, font, I18n.format("quark.gui.config.subheader1", TextFormatting.GOLD, ContributorRewardHandler.featuredPatron, TextFormatting.RESET), width / 2, 28, 0xf96854);
		drawCenteredString(mstack, font, I18n.format("quark.gui.config.subheader2"), width / 2, 38, 0xf96854);
	}

}
