package vazkii.quark.base.client.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.common.base.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import vazkii.quark.base.Quark;

@EventBusSubscriber(modid = Quark.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class ModelHandler {

	private static Map<ModelLayerLocation, Layer> layers = new HashMap<>();

	public static ModelLayerLocation addModel(String name, Supplier<LayerDefinition> supplier, Function<ModelPart, EntityModel<?>> modelConstructor) {
		ModelLayerLocation loc = new ModelLayerLocation(new ResourceLocation(Quark.MOD_ID, name), "main");
		layers.put(loc, new Layer(supplier, modelConstructor));
		return loc;
	}

	@SubscribeEvent
	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for(ModelLayerLocation location : layers.keySet())
			event.registerLayerDefinition(location, layers.get(location).definition);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Mob, M extends EntityModel<T>> M model(ModelLayerLocation location) {
		Layer layer = layers.get(location);
		Minecraft mc = Minecraft.getInstance();
		
		return (M) layer.modelConstructor.apply(mc.getEntityModels().bakeLayer(location));
	}

	private static class Layer {
		final Supplier<LayerDefinition> definition;
		final Function<ModelPart, EntityModel<?>> modelConstructor;
		
		public Layer(Supplier<LayerDefinition> definition, Function<ModelPart, EntityModel<?>> modelConstructor) {
			this.definition = definition;
			this.modelConstructor = modelConstructor;
		}
		
	}
	
}
