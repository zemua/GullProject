package devs.mrp.gullproject.domains.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.hateoas.RepresentationModel;

import devs.mrp.gullproject.domains.propuestas.Propuesta;
import lombok.Data;

@Data
public class ConsultaRepresentationModel extends RepresentationModel<ConsultaRepresentationModel> {

	String id;
	String nombre;
	String status;
	Long createdTime;
	Long editedTime;
	List<Propuesta> propuestas;
	
}
