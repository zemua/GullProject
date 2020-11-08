package devs.mrp.gullproject.repositorios;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import devs.mrp.gullproject.domains.Tipo;

@Repository
public interface TipoRepo extends ReactiveCrudRepository<Tipo, String> {

}
