package vazkii.quark.base.client.render;

import java.util.Calendar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;

// A copy of ChestTileEntityRenderer from vanilla but less private
public abstract class GenericChestTERenderer<T extends BlockEntity & LidBlockEntity> extends BlockEntityRenderer<T> {
	
	public final ModelPart lid;
	public final ModelPart bottom;
	public final ModelPart lock;
	public final ModelPart doubleLeftLid;
	public final ModelPart doubleLeftBottom;
	public final ModelPart doubleLeftLock;
	public final ModelPart doubleRightLid;
	public final ModelPart doubleRightBottom;
	public final ModelPart doubleRightLock;
	public boolean isChristmas;

	public GenericChestTERenderer(BlockEntityRenderDispatcher p_i226008_1_) {
		super(p_i226008_1_);
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}

		this.bottom = new ModelPart(64, 64, 0, 19);
		this.bottom.addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F, 0.0F);
		this.lid = new ModelPart(64, 64, 0, 0);
		this.lid.addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F, 0.0F);
		this.lid.y = 9.0F;
		this.lid.z = 1.0F;
		this.lock = new ModelPart(64, 64, 0, 0);
		this.lock.addBox(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F, 0.0F);
		this.lock.y = 8.0F;
		this.doubleLeftBottom = new ModelPart(64, 64, 0, 19);
		this.doubleLeftBottom.addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.doubleLeftLid = new ModelPart(64, 64, 0, 0);
		this.doubleLeftLid.addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.doubleLeftLid.y = 9.0F;
		this.doubleLeftLid.z = 1.0F;
		this.doubleLeftLock = new ModelPart(64, 64, 0, 0);
		this.doubleLeftLock.addBox(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.doubleLeftLock.y = 8.0F;
		this.doubleRightBottom = new ModelPart(64, 64, 0, 19);
		this.doubleRightBottom.addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, 0.0F);
		this.doubleRightLid = new ModelPart(64, 64, 0, 0);
		this.doubleRightLid.addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, 0.0F);
		this.doubleRightLid.y = 9.0F;
		this.doubleRightLid.z = 1.0F;
		this.doubleRightLock = new ModelPart(64, 64, 0, 0);
		this.doubleRightLock.addBox(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F, 0.0F);
		this.doubleRightLock.y = 8.0F;
	}

	@Override
	public void render(T p_225616_1_, float p_225616_2_, PoseStack p_225616_3_, MultiBufferSource p_225616_4_, int p_225616_5_, int p_225616_6_) {
		Level world = p_225616_1_.getLevel();
		boolean flag = world != null;
		BlockState blockstate = flag ? p_225616_1_.getBlockState() : Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
		ChestType chesttype = blockstate.getValues().containsKey(ChestBlock.TYPE) ? blockstate.getValue(ChestBlock.TYPE) : ChestType.SINGLE;
		Block block = blockstate.getBlock();
		if (block instanceof AbstractChestBlock) {
			AbstractChestBlock<?> abstractchestblock = (AbstractChestBlock<?>) block;
			boolean flag1 = chesttype != ChestType.SINGLE;
			p_225616_3_.pushPose();
			float f = blockstate.getValue(ChestBlock.FACING).toYRot();
			p_225616_3_.translate(0.5D, 0.5D, 0.5D);
			p_225616_3_.mulPose(Vector3f.YP.rotationDegrees(-f));
			p_225616_3_.translate(-0.5D, -0.5D, -0.5D);
			DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> icallbackwrapper;
			if (flag) {
				icallbackwrapper = abstractchestblock.combine(blockstate, world, p_225616_1_.getBlockPos(), true);
			} else {
				icallbackwrapper = DoubleBlockCombiner.Combiner::acceptNone; // getFallback
			}
			
			// getAnimationProgressRetreiver
			float f1 = icallbackwrapper.apply(ChestBlock.opennessCombiner((LidBlockEntity)p_225616_1_)).get(p_225616_2_);
			f1 = 1.0F - f1;
			f1 = 1.0F - f1 * f1 * f1;
			int i = icallbackwrapper.apply(new BrightnessCombiner<>()).applyAsInt(p_225616_5_);
			Material material = getMaterialFinal(p_225616_1_, chesttype); // <- Changed here
			if(material != null) {
				VertexConsumer ivertexbuilder = material.buffer(p_225616_4_, RenderType::entityCutout);
				if (flag1) {
					if (chesttype == ChestType.LEFT) {
						this.render(p_225616_3_, ivertexbuilder, this.doubleRightLid, this.doubleRightLock, this.doubleRightBottom, f1, i, p_225616_6_);
					} else {
						this.render(p_225616_3_, ivertexbuilder, this.doubleLeftLid, this.doubleLeftLock, this.doubleLeftBottom, f1, i, p_225616_6_);
					}
				} else {
					this.render(p_225616_3_, ivertexbuilder, this.lid, this.lock, this.bottom, f1, i, p_225616_6_);
				}
			}

			p_225616_3_.popPose();
		}
	}
	
	public final Material getMaterialFinal(T t, ChestType type) {
		if(isChristmas)
			return Sheets.chooseMaterial(t, type, this.isChristmas);

		return getMaterial(t, type);
	}
	
	public abstract Material getMaterial(T t, ChestType type);

	public void render(PoseStack p_228871_1_, VertexConsumer p_228871_2_, ModelPart p_228871_3_, ModelPart p_228871_4_, ModelPart p_228871_5_, float p_228871_6_, int p_228871_7_, int p_228871_8_) {
		p_228871_3_.xRot = -(p_228871_6_ * ((float)Math.PI / 2F));
		p_228871_4_.xRot = p_228871_3_.xRot;
		p_228871_3_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
		p_228871_4_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
		p_228871_5_.render(p_228871_1_, p_228871_2_, p_228871_7_, p_228871_8_);
	}
}
