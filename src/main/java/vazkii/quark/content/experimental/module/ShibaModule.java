package vazkii.quark.content.experimental.module;

import com.google.common.base.Predicates;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.world.EntitySpawnHandler;
import vazkii.quark.content.experimental.shiba.client.render.ShibaRenderer;
import vazkii.quark.content.experimental.shiba.entity.ShibaEntity;

@LoadModule(category = ModuleCategory.EXPERIMENTAL, enabledByDefault = false)
public class ShibaModule extends QuarkModule {

	public static EntityType<ShibaEntity> shibaType;
	
	@Override
	public void construct() {
		super.construct();
		
		shibaType = EntityType.Builder.create(ShibaEntity::new, EntityClassification.CREATURE)
				.size(0.8F, 0.8F)
				.trackingRange(8)
				.setCustomClientFactory((spawnEntity, world) -> new ShibaEntity(shibaType, world))
				.build("shiba");
		RegistryHelper.register(shibaType, "shiba");
		
		EntitySpawnHandler.addEgg(shibaType, 0xa86741, 0xe8d5b6, this, () -> true);
	}
	
	@Override
	public void setup() {
		GlobalEntityTypeAttributes.put(shibaType, WolfEntity.func_234233_eS_().create());
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		RenderingRegistry.registerEntityRenderingHandler(shibaType, ShibaRenderer::new);
	}
	
}
