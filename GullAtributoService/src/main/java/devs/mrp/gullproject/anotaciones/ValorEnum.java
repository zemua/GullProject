package devs.mrp.gullproject.anotaciones;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValorEnumValidator.class)
public @interface ValorEnum {

	Class<? extends Enum<?>> enumClass();
    String message() default "Debe ser un valor de {enumClass}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
}
