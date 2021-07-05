package devs.mrp.gullproject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByAssignedLineFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpMapperByCounterLineFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpMarginMapperByAssignedLineFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpMarginMapperByCounterIdFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpSumByAssignedLineIdFinderFactory;
import devs.mrp.gullproject.service.linea.oferta.PvpSumByCounterIdFactory;
import devs.mrp.gullproject.service.linea.proveedor.SupplierLineMapperByPropAndAssignedLineFactory;
import devs.mrp.gullproject.service.linea.proveedor.SupplierLineMapperByProposalAndCounterLineFactory;

@Configuration
public class LineaMappersBeanConfiguration {

	@Bean
	public PvpSumByCounterIdFactory getSumMapperFactory() {
		return new PvpSumByAssignedLineIdFinderFactory();
	}
	
	@Bean
	public PvpMarginMapperByCounterIdFactory getMarginMapperFactory() {
		return new PvpMarginMapperByAssignedLineFactory();
	}
	
	@Bean
	public PvpMapperByCounterLineFactory<Linea> getPvpMapperFactory() {
		return new PvpMapperByAssignedLineFactory<>();
	}
	
	@Bean
	public SupplierLineMapperByPropAndAssignedLineFactory getSupplierLineMapperFactory() {
		return new SupplierLineMapperByProposalAndCounterLineFactory();
	}
	
}
