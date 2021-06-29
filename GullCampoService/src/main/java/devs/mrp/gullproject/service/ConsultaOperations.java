package devs.mrp.gullproject.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.CosteProveedor;
import devs.mrp.gullproject.domains.Propuesta;
import devs.mrp.gullproject.domains.PropuestaProveedor;
import devs.mrp.gullproject.domains.TipoPropuesta;

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
			costes.addAll(((PropuestaProveedor)p).getCostes());
		});
		return costes;
	}
	
	public Map<String, CosteProveedor> mapIdToCosteProveedor() {
		return getCostesOfPropuestasProveedor().stream().collect(Collectors.toMap(CosteProveedor::getId, Function.identity()));
	}
	
	public List<Propuesta> getAllPropuestasAssignedToId(String idAssignedTo) {
		return consulta.getPropuestas().stream().filter(p -> p.getForProposalId().equals(idAssignedTo)).collect(Collectors.toList());
	}
	
}
