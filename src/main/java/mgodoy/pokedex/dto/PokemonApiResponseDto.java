package mgodoy.pokedex.dto;

import java.util.List;

import lombok.Data;

@Data
public class PokemonApiResponseDto {
    private List<PokemonApiDto> results;

}
