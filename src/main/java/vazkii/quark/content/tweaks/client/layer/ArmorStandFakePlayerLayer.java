package vazkii.quark.content.tweaks.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.decoration.ArmorStand;
import vazkii.quark.content.tweaks.module.UsesForCursesModule;

public class ArmorStandFakePlayerLayer<M extends EntityModel<ArmorStand>> extends RenderLayer<ArmorStand, M> {

	private final PlayerModel<?> playerModel;
	private final PlayerModel<?> playerModelSlim;
	
	public ArmorStandFakePlayerLayer(RenderLayerParent<ArmorStand, M> parent, EntityModelSet models) {
		super(parent);
		
		playerModel = new PlayerModel<>(models.bakeLayer(ModelLayers.PLAYER), false);
		playerModelSlim = new PlayerModel<>(models.bakeLayer(ModelLayers.PLAYER_SLIM), true);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int p_117351_, ArmorStand armor, float p_117353_, float p_117354_, float p_117355_, float p_117356_, float p_117357_, float p_117358_) {
		System.out.println("hi");
		
		if(!UsesForCursesModule.staticEnabled || !UsesForCursesModule.bindArmorStandsWithPlayerHeads)
			return;
		
		System.out.println("ho");
	}

}
