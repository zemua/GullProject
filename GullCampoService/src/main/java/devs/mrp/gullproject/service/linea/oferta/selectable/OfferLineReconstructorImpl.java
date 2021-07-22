package devs.mrp.gullproject.service.linea.oferta.selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrap;
import devs.mrp.gullproject.service.linea.oferta.concatenator.LineAttributeConcatenatorForPvp;

@Service
public class OfferLineReconstructorImpl implements OfferLineReconstructor {

	@Override
	public List<LineaAbstracta> from(SelectableLinesWrap wrap, LineAttributeConcatenatorForPvp concatenator, List<AtributoForCampo> atributos, String forPropuestaId) {
		List<LineaAbstracta> lineas = new ArrayList<>();
		AtomicInteger num = new AtomicInteger(0);
		wrap.getLineas().stream().filter(l -> l.getSelected()).forEach(lin -> {
			lin.setCampos(new ArrayList<>());
			lin.setPropuestaId(forPropuestaId);
			atributos.forEach(att -> {
				Campo<String> campo = new Campo<>();
				campo.setAtributoId(att.getId());
				campo.setDatos(concatenator.forLineAttPvp(lin.getCounterLineId(), att.getId(), lin.getPvp().getPvperId()));
				lin.getCampos().add(campo);
			});
			if (lin.getNombre().isEmpty()) {lin.setNombre(String.valueOf(num.getAndIncrement()));}
			lineas.add(lin);
		});
		return lineas;
	}

}
