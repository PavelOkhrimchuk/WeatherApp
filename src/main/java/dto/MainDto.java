package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainDto {

    @JsonProperty("temp")
    private double temp;

    @JsonProperty("feels_like")
    private double feelsLike;

    @JsonProperty("temp_min")
    private double tempMin;

    @JsonProperty("temp_max")
    private double tempMax;

    @JsonProperty("pressure")
    private int pressure;

    @JsonProperty("humidity")
    private int humidity;

    @JsonProperty("sea_level")
    private Integer seaLevel;

    @JsonProperty("grnd_level")
    private Integer grndLevel;

    public String getTempCelsius() {
        return String.format("%.2f", temp - 273.15);
    }


    public String getFeelsLikeCelsius() {
        return String.format("%.2f", feelsLike - 273.15);
    }


    public String getTempMinCelsius() {
        return String.format("%.2f", tempMin - 273.15);
    }


    public String getTempMaxCelsius() {
        return String.format("%.2f", tempMax - 273.15);
    }


}
