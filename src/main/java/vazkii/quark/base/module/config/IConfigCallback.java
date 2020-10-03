package vazkii.quark.base.module.config;

import java.util.function.Predicate;

public interface IConfigCallback {
	
	public abstract void push(String s, String comment);
	public abstract void pop();
	
	public abstract void addEntry(String name, Object default_, String comment, Predicate<Object> restriction);
	
	public static final class Dummy implements IConfigCallback {

		@Override
		public void push(String s, String comment) {
			// NO-OP
		}

		@Override
		public void pop() {
			// NO-OP			
		}

		@Override
		public void addEntry(String name, Object default_, String comment, Predicate<Object> restriction) {
			// NO-OP			
		}
		
	}
	
}
