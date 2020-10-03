package vazkii.quark.base.module.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

	String name() default "";
	String description() default "";
	String flag() default "";

	@interface Restriction {
		String[] value();
	}

	@interface Min {
		double value();
		boolean exclusive() default false;
	}

	@interface Max {
		double value();
		boolean exclusive() default false;
	}
	
}
