package devs.mrp.gullproject.service;

import org.springframework.beans.factory.annotation.Autowired;

import devs.mrp.gullproject.repositorios.AtributoRepo;

public class AtributoService {
	
	private AtributoRepo atributoRepo;
	
	@Autowired
	public AtributoService(AtributoRepo atributoRepo) {
		this.atributoRepo = atributoRepo;
	}

}
