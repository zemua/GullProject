package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyFactoryFrom2To;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;
import devs.mrp.gullproject.domains.propuestas.PropuestaNuestra;

public interface PvpSumByCounterIdFactory extends MyFactoryFrom2To<MyMapperByDupla<Double, String, String>, PropuestaNuestra, List<Linea>> {

}
