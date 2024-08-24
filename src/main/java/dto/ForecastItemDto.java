package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastItemDto {

    @JsonProperty("dt")
    private long dt;

    @JsonProperty("main")
    private MainDto main;

    @JsonProperty("weather")
    private List<WeatherDto> weather;

    @JsonProperty("clouds")
    private CloudsDto clouds;

    @JsonProperty("wind")
    private WindDto wind;

    @JsonProperty("visibility")
    private int visibility;

    @JsonProperty("pop")
    private double pop;

    @JsonProperty("sys")
    private SysDto sys;

    @JsonProperty("dt_txt")
    private String dtTxt;
}
