package devs.mrp.gullproject.service.linea.oferta.selectable;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import devs.mrp.gullproject.domains.Consulta;
import devs.mrp.gullproject.domains.linea.Campo;
import devs.mrp.gullproject.domains.linea.CosteLineaProveedor;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.linea.PvperLinea;
import devs.mrp.gullproject.domains.linea.abs.LineaAbstracta;
import devs.mrp.gullproject.domains.propuestas.AtributoForCampo;
import devs.mrp.gullproject.domains.propuestas.CosteProveedor;
import devs.mrp.gullproject.domains.propuestas.PropuestaProveedor;
import devs.mrp.gullproject.domains.propuestas.Pvper;
import devs.mrp.gullproject.domains.propuestas.Pvper.IdAttsList;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableAbstractLine;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLineFactory;
import devs.mrp.gullproject.domainsdto.linea.selectable.SelectableLinesWrapFactory;
import devs.mrp.gullproject.service.linea.oferta.concatenator.LineAttributeConcatenatorForPvpFactory;
import devs.mrp.gullproject.service.linea.proveedor.LineAttributeConcatenatorFactory;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class OfferLineReconstructorImplTest {

	@Autowired OfferLineReconstructor reconstructor;
	@Autowired LineAttributeConcatenatorForPvpFactory concatenatorFactory;
	@Autowired SelectableLineFactory selectableFactory;
	@Autowired SelectableLinesWrapFactory wrapFactory;
	
	@Test
	void test() {
		CosteProveedor cp1a = new CosteProveedor();
		cp1a.setName("coste 1a");
		CosteProveedor cp1b = new CosteProveedor();
		cp1b.setName("coste 1b");
		AtributoForCampo att1 = new AtributoForCampo();
		att1.setId("att1id");
		AtributoForCampo att2 = new AtributoForCampo();
		att2.setId("att2id");
		List<AtributoForCampo> atributos = new ArrayList<>() {{add(att1);add(att2);}};
		PropuestaProveedor pp = new PropuestaProveedor();
		pp.setCostes(new ArrayList<>() {{add(cp1a);add(cp1b);}});
		pp.setAttributeColumns(atributos);
		pp.setNombre("nombre propuesta proveedor");
		
		Pvper pvper1 = new Pvper();
		pvper1.setName("pvper 1 name");
		pvper1.setIdCostes(new ArrayList<>() {{add(cp1a.getId());}});
		IdAttsList atts1 = new IdAttsList();
		atts1.setCotizId(pp.getId());
		atts1.setIds(new ArrayList<>() {{add(att1.getId());}});
		pvper1.setIdAttributesByCotiz(new ArrayList<>() {{add(atts1);}});
		
		Pvper pvper2 = new Pvper();
		pvper2.setName("pvper 2 name");
		pvper2.setIdCostes(new ArrayList<>());
		IdAttsList atts2 = new IdAttsList();
		atts2.setCotizId(pp.getId());
		atts2.setIds(new ArrayList<>() {{add(att2.getId());}});
		pvper2.setIdAttributesByCotiz(new ArrayList<>() {{add(atts2);}});
		
		List<Pvper> pvps = new ArrayList<>() {{add(pvper1);add(pvper2);}};
		
		Consulta consulta = new Consulta();
		consulta.setNombre("consulta name");
		consulta.setPropuestas(new ArrayList<>() {{add(pp);}});
		
		SelectableAbstractLine l1 = selectableFactory.create();
		l1.setNombre("selectable line 1 name");
		l1.setCounterLineId("counter1");
		l1.setSelected(true);
		PvperLinea pvplinea1 = new PvperLinea();
		pvplinea1.setMargen(78);
		pvplinea1.setPvp(89);
		pvplinea1.setPvperId(pvper1.getId());
		l1.setPvp(pvplinea1);
		SelectableAbstractLine l2 = selectableFactory.create();
		l2.setNombre("selectable line 2 name");
		l2.setCounterLineId("counter2");
		l2.setSelected(true);
		PvperLinea pvplinea2 = new PvperLinea();
		pvplinea2.setMargen(45);
		pvplinea2.setPvp(56);
		pvplinea2.setPvperId(pvper2.getId());
		l2.setPvp(pvplinea2);
		SelectableAbstractLine l3 = selectableFactory.create();
		l3.setNombre("selectable line 3 name");
		l3.setCounterLineId("counter1");
		l3.setSelected(true);
		PvperLinea pvplinea3 = new PvperLinea();
		pvplinea3.setMargen(12);
		pvplinea3.setPvp(23);
		pvplinea3.setPvperId(pvper1.getId());
		l3.setPvp(pvplinea3);
		SelectableAbstractLine l4 = selectableFactory.create();
		l4.setNombre("selectable line 4 name");
		l4.setCounterLineId("counter2");
		l4.setSelected(false);
		PvperLinea pvplinea4 = new PvperLinea();
		pvplinea4.setMargen(98);
		pvplinea4.setPvp(87);
		pvplinea4.setPvperId(pvper2.getId());
		l4.setPvp(pvplinea4);
		
		var wrap = wrapFactory.create();
		wrap.setLineas(new ArrayList<>() {{add(l1);add(l2);add(l3);add(l4);}});
		
		Campo<String> c1a = new Campo<>();
		c1a.setAtributoId("att1id");
		c1a.setDatos("datos c1a");
		Campo<String> c1b = new Campo<>();
		c1b.setAtributoId("att2id");
		c1b.setDatos("datos c1b");
		CosteLineaProveedor clp1a = new CosteLineaProveedor();
		clp1a.setCosteProveedorId(cp1a.getId());
		clp1a.setValue(789);
		CosteLineaProveedor clp1b = new CosteLineaProveedor();
		clp1b.setCosteProveedorId(cp1b.getId());
		clp1b.setValue(987);
		Linea lp1 = new Linea();
		lp1.setCounterLineId(new ArrayList<>() {{add("counter1");}});
		lp1.setCampos(new ArrayList<>() {{add(c1a);add(c1b);}});
		lp1.setCostesProveedor(new ArrayList<>() {{add(clp1a);add(clp1b);}});
		lp1.setPropuestaId(pp.getId());
		
		Campo<String> c2a = new Campo<>();
		c2a.setAtributoId("att1id");
		c2a.setDatos("datos c2a");
		CosteLineaProveedor clp2a = new CosteLineaProveedor();
		clp2a.setCosteProveedorId(cp1a.getId());
		clp2a.setValue(456);
		CosteLineaProveedor clp2b = new CosteLineaProveedor();
		clp2b.setCosteProveedorId(cp1b.getId());
		clp2b.setValue(654);
		Linea lp2 = new Linea();
		lp2.setCounterLineId(new ArrayList<>() {{add("counter1");add("counter2");}});
		lp2.setCampos(new ArrayList<>() {{add(c2a);}});
		lp2.setCostesProveedor(new ArrayList<>() {{add(clp2b);}});
		lp2.setPropuestaId(pp.getId());
		
		Campo<String> c3b = new Campo<>();
		c3b.setAtributoId("att2id");
		c3b.setDatos("datos c3b");
		CosteLineaProveedor clp3a = new CosteLineaProveedor();
		clp3a.setCosteProveedorId(cp1a.getId());
		clp3a.setValue(123);
		Linea lp3 = new Linea();
		lp3.setCounterLineId(new ArrayList<>() {{add("counter2");}});
		lp3.setCampos(new ArrayList<>() {{add(c3b);}});
		lp3.setCostesProveedor(new ArrayList<>() {{add(clp3a);}});
		lp3.setPropuestaId(pp.getId());
		
		List<Linea> lineas = new ArrayList<>() {{add(lp1);add(lp2);add(lp3);}};
		
		var concatenator = concatenatorFactory.from(lineas, pvps, consulta);
		
		List<LineaAbstracta> abstractas = reconstructor.from(wrap, concatenator, atributos, pp.getId());
		
		assertEquals(3, abstractas.size());
		assertEquals(l1.getId(), abstractas.get(0).getId());
		assertEquals("datos c1a / datos c2a", abstractas.get(0).getCampos().get(0).getDatosText());
		assertEquals("", abstractas.get(0).getCampos().get(1).getDatosText());
		assertEquals("", abstractas.get(1).getCampos().get(0).getDatosText());
		assertEquals("", abstractas.get(1).getCampos().get(1).getDatosText());
		assertEquals("datos c1a / datos c2a", abstractas.get(2).getCampos().get(0).getDatosText());
		assertEquals("", abstractas.get(2).getCampos().get(1).getDatosText());
	}

}
