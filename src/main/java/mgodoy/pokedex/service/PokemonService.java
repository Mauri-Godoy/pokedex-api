package mgodoy.pokedex.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import mgodoy.pokedex.dto.PokemonDto;

public interface PokemonService {

	CompletableFuture<List<PokemonDto>> getAll(Integer offset, Integer limit);

}
