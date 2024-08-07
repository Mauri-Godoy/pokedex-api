package mgodoy.pokedex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
public class PokemonSpeciesDto {
	@JsonProperty("flavor_text_entries")
	private List<FlavorTextEntry> flavorTextEntries;

	@Getter
	public static class FlavorTextEntry {
		@JsonProperty("flavor_text")
		private String flavorText;
		private Language language;
	}

	@Getter
	public static class Language {
		private String name;
	}
}
