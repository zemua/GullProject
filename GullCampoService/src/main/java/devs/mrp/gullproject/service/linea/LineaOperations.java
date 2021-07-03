package devs.mrp.gullproject.service.linea;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class LineaOperations {

	private final Linea linea;
	
	public LineaOperations(Linea linea) {
		this.linea = linea;
	}
	
	public Linea clonar() {
		return new Linea(this.linea);
	}
	
	public void resetCampos(List<Campo<Object>> campos) {
		linea.getCampos().clear();
		linea.getCampos().addAll(campos);
	}
	
	@JsonIgnore
	public int getCantidadCampos() {
		return linea.getCampos().size();
	}
	
	public Map<String, Campo<?>> getMapOfCamposByAtributoId() {
		return linea.getCampos().stream().collect(Collectors.toMap((c)->c.getAtributoId(), (c)->c));
	}
	
	public Campo<?> getCampoByIndex(int i){
		return linea.getCampos().get(i);
	}
	
	public Campo<?> getCampoByAttId(String attId) {
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(attId)) {
				return campo;
			}
		}
		return new Campo<String>(attId, "");
	}
	
	public String getValueByAttId(String attId) {
		Campo<?> c = getCampoByAttId(attId);
		if (c == null || c.getDatos() == null) {
			return "";
		} else {
			return String.valueOf(c.getDatos());
		}
	}
	
	public boolean replaceCampo(String atributoId, Campo<?> c) {
		boolean removed = false;
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
				removed = true;
				break;
			}
		}
		if (removed) { linea.getCampos().add(c); }
		return removed;
	}
	
	public boolean replaceCampo(Campo<?> c) {
		return replaceCampo(c.getAtributoId(), c);
	}
	
	public void replaceOrElseAddCampo(String atributoId, Campo<?> c) {
		if (!replaceCampo(atributoId, c)) {
			linea.getCampos().add(c);
		}
	}
	
	public void replaceOrElseAddCampos(List<Campo<?>> campos) {
		campos.stream().forEach(c -> replaceOrElseAddCampo(c.getAtributoId(), c));
	}
	
	public void replaceOrAddCamposObj(List<Campo<Object>> campos) {
		List<Campo<?>> cs = new ArrayList<>();
		cs.addAll(campos);
		replaceOrElseAddCampos(cs);
	}
	
	public void addCampo(Campo<?> c) {
		linea.getCampos().add(c);
	}
	
	public void removeCampoByAttId(String atributoId) {
		Iterator<Campo<?>> it = linea.getCampos().iterator();
		while (it.hasNext()) {
			Campo<?> campo = it.next();
			if (campo.getAtributoId().equals(atributoId)) {
				it.remove();
			}
		}
	}
	
	public void removeCamposByAttId(List<String> attIds) {
		attIds.stream().forEach(att -> removeCampoByAttId(att));
	}
	
	public boolean ifAssignedTo(String id) {
		Optional<String> assigned = linea.getCounterLineId().stream().filter(c -> c.equals(id)).findAny();
		return assigned.isPresent();
	}
	
	public boolean ifHasPvp(String pvpId) {
		Optional<PvperLinea> pvp = linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny();
		return pvp.isPresent();
	}
	
	public PvperLinea getPvp(String pvpId) {
		return linea.getPvps().stream().filter(p -> p.getPvperId().equals(pvpId)).findAny().orElse(null);
	}
	
	public Double getPvpValue(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getPvp();
	}
	
	public Double getPvpMargin(String pvpId) {
		var pvp = getPvp(pvpId);
		if (pvp == null) {
			return 0D;
		}
		return pvp.getMargen();
	}
	
	public void removePvpById(String pvpId) {
		Iterator<PvperLinea> it = linea.getPvps().iterator();
		while (it.hasNext()) {
			PvperLinea linea = it.next();
			if (linea.getPvperId().equals(pvpId)) {
				it.remove();
			}
		}
	}
	
	public CosteLineaProveedor getCosteByCosteId(String costeId) {
		if (linea.getCostesProveedor() == null) {
			return new CosteLineaProveedor(costeId);
		}
		Optional<CosteLineaProveedor> cos = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costeId)).findFirst();
		return cos.orElse(new CosteLineaProveedor(costeId));
	}
	
	public boolean ifHasCost(String costId) {
		Optional<CosteLineaProveedor> coste = linea.getCostesProveedor().stream().filter(c -> c.getCosteProveedorId().equals(costId)).findAny();
		return coste.isPresent();
	}
	
	public void removeCosteById(String costId) {
		Iterator<CosteLineaProveedor> it = linea.getCostesProveedor().iterator();
		while (it.hasNext()) {
			var cos = it.next();
			if (cos.getCosteProveedorId().equals(costId)) {
				it.remove();
			}
		}
	}
	
}
