package mgodoy.pokedex.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonApiDto {
	private String name;
	private String url;
	private Integer id;
	private Sprites sprites;
	private Integer weight;
	private Integer height;
	private List<TypeContainer> types;
	private List<AbilityContainer> abilities;
	private List<MovesContainer> moves;
	private String description;

	@Getter
	public static class Sprites {
		@JsonProperty("front_default")
		private String frontDefault;

		@JsonProperty("back_default")
		private String backDefault;
	}

	@Getter
	public static class TypeContainer {
		private Type type;
	}

	@Getter
	public static class Type {
		private String name;
	}

	@Getter
	public static class AbilityContainer {
		private Ability ability;

	}

	@Getter
	public static class MovesContainer {
		private Move move;
	}

	@Getter
	public static class Move {
		private String name;
	}

	@Getter
	public static class Ability {
		private String name;
	}
}