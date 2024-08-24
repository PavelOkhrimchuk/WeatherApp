package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("coord")
    private CoordinatesDto coord;

    @JsonProperty("country")
    private String country;

    @JsonProperty("population")
    private int population;

    @JsonProperty("timezone")
    private int timezone;

    @JsonProperty("sunrise")
    private long sunrise;

    @JsonProperty("sunset")
    private long sunset;
}
