package devs.mrp.gullproject.service.linea.oferta;

import java.util.List;

import devs.mrp.gullproject.ainterfaces.MyFactoryFromTo;
import devs.mrp.gullproject.ainterfaces.MyMapperByDupla;
import devs.mrp.gullproject.domains.linea.Linea;

public interface PvpMapperByCounterLineFactory <T extends Linea> extends MyFactoryFromTo<List<T>, MyMapperByDupla<Double, String, String>> {

}
