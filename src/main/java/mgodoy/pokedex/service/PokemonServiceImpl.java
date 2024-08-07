package mgodoy.pokedex.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mgodoy.pokedex.dto.PokemonApiResponseDto;
import mgodoy.pokedex.dto.PokemonDto;
import mgodoy.pokedex.dto.PokemonSpeciesDto;
import mgodoy.pokedex.exception.ConflictException;
import mgodoy.pokedex.util.Constants;

@Service
public class PokemonServiceImpl implements PokemonService {

	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public CompletableFuture<List<PokemonDto>> getAll(Integer offset, Integer limit) {
		String url = Constants.POKEMON_ENDPOINT;

		if (offset != null && limit != null) {
			url = String.format("%s?offset=%s&limit=%s", url, offset, limit);
		}

		ResponseEntity<PokemonApiResponseDto> response = restTemplate.getForEntity(url, PokemonApiResponseDto.class);

		if (response.getBody() == null)
			throw new ConflictException("Error al obtener lista de pokemones.");

		List<PokemonDto> pokemons = response.getBody().getResults();

		// Obtener los detalles de cada Pokémon de forma asíncrona
		List<CompletableFuture<PokemonDto>> pokemonFutures = pokemons.stream().map(pokemon -> {
			return CompletableFuture.supplyAsync(() -> {
				// Obtener detalles del Pokémon
				ResponseEntity<PokemonDto> pokemonResponse = restTemplate.getForEntity(pokemon.getUrl(),
						PokemonDto.class);
				PokemonDto pokemonDto = pokemonResponse.getBody();

				// Setear la descripción en el Pokémon
				pokemonDto.setDescription(getDescriptionById(pokemonDto.getId()));

				return pokemonDto;
			});
		}).toList();

		return CompletableFuture.allOf(pokemonFutures.toArray(new CompletableFuture[0]))
				.thenApply(v -> pokemonFutures.stream().map(CompletableFuture::join).toList());
	}

	private String getDescriptionById(Integer id) {
		String speciesUrl = String.format("%s/%s", Constants.POKEMON_SPECIES_ENDPOINT, id);

		ResponseEntity<PokemonSpeciesDto> speciesResponse = restTemplate.getForEntity(speciesUrl,
				PokemonSpeciesDto.class);
		PokemonSpeciesDto speciesData = speciesResponse.getBody();

		if (speciesData == null || speciesData.getFlavorTextEntries() == null)
			return "Descripción no disponible.";

		Optional<String> spanishDescription = speciesData.getFlavorTextEntries().stream()
				.filter(entry -> "es".equals(entry.getLanguage().getName()))
				.map(PokemonSpeciesDto.FlavorTextEntry::getFlavorText).findFirst();
		return spanishDescription.orElse("Descripción no disponible.");
	}

	@Override
	public PokemonDto getById(Integer id) {
		String url = Constants.POKEMON_ENDPOINT + "/" + id;

		ResponseEntity<PokemonDto> response = restTemplate.getForEntity(url, PokemonDto.class);

		PokemonDto pokemon = response.getBody();

		if (pokemon == null)
			throw new ConflictException("Error al obtener obtener el pokemon con ese identificador.");

		pokemon.setDescription(getDescriptionById(id));

		return pokemon;
	}
}
