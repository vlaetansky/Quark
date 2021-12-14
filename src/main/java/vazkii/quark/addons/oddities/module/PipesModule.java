package vazkii.quark.addons.oddities.module;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ForgeModelBakery;
import vazkii.arl.util.RegistryHelper;
import vazkii.quark.addons.oddities.block.PipeBlock;
import vazkii.quark.addons.oddities.block.be.PipeTileEntity;
import vazkii.quark.addons.oddities.client.render.PipeTileEntityRenderer;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.LoadModule;
import vazkii.quark.base.module.ModuleCategory;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.Config;

@LoadModule(category = ModuleCategory.ODDITIES)
public class PipesModule extends QuarkModule {

    public static BlockEntityType<PipeTileEntity> tileEntityType;

	@Config(description = "How long it takes for an item to cross a pipe. Bigger = slower.") 
	private static int pipeSpeed = 5;
	
	@Config(description = "Set to 0 if you don't want pipes to have a max amount of items")
	public static int maxPipeItems = 16;
	
	@Config(description = "When items eject or are absorbed by pipes, should they make sounds?")
	public static boolean doPipesWhoosh = true;
    
	public static Block pipe;
	
	public static int effectivePipeSpeed;
	
    @Override
    public void construct() {
    	pipe = new PipeBlock(this);
    	
    	tileEntityType = BlockEntityType.Builder.of(PipeTileEntity::new, pipe).build(null);
		RegistryHelper.register(tileEntityType, "pipe");
    }
    
    @Override
    public void configChanged() {
    	effectivePipeSpeed = pipeSpeed * 2;
    }

	@Override
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		BlockEntityRenderers.register(tileEntityType, PipeTileEntityRenderer::new);
	}

    @Override
    @OnlyIn(Dist.CLIENT)
    public void modelRegistry() {
    	ForgeModelBakery.addSpecialModel(new ModelResourceLocation(new ResourceLocation(Quark.MOD_ID, "pipe_flare"), "inventory"));
    }
	
}
