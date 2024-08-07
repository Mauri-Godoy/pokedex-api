package mgodoy.pokedex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonDto {
	private String name;
	private String url;
	private Integer id;
	private Sprites sprites;
	private Integer weight;
	private List<Type> types;
	private List<AbilityContainer> abilities;
	private String description;

	@Getter
	private static class Sprites {
		@JsonProperty("front_default")
		private String frontDefault;

		@JsonProperty("back_default")
		private String backDefault;
	}

	@Getter
	private static class Type {
		private String name;
	}

	@Getter
	private static class AbilityContainer {
		private Ability ability;
	}

	@Getter
	private static class Ability {
		private String name;
	}
}