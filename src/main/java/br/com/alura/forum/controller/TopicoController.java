package br.com.alura.forum.controller;

import java.net.URI;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	@Cacheable(value = "listaDeTopicos")
	public Page<TopicoDTO> listar(@RequestParam(required = false) String cursoNome,
			@PageableDefault(sort = "id") Pageable paginacao) {

		if (cursoNome == null) {
			Page<Topico> topicos = topicoRepository.findAll(paginacao);
			return TopicoDTO.converter(topicos);

		} else {
			Page<Topico> topicos = topicoRepository.findByCursoNome(cursoNome, paginacao);
			return TopicoDTO.converter(topicos);
		}

	}

	@PostMapping
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> cadastrar(@RequestBody @Valid TopicosForm form, UriComponentsBuilder uriBuilder) {

		Topico topico = form.converter(cursoRepository);

		topicoRepository.save(topico);

		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();

		return ResponseEntity.created(uri).body(new TopicoDTO(topico));

	}

	@GetMapping("{id}")
	public ResponseEntity<DetalhesdoTopicoDTO> detalhar(@PathVariable Long id) {

		Optional<Topico> topico = topicoRepository.findById(id);

		if (topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesdoTopicoDTO(topico.get()));
		}

		return ResponseEntity.notFound().build();

	}

	@PutMapping("{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<TopicoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicosForm form) {

		Optional<Topico> optionalTopico = topicoRepository.findById(id);

		if (optionalTopico.isPresent()) {
			Topico topico = form.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDTO(topico));
		}

		return ResponseEntity.notFound().build();

	}

	@DeleteMapping("/{id}")
	@Transactional
	@CacheEvict(value = "listaDeTopicos", allEntries = true)
	public ResponseEntity<?> remover(@PathVariable Long id) {

		Optional<Topico> optionalTopico = topicoRepository.findById(id);

		if (optionalTopico.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}

		return ResponseEntity.notFound().build();

	}

}
