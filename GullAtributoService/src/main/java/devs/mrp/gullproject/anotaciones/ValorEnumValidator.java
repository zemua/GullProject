package devs.mrp.gullproject.anotaciones;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValorEnumValidator implements ConstraintValidator<ValorEnum, CharSequence> {
	
	// TODO test

	private List<String> acceptedValues;
	
	@Override
	public void initialize(ValorEnum constraintAnnotation) {
		acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
				.map(Enum::name)
				.collect(Collectors.toList());
	}
	
	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString());
	}

}
