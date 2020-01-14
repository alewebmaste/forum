package br.com.alura.forum.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.dto.DetalhesdoTopicoDTO;
import br.com.alura.forum.dto.TopicoDTO;
import br.com.alura.forum.form.AtualizacaoTopicosForm;
import br.com.alura.forum.form.TopicosForm;
import br.com.alura.forum.model.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

	@Autowired
	private TopicoRepository topicoRepository;

	@Autowired
	private CursoRepository cursoRepository;

	@GetMapping
	public List<TopicoDTO> listar(String cursoNome) {

		List<Topico> topicos = new ArrayList<>();

		if (cursoNome == null) {
			topicos = topicoRepository.findAll();

		} else {
			topicos = topicoRepository.findByCursoNome(cursoNome);
		}

		return TopicoDTO.converter(topicos);
	}

	@PostMapping
	public ResponseEntity<TopicoDTO> cadastrar(@RequestBody @Valid TopicosForm form, UriComponentsBuilder uriBuilder) {

		Topico topico = form.converter(cursoRepository);

		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

		return ResponseEntity.created(uri).body(new TopicoDTO(topico));

	}

	@GetMapping("{id}")
	public DetalhesdoTopicoDTO detalhar(@PathVariable Long id) {
		Topico topico = topicoRepository.getOne(id);

		return new DetalhesdoTopicoDTO(topico);
	}

	@PutMapping("{id}")
	@Transactional
	public ResponseEntity<TopicoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicosForm form) {

		Topico topico = form.atualizar(id, topicoRepository);

		return ResponseEntity.ok(new TopicoDTO(topico));

	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> remover(@PathVariable Long id) {

		topicoRepository.deleteById(id);

		return ResponseEntity.ok().build();
	}

}
