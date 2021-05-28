package devs.mrp.gullproject.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.domains.dto.AtributoForLineaFormDto;
import devs.mrp.gullproject.domains.dto.ParTipoValor;
import devs.mrp.gullproject.service.AtributoServiceProxyWebClient;

public class AttributeValueValidator implements ConstraintValidator<ValueMatchesTipoConstraint, ParTipoValor> {

	@Autowired
	AtributoServiceProxyWebClient atributoService;
	
	@Override
	public void initialize(ValueMatchesTipoConstraint valueMatchesTipoConstraint) {
		
	}
	
	@Override
	public boolean isValid(ParTipoValor value, ConstraintValidatorContext context) {
		return atributoService.validateDataFormat(value.getTipo(), value.getValue()).block(); // Cannot block... there is no alternative, has to be done "manually"
	}

}
