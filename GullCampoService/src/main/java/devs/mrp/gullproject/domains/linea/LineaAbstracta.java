package devs.mrp.gullproject.domains.linea;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Document (collection = "lineasoferta")
@NoArgsConstructor
public abstract class LineaAbstracta {

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
	private String counterLineId;
	
	private Integer order;
	private Integer qty;
	
	/**
	 * Then the proposal will have an ArrayList of attributes, that can have repeated ones, for example 2 dimensions for inlet and outlet
	 * Finally the line will have attribute fields, filled in a table, and controlled by javascript the type of data (and server side too)
	 * if the proposal has an attribute that the line doesn't, then it shows blank
	 * if the line has an attribute that the proposal doesn't, then it doesn't show
	 */
	private List<Campo<?>> campos = new ArrayList<>();
	
	private PvperLinea pvp;
	
	public LineaAbstracta(LineaAbstracta lin) {
		this.counterLineId = lin.counterLineId;
		this.nombre = lin.nombre;
		if (lin.order != null) {this.order = lin.order.intValue();}
		this.parentId = lin.parentId;
		this.propuestaId = lin.propuestaId;
		lin.getCampos().stream().forEach(c -> this.campos.add(new Campo<>(c)));
		this.pvp = lin.pvp;
	}
	
}
