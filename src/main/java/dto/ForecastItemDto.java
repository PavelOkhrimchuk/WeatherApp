package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonProperty("rain")
    private RainDto rain;

    @JsonProperty("pop")
    private double pop;

    @JsonProperty("sys")
    private SysForecastDto sys;

    @JsonProperty("dt_txt")
    private String dtTxt;
}
