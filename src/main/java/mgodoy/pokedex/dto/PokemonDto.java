package mgodoy.pokedex.dto;

import java.util.List;

import lombok.Data;

@Data
public class PokemonDto {
	Integer id;
	String name;
	String url;
	List<String> types;
	Integer weight;
	Integer height;
	List<String> abilities;
	String description;
	List<String> moves;
}
