package vazkii.quark.addons.oddities.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.client.render.BaseArmorModel;

@OnlyIn(Dist.CLIENT)
public class BackpackModel extends BaseArmorModel {

	private final ModelPart straps;
	private final ModelPart backpack;
	private final ModelPart fitting;

	private final ModelPart base;

	public BackpackModel() {
		super(EquipmentSlot.CHEST);
		base = new ModelPart(this);

		straps = new ModelPart(this, 24, 0);
		straps.setPos(0.0F, 0.0F, 0.0F);
		straps.addBox(-4.0F, 0.05F, -3.0F, 8, 8, 5, 0.0F);
		fitting = new ModelPart(this, 50, 0);
		fitting.setPos(0.0F, 0.0F, 0.0F);
		fitting.addBox(-1.0F, 3.0F, 6.0F, 2, 3, 1, 0.0F);
		backpack = new ModelPart(this, 0, 0);
		backpack.setPos(0.0F, 0.0F, 0.0F);
		backpack.addBox(-4.0F, 0.0F, 2.0F, 8, 10, 4, 0.0F);

		base.addChild(straps);
		base.addChild(backpack);
		base.addChild(fitting);
		
		body = base;
	}
	
}
