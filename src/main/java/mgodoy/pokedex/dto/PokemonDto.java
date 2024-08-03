package mgodoy.pokedex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonDto {
	private String name;
	private String url;
	private Integer id;
	private Sprites sprites;

	public static class Sprites {
		@JsonProperty("front_default")
		private String frontDefault;

		// Getter and Setter
	}
}