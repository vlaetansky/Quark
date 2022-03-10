package vazkii.quark.base.client.config.screen.widgets;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.config.screen.CategoryScreen;
import vazkii.quark.base.client.config.screen.WidgetWrapper;

public interface IWidgetProvider {

	@OnlyIn(Dist.CLIENT)
	void addWidgets(CategoryScreen parent, List<WidgetWrapper> widgets);

	@OnlyIn(Dist.CLIENT)
	String getSubtitle();

}
