package devs.mrp.gullproject.repositorios;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import devs.mrp.gullproject.domains.Tipo;

public interface TipoRepo extends ReactiveCrudRepository<Tipo, String> {

}
