package devs.mrp.gullproject.validator.linea;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import devs.mrp.gullproject.domains.linea.Linea;

public class LineaValidator implements ConstraintValidator<LineaConstraint, Linea> {

	@Override
	public boolean isValid(Linea value, ConstraintValidatorContext context) {
		if (value.getId().isBlank()) {return false;}
		if (value.getNombre().isBlank()) {return false;}
		if (value.getPropuestaId().isBlank()) {return false;}
		return true;
	}

}
