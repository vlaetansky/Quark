package vazkii.quark.content.client.render.variant;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import vazkii.quark.base.Quark;
import vazkii.quark.content.client.module.VariantAnimalTexturesModule;

public class VariantBeeRenderer extends BeeRenderer {

	public static IRenderFactory<Bee> OLD_BEE_RENDER_FACTORY = null;
	private EntityRenderer<? super Bee> OLD_BEE_RENDER = null;
	private static final List<String> VARIANTS = ImmutableList.of(
			"acebee", "agenbee", "arobee", "beefluid", "beesexual", 
			"beequeer", "enbee", "gaybee", "interbee", "lesbeean", 
			"panbee", "polysexbee", "transbee", "helen");
	
	public VariantBeeRenderer(EntityRenderDispatcher renderManagerIn) {
		super(renderManagerIn);
		if(OLD_BEE_RENDER_FACTORY != null) {
			OLD_BEE_RENDER = OLD_BEE_RENDER_FACTORY.createRenderFor(renderManagerIn);
		}
	}
	
	@Override
	public ResourceLocation getTextureLocation(Bee entity) {
		UUID id = entity.getUUID();
		long most = id.getMostSignificantBits();
		
		// From https://news.gallup.com/poll/329708/lgbt-identification-rises-latest-estimate.aspx
		final double lgbtChance = 0.056;
		boolean lgbt = VariantAnimalTexturesModule.everyBeeIsLGBT ||  (new Random(most)).nextDouble() < lgbtChance;
		
		if(entity.hasCustomName() || lgbt) {
			String custName = entity.hasCustomName() ? entity.getCustomName().getString().trim() : "";
			String name = custName.toLowerCase(Locale.ROOT);
			
			if(!VARIANTS.contains(name)) {
				if(custName.matches("wire(se|bee)gal"))
					name = "enbee";
				else if(lgbt)
					name = VARIANTS.get(Math.abs((int) (most % (VARIANTS.size() - 1)))); // -1 to not spawn helen bee naturally
			}
			
			if(VARIANTS.contains(name)) {
				String type = "normal";
				boolean angery = entity.hasStung();
				boolean nectar = entity.hasNectar();
				
				if(angery)
					type = nectar ? "angry_nectar" : "angry";
				else if(nectar)
					type = "nectar";
				
				String path = String.format("textures/model/entity/variants/bees/%s/%s.png", name, type);
				return new ResourceLocation(Quark.MOD_ID, path);
			}
		}

		if(OLD_BEE_RENDER != null) {
			return OLD_BEE_RENDER.getTextureLocation(entity);
		}
		
		return super.getTextureLocation(entity);
	}

}
