package vazkii.quark.addons.oddities.module;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.TinyPotatoBlock;
import vazkii.quark.addons.oddities.block.be.TinyPotatoBlockEntity;
import vazkii.quark.addons.oddities.client.model.TinyPotatoModel;
import vazkii.quark.addons.oddities.client.render.be.TinyPotatoRenderer;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

import java.util.Map;

@LoadModule(category = ModuleCategory.ODDITIES, antiOverlap = "botania", hasSubscriptions = true, subscribeOn = Dist.CLIENT)
public class TinyPotatoModule extends QuarkModule {

	public static BlockEntityType<TinyPotatoBlockEntity> blockEntityType;

	public static Block tiny_potato;

	@Config(description = "Set this to true to use the recipe without the Heart of Diamond, even if the Heart of Diamond is enabled.", flag = "tiny_potato_never_uses_heart")
	public static boolean neverUseHeartOfDiamond = false;

	@Override
	public void register() {
		tiny_potato = new TinyPotatoBlock(this);

		blockEntityType = BlockEntityType.Builder.of(TinyPotatoBlockEntity::new, tiny_potato).build(null);
		RegistryHelper.register(blockEntityType, "tiny_potato");
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void modelBake(ModelBakeEvent event) {
		ResourceLocation tinyPotato = new ModelResourceLocation(new ResourceLocation("quark", "tiny_potato"), "inventory");
		Map<ResourceLocation, BakedModel> map = event.getModelRegistry();
		BakedModel originalPotato = map.get(tinyPotato);
		map.put(tinyPotato, new TinyPotatoModel(originalPotato));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void modelRegistry() {
		ForgeModelBakery bakery = ForgeModelBakery.instance();
		if (bakery != null) {
			ResourceManager rm = bakery.resourceManager;

			for (ResourceLocation model : rm.listResources("models/tiny_potato", s -> s.endsWith(".json"))) {
				if ("quark".equals(model.getNamespace())) {
					String path = model.getPath();
					path = path.substring("models/".length(), path.length() - ".json".length());
					ForgeModelBakery.addSpecialModel(new ResourceLocation("quark", path));
				}
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		BlockEntityRenderers.register(blockEntityType, TinyPotatoRenderer::new);
	}
}
