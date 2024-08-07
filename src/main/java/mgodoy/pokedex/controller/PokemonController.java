package mgodoy.pokedex.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mgodoy.pokedex.dto.PokemonDto;
import mgodoy.pokedex.service.PokemonService;

@RestController
public class PokemonController {

	@Autowired
	private PokemonService service;

	@GetMapping("")
	public CompletableFuture<List<PokemonDto>> getAll(@RequestParam(required = false) Integer offset,
			@RequestParam(required = false) Integer limit) {
		return service.getAll(offset, limit);
	}

	@GetMapping("/{id}")
	public PokemonDto getById(@PathVariable() Integer id) {
		return service.getById(id);
	}
}
