package vazkii.quark.content.client.tooltip;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.FormattedText;

/**
 * @author WireSegal
 * Created at 10:40 AM on 9/1/19.
 */
public class TooltipUtils {

    public static int shiftTextByLines(List<? extends FormattedText> lines, int y) {
        for(int i = 1; i < lines.size(); i++) {
            String s = lines.get(i).getString();
            s = ChatFormatting.stripFormatting(s);
            if(s != null && s.trim().isEmpty()) {
                y += 10 * (i - 1) + 1;
                break;
            }
        }
        return y;
    }
}
