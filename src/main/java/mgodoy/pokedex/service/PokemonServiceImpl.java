package mgodoy.pokedex.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import mgodoy.pokedex.dto.PokemonApiResponseDto;
import mgodoy.pokedex.dto.PokemonDto;
import mgodoy.pokedex.dto.PokemonApiDto;
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

		List<PokemonApiDto> pokemons = response.getBody().getResults();

		// Obtener los detalles de cada Pokémon de forma asíncrona
		List<CompletableFuture<PokemonDto>> pokemonFutures = pokemons.stream().map(pokemon -> {
			return CompletableFuture.supplyAsync(() -> {
				ResponseEntity<PokemonApiDto> pokemonResponse = restTemplate.getForEntity(pokemon.getUrl(),
						PokemonApiDto.class);
				return convertToPokemonDto(pokemonResponse.getBody());
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

		ResponseEntity<PokemonApiDto> response = restTemplate.getForEntity(url, PokemonApiDto.class);

		PokemonApiDto pokemon = response.getBody();

		if (pokemon == null)
			throw new ConflictException("Error al obtener obtener el pokemon con ese identificador.");

		pokemon.setDescription(getDescriptionById(id));

		return convertToPokemonDto(pokemon);
	}

	private PokemonDto convertToPokemonDto(PokemonApiDto apiDto) {
		PokemonDto dto = new PokemonDto();
		dto.setId(apiDto.getId());
		dto.setAbilities(apiDto.getAbilities().stream().map(item -> item.getAbility().getName()).toList());
		dto.setDescription(apiDto.getDescription());
		dto.setHeight(apiDto.getHeight());
		dto.setMoves(apiDto.getMoves().stream().map(item -> item.getMove().getName()).toList());
		dto.setName(apiDto.getName());
		dto.setTypes(apiDto.getTypes().stream().map(item -> item.getType().getName()).toList());
		dto.setUrl(apiDto.getSprites().getFrontDefault());
		dto.setWeight(apiDto.getWeight());
		return dto;
	}
}
