package devs.mrp.gullproject.domainsdto.propuesta.oferta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.Pvper.IdAttsList;
import devs.mrp.gullproject.domainsdto.propuesta.oferta.PvperCheckboxedCosts.CheckboxedAttId;

@Service
public class PvperCheckboxedCostToPvperImpl implements PvperCheckboxedCostToPvper { // TODO test

	@Override
	public Pvper from(PvperCheckboxedCosts dto) {
		Pvper pvp = new Pvper();
		
		pvp.setId(dto.getId());
		pvp.setName(dto.getName());
		pvp.setIdCostes(getCostes(dto));
		pvp.setIdAttributesByCotiz(getAttributes(dto));
		
		return pvp;
	}
	
	private List<IdAttsList> getAttributes(PvperCheckboxedCosts dto) {
		List<IdAttsList> idAttributesByCotiz = new ArrayList<>();
		dto.getAttributesByCotiz().forEach(cotiz -> {
			Pvper.IdAttsList attList = new Pvper.IdAttsList();
			List<String> atts = cotiz.getAtts().stream().filter(a -> a.isSelected()).map(a -> a.getId()).collect(Collectors.toList());
			attList.setIds(atts);
			attList.setCotizId(cotiz.getCotizId());
			idAttributesByCotiz.add(attList);
		});
		return idAttributesByCotiz;
	}
	
	private List<String> getCostes(PvperCheckboxedCosts dto) {
		return dto.getCosts().stream().filter(c -> c.isSelected()).map(c -> c.getId()).collect(Collectors.toList());
	}

}
