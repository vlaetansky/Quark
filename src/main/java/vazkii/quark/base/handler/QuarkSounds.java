package vazkii.quark.base.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.GameData;
import vazkii.arl.util.RegistryHelper;

/**
 * @author WireSegal
 * Created at 12:40 PM on 9/9/19.
 */
public class QuarkSounds {
    private static final List<SoundEvent> REGISTRY_DEFERENCE = Lists.newArrayList();

    public static final SoundEvent ENTITY_STONELING_MEEP = register("entity.stoneling.meep");
    public static final SoundEvent ENTITY_STONELING_PURR = register("entity.stoneling.purr");
    public static final SoundEvent ENTITY_STONELING_GIVE = register("entity.stoneling.give");
    public static final SoundEvent ENTITY_STONELING_TAKE = register("entity.stoneling.take");
    public static final SoundEvent ENTITY_STONELING_EAT = register("entity.stoneling.eat");
    public static final SoundEvent ENTITY_STONELING_DIE = register("entity.stoneling.die");
    public static final SoundEvent ENTITY_STONELING_CRY = register("entity.stoneling.cry");
    public static final SoundEvent ENTITY_STONELING_MICHAEL = register("entity.stoneling.michael");
    
    public static final SoundEvent ENTITY_PICKARANG_THROW = register("entity.pickarang.throw");
    public static final SoundEvent ENTITY_PICKARANG_CLANK = register("entity.pickarang.clank");
    public static final SoundEvent ENTITY_PICKARANG_SPARK = register("entity.pickarang.spark");
    public static final SoundEvent ENTITY_PICKARANG_PICKUP = register("entity.pickarang.pickup");
    
    public static final SoundEvent ENTITY_FROG_WEDNESDAY = register("entity.frog.wednesday");
    public static final SoundEvent ENTITY_FROG_JUMP = register("entity.frog.jump");
    public static final SoundEvent ENTITY_FROG_DIE = register("entity.frog.die");
    public static final SoundEvent ENTITY_FROG_HURT = register("entity.frog.hurt");
    public static final SoundEvent ENTITY_FROG_IDLE = register("entity.frog.idle");
    
    public static final SoundEvent ENTITY_CRAB_DIE = register("entity.crab.die");
    public static final SoundEvent ENTITY_CRAB_HURT = register("entity.crab.hurt");
    public static final SoundEvent ENTITY_CRAB_IDLE = register("entity.crab.idle");
    
    public static final SoundEvent BLOCK_MONSTER_BOX_GROWL = register("block.monster_box.growl");
    
    public static final SoundEvent BLOCK_PIPE_SHOOT = register("block.pipe.shoot");
    public static final SoundEvent BLOCK_PIPE_PICKUP = register("block.pipe.pickup");
    public static final SoundEvent BLOCK_PIPE_SHOOT_LENNY = register("block.pipe.shoot.lenny");
    public static final SoundEvent BLOCK_PIPE_PICKUP_LENNY = register("block.pipe.pickup.lenny");

    public static final SoundEvent ITEM_CAMERA_SHUTTER = register("item.camera.shutter");
    public static final SoundEvent ITEM_SOUL_POWDER_SPAWN = register("item.soul_powder.spawn");

//    public static final SoundEvent MUSIC_TEDIUM = register("music.tedium");
    
    private static final String[] VOCALS = new String[] {"a", "ba", "be", "bi", "bo", "bu", "bya", "bye", "byo", "byu", "cha", "che", "chi", "cho", "chu", "da", "de", "di", "do", "dou", "dyu", "e", "fa", "fe", "fi", "fo", "fu", "ga", "ge", "gi", "gie", "go", "gu", "gya", "gyo", "gyu", "ha", "he", "hi", "ho", "hya", "hye", "hyo", "hyu", "i", "ie", "ja", "ji", "jie", "jo", "ju", "ka", "ke", "ki", "ko", "ku", "kya", "kye", "kyo", "kyu", "ma", "me", "mi", "mo", "mu", "mya", "mye", "myo", "myu", "n", "na", "ne", "ni", "no", "nu", "nya", "nye", "nyo", "nyu", "o", "pa", "pe", "pi", "po", "pu", "pya", "pye", "pyo", "pyu", "ra", "re", "ri", "ro", "ru", "rya", "rye", "ryo", "ryu", "sa", "se", "sha", "she", "shi", "sho", "shu", "si", "so", "su", "ta", "te", "thi", "to", "tsa", "tse", "tsi", "tso", "tsu", "tyu", "u", "va", "ve", "vi", "vo", "wa", "we", "who", "wi", "ya", "yo", "yu", "za", "ze", "zi", "zo", "zu" };
    public static Map<String, SoundEvent> VOCAL_EVENTS = new HashMap<>();
    
    static {
    	for(String s : VOCALS) {
    		SoundEvent ev = register("voice." + s);
    		VOCAL_EVENTS.put(s, ev);
    	}
    };
    
    public static void start() {
        for (SoundEvent event : REGISTRY_DEFERENCE)
            RegistryHelper.register(event);
        REGISTRY_DEFERENCE.clear();
    }

    public static SoundEvent register(String name) {
        ResourceLocation loc = GameData.checkPrefix(name, false);
        SoundEvent event = new SoundEvent(loc);
        event.setRegistryName(loc);
        REGISTRY_DEFERENCE.add(event);
        return event;
    }
}
