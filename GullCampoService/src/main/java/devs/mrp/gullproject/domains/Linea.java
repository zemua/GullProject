package devs.mrp.gullproject.domains;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import devs.mrp.gullproject.service.LineaOperations;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Document (collection = "lineas")
@NoArgsConstructor
public class Linea { // TODO aplicar Liskov Principle

	@Id
	private String id = new ObjectId().toString();
	
	@NotBlank(message = "Selecciona un nombre")
	private String nombre;
	
	/**
	 * This is the id of the propuesta to which this one belongs
	 */
	@NotBlank
	private String propuestaId;
	
	/**
	 * When creating an updated snapshot, this will refer to the "old" line
	 */
	private String parentId;
	/**
	 * When creating an offer of suppliers or ours, this will refer to the customer line
	 */
	private List<String> counterLineId;
	
	private Integer order;
	
	/**
	 * Then the proposal will have an ArrayList of attributes, that can have repeated ones, for example 2 dimensions for inlet and outlet
	 * Finally the line will have attribute fields, filled in a table, and controlled by javascript the type of data (and server side too)
	 * if the proposal has an attribute that the line doesn't, then it shows blank
	 * if the line has an attribute that the proposal doesn't, then it doesn't show
	 */
	private List<Campo<?>> campos = new ArrayList<>();
	
	/**
	 * For specific uses, not required fields
	 */
	
	private List<CosteLineaProveedor> costesProveedor;
	private List<PvperLinea> pvps;
	private List<String> pvpSums;
	
	/**
	 * Methods and constructors
	 * @param l
	 * @return
	 */
	
	public boolean equals(Linea l) {
		LineaOperations op = new LineaOperations(this);
		return op.equals(l);
	}
	
	public Linea(Linea lin) {
		this.counterLineId = lin.counterLineId;
		this.nombre = lin.nombre;
		if (lin.order != null) {this.order = lin.order.intValue();}
		this.parentId = lin.parentId;
		this.propuestaId = lin.propuestaId;
		lin.getCampos().stream().forEach(c -> this.campos.add(new Campo<>(c)));
		if (lin.getCostesProveedor() != null) {
			this.costesProveedor = new ArrayList<>();
			lin.getCostesProveedor().stream().forEach(c -> this.costesProveedor.add(new CosteLineaProveedor(c)));
		}
		if (lin.getPvpSums() != null) {
			this.pvpSums = new ArrayList<>();
			lin.getPvpSums().stream().forEach(s -> this.pvpSums.add(s));
		}
		if (lin.getPvps() != null) {
			this.pvps = new ArrayList<>();
			lin.getPvps().stream().forEach(p -> this.pvps.add(new PvperLinea(p)));
		}
	}
	
	public LineaOperations operations() {
		return new LineaOperations(this);
	}
	
}
