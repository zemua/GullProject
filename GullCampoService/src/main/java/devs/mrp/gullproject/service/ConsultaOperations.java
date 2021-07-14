package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.Propuesta;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.TipoPropuesta;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsultaOperations {

	private final Consulta consulta;
	
	public ConsultaOperations(Consulta consulta) {
		this.consulta = consulta;
	}
	
	public void actualizaEditTime() {
		this.consulta.setEditedTime(System.currentTimeMillis());
	}
	
	public void addPropuesta(Propuesta propuesta) {
		this.consulta.getPropuestas().add(propuesta);
	}
	
	public void removePropuesta(Propuesta propuesta) {
		this.consulta.getPropuestas().remove(propuesta);
		actualizaEditTime();
	}
	
	public void removePropuesta(String propuestaId) {
		this.consulta.getPropuestas().removeIf(propuesta -> propuesta.getId().equals(propuestaId));
	}
	
	public int getCantidadPropuestas() {
		return this.consulta.getPropuestas().size();
	}
	
	public Propuesta getPropuestaByIndex(int index) {
		return this.consulta.getPropuestas().get(index);
	}
	
	public int getPropuestaIndexByPropuestaId(String id) {
		for(int i = 0; i<this.consulta.getPropuestas().size(); i++) {
			if (this.consulta.getPropuestas().get(i).getId().equals(id)) {
				return i;
			}
		}
		return -1;
	}
	
	public Propuesta getPropuestaById(String id) {
		Iterator<Propuesta> i = this.consulta.getPropuestas().iterator();
		while (i.hasNext()) {
			Propuesta p = i.next();
			if (p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}
	
	public List<Propuesta> getPropuestasProveedor() {
		return consulta.getPropuestas().stream().filter(p-> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR)).collect(Collectors.toList());
	}
	
	public List<Propuesta> getPropuestasProveedorAssignedTo(String idAssignedto) {
		return consulta.getPropuestas().stream().filter(p -> p.getTipoPropuesta().equals(TipoPropuesta.PROVEEDOR)).filter(p -> p.getForProposalId().equals(idAssignedto)).collect(Collectors.toList());
	}
	
	public List<CosteProveedor> getCostesOfPropuestasProveedor() {
		List<CosteProveedor> costes = new ArrayList<>();
		getPropuestasProveedor().stream().forEach(p -> {
			log.debug("adding costs of propuesta: " + p.toString());
			costes.addAll(((PropuestaProveedor)p).getCostes());
			log.debug("all costs are: " + costes.toString());
		});
		log.debug("returning costes: " + costes.toString());
		return costes;
	}
	
	public Map<String, CosteProveedor> mapIdToCosteProveedor() {
		return getCostesOfPropuestasProveedor().stream().collect(Collectors.toMap(CosteProveedor::getId, Function.identity()));
	}
	
	public List<Propuesta> getAllPropuestasAssignedToId(String idAssignedTo) {
		return consulta.getPropuestas().stream().filter(p -> p.getForProposalId() != null).filter(p -> p.getForProposalId().equals(idAssignedTo)).collect(Collectors.toList());
	}
	
}
