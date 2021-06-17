package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class CostRemapperUtilities {

	LineaService lineaService;
	
	@Autowired
	CostRemapperUtilities(LineaService lineaService) {
		this.lineaService = lineaService;
	}
	
	
	
}
