package vazkii.quark.base.client.screen;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.quark.base.client.config.ConfigCategory;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.module.ModuleCategory;

public class QHomeScreen extends QScreen {

	// TODO localize all the strings

	public QHomeScreen(Screen parent) {
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
		for(ModuleCategory category : ModuleCategory.values())
			if(category.showInGui) {
				int x = left + (bWidth + pad) * (i % 2);
				int y = vStart + (i / 2) * vpad;

				ConfigCategory configCategory = IngameConfigHandler.INSTANCE.getConfigCategory(category);
				String name = WordUtils.capitalizeFully(category.name);

				if(configCategory.isDirty())
					name += TextFormatting.GOLD + "*";

				addButton(new IconButton(x, y, bWidth - 20, 20, new TranslationTextComponent(name), new ItemStack(category.item), categoryLink(configCategory)));
				addButton(new CheckboxButton(x + bWidth - 20, y, IngameConfigHandler.INSTANCE.getCategoryEnabledObject(category)));
				i++;
			}

		pad = 3;
		vpad = 23;
		bWidth = 121;
		left = (width - (bWidth + pad) * 3) / 2;
		vStart = height - 30;

		addButton(new Button(left, vStart, bWidth, 20, new TranslationTextComponent("General Settings"), categoryLink(IngameConfigHandler.INSTANCE.getConfigCategory(null))));
		addButton(new Button(left + bWidth + pad, vStart, bWidth, 20, new TranslationTextComponent("Import Config"), this::returnToParent)); // TODO
		addButton(new Button(left + (bWidth + pad) * 2, vStart, bWidth, 20, new TranslationTextComponent("Save Changes"), this::commit));

		bWidth = 71;
		left = (width - (bWidth + pad) * 5) / 2;

		addButton(new ColorTextButton(left, vStart - vpad, bWidth, 20, new TranslationTextComponent("Quark Site"), 0x48ddbc, webLink("https://quark.vazkii.net")));
		addButton(new ColorTextButton(left + bWidth + pad, vStart - vpad, bWidth, 20, new TranslationTextComponent("Discord"), 0x7289da, webLink("https://vazkii.net/discord")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 2, vStart - vpad, bWidth, 20, new TranslationTextComponent("Patreon"), 0xf96854, webLink("https://patreon.com/vazkii")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 3, vStart - vpad, bWidth, 20, new TranslationTextComponent("Reddit"), 0xff4400, webLink("https://reddit.com/r/quarkmod")));
		addButton(new ColorTextButton(left + (bWidth + pad) * 4, vStart - vpad, bWidth, 20, new TranslationTextComponent("Twitter"), 0x1da1f2, webLink("https://twitter.com/VazkiiMods")));
	}
	
	public void commit(Button button) {
		IngameConfigHandler.INSTANCE.commit();
		returnToParent(button);
	}
	
	@Override
	public void render(MatrixStack mstack, int mouseX, int mouseY, float pticks) {
		renderBackground(mstack);
		super.render(mstack, mouseX, mouseY, pticks);

		drawCenteredString(mstack, font, TextFormatting.BOLD + "Quark Configuration", width / 2, 15, 0x48ddbc);
		drawCenteredString(mstack, font, String.format("Quark is possible thanks to the support of %s%s%s and others.", TextFormatting.GOLD, ContributorRewardHandler.featuredPatron, TextFormatting.RESET), width / 2, 28, 0xf96854);
		drawCenteredString(mstack, font, "Consider supporting us on Patreon for cool ingame rewards like this!", width / 2, 38, 0xf96854);
	}

}
