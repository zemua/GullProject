package devs.mrp.gullproject.domainsdto.linea.selectable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;
import devs.mrp.gullproject.service.linea.oferta.selectable.SelectableLineMapperByCounterAndPvp;
import devs.mrp.gullproject.service.linea.oferta.selectable.SelectableLineMapperByCounterAndPvpFactory;

@Service
public class SelectableLinesWrapBuilderImpl implements SelectableLinesWrapBuilder {

	@Autowired SelectableLineMapperByCounterAndPvpFactory mapperFactory;
	@Autowired SelectableLinesWrapFactory wrapFactory;
	@Autowired SelectableLineFactory lineFactory;
	
	@Override
	public SelectableLinesWrap from(List<Linea> lineasCliente, List<SelectableAbstractLine> lineasOferta, PropuestaNuestra propuestaNuestra) {
		if (lineasOferta == null) {return wrapFactory.withNumberOfLines(lineasCliente.size());}
		
		SelectableLineMapperByCounterAndPvp mapper;
		SelectableLinesWrap wrap = wrapFactory.create();
		mapper = mapperFactory.from(lineasOferta);
		
		AtomicInteger order = new AtomicInteger(0);
		
		propuestaNuestra.getPvps()
		.forEach(sPvp -> {
			lineasCliente
			.forEach(sLineaCliente -> {
				SelectableAbstractLine linea = mapper.findBy(sLineaCliente.getId(), sPvp.getId());
				if (linea == null) {
					linea = lineFactory.create();
					linea.setSelected(false);
					linea.setCounterLineId(sLineaCliente.getId());
					linea.setPvp(new PvperLinea());
					linea.getPvp().setPvperId(sPvp.getId());
					linea.getPvp().setMargen(0);
					linea.getPvp().setPvp(0);
					linea.setNombre("");
					linea.setQty(sLineaCliente.getQty());
				} else {
					linea.setSelected(true);
				}
				linea.setOrder(order.getAndIncrement());
				wrap.getLineas().add(linea);
			});
		});
		
		return wrap;
	}

}
