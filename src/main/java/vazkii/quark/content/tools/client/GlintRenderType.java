package vazkii.quark.content.tools.client;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.quark.base.Quark;
import vazkii.quark.content.tools.module.ColorRunesModule;

@OnlyIn(Dist.CLIENT)
public class GlintRenderType {
	
    public static List<RenderType> glintColor = newRenderList(GlintRenderType::buildGlintRenderType);
    public static List<RenderType> entityGlintColor = newRenderList(GlintRenderType::buildEntityGlintRenderType);
    public static List<RenderType> glintDirectColor = newRenderList(GlintRenderType::buildGlintDirectRenderType);
    public static List<RenderType> entityGlintDirectColor = newRenderList(GlintRenderType::buildEntityGlintDriectRenderType);
    
    public static List<RenderType> armorGlintColor = newRenderList(GlintRenderType::buildArmorGlintRenderType);
    public static List<RenderType> armorEntityGlintColor = newRenderList(GlintRenderType::buildArmorEntityGlintRenderType);

    public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
    	addGlintTypes(map, glintColor);
    	addGlintTypes(map, entityGlintColor);
    	addGlintTypes(map, glintDirectColor);
    	addGlintTypes(map, entityGlintDirectColor);
    	addGlintTypes(map, armorGlintColor);
    	addGlintTypes(map, armorEntityGlintColor);
    }
    
    private static List<RenderType> newRenderList(Function<String, RenderType> func) {
    	ArrayList<RenderType> list = new ArrayList<>(ColorRunesModule.RUNE_TYPES + 1);
    	
        for (DyeColor color : DyeColor.values())
        	list.add(func.apply(color.getName()));
        list.add(func.apply("rainbow"));
        list.add(func.apply("blank"));

        return list;
    }
    
    private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, List<RenderType> typeList) {
    	for(RenderType renderType : typeList)
    		if (!map.containsKey(renderType))
    			map.put(renderType, new BufferBuilder(renderType.bufferSize()));
    }

    private static RenderType buildGlintRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");

        return RenderType.create("glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
        	.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
            .createCompositeState(false));
    }

    private static RenderType buildEntityGlintRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");

        return RenderType.create("entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
        	.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .createCompositeState(false));
    }

 
    private static RenderType buildGlintDirectRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");

        return RenderType.create("glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.GLINT_TEXTURING)
            .createCompositeState(false));
    }

    
    private static RenderType buildEntityGlintDriectRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");

        return RenderType.create("entity_glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .createCompositeState(false));
    }
    
    private static RenderType buildArmorGlintRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");
        
        return RenderType.create("armor_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false));
    }
    
    private static RenderType buildArmorEntityGlintRenderType(String name) {
        final ResourceLocation res = new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");

        return RenderType.create("armor_entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false,RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(res, true, false))
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false));
    }
}
