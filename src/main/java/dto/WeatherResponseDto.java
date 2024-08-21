package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherResponseDto {


    @JsonProperty("coord")
    private CoordinatesDto coord;


    @JsonProperty("weather")
    private List<WeatherDto> weather;

    @JsonProperty("base")
    private String base;

    @JsonProperty("main")
    private MainDto main;


    @JsonProperty("visibility")
    private int visibility;

    @JsonProperty("wind")
    private WindDto wind;

    @JsonProperty("clouds")
    private CloudsDto clouds;


    @JsonProperty("dt")
    private long dt;

    @JsonProperty("sys")
    private SysDto sys;


    @JsonProperty("timezone")
    private int timezone;


    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cod")
    private int cod;




}
