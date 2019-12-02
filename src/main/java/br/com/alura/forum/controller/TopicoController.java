package br.com.alura.forum.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.forum.dto.TopicoDTO;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
public class TopicoController {

	@Autowired
	private TopicoRepository topicoRepository;

	@RequestMapping("/topicos")
	public List<TopicoDTO> listar(String cursoNome) {

		List<Topico> topicos = new ArrayList<>();
		
		if(cursoNome == null) {
			topicos = topicoRepository.findAll();
			
		}else {
			topicos = topicoRepository.findByCursoNome(cursoNome);
		}

		return TopicoDTO.converter(topicos);
	}

}
