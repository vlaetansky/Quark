package vazkii.quark.base.client.config.gui;

import java.util.function.Supplier;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import vazkii.quark.base.client.config.ConfigObject;

public class CheckboxButton extends Button {

	private final Supplier<Boolean> checkedSupplier;
	
	public CheckboxButton(int x, int y, Supplier<Boolean> checkedSupplier, IPressable onClick) {
		super(x, y, 20, 20, new StringTextComponent(""), onClick);
		this.checkedSupplier = checkedSupplier;
	}
	
	public CheckboxButton(int x, int y, ConfigObject<Boolean> configObj) {
		this(x, y, () -> configObj.getCurrentObj(), (b) -> configObj.setCurrentObj(!configObj.getCurrentObj()));
	}
	
	// TODO proper icons
	
	@Override
	public int getFGColor() {
		return checkedSupplier.get() ? 0x00FF00 : 0xFF0000;
	}
	
	@Override
	public ITextComponent getMessage() {
		return new StringTextComponent(checkedSupplier.get() ? "V" : "X");
	}

}
