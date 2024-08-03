package mgodoy.pokedex.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mgodoy.pokedex.dto.PokemonApiResponseDto;
import mgodoy.pokedex.dto.PokemonDto;
import mgodoy.pokedex.exception.ConflictException;
import mgodoy.pokedex.util.Constants;

@Service
public class PokemonServiceImpl implements PokemonService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public CompletableFuture<List<PokemonDto>> getAll() {
		String url = Constants.POKEMON_ENDPOINT;

		ResponseEntity<PokemonApiResponseDto> response = restTemplate.getForEntity(url, PokemonApiResponseDto.class);

		if (response.getBody() == null)
			throw new ConflictException("Error al obtener lista de pokemones.");

		List<PokemonDto> pokemons = response.getBody().getResults();

		List<String> pokemonUrls = pokemons.stream().map(PokemonDto::getUrl).toList();

		// Obtener los detalles de cada Pokémon de forma asíncrona
		List<CompletableFuture<PokemonDto>> pokemonFutures = pokemonUrls.stream().map(pokemonUrl -> CompletableFuture
				.supplyAsync(() -> restTemplate.getForObject(pokemonUrl, PokemonDto.class))).toList();

		return CompletableFuture.allOf(pokemonFutures.toArray(new CompletableFuture[0]))
				.thenApply(v -> pokemonFutures.stream().map(CompletableFuture::join).toList());
	}
}
