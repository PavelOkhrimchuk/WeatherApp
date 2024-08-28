package dto.main.forecast;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeatherForecastResponseDto {

    @JsonProperty("cod")
    private String cod;

    @JsonProperty("message")
    private int message;

    @JsonProperty("cnt")
    private int cnt;

    @JsonProperty("list")
    private List<ForecastItemDto> list;

    @JsonProperty("city")
    private CityDto city;


}
