package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WindDto {

    @JsonProperty("speed")
    private double speed;

    @JsonProperty("deg")
    private int deg;

    @JsonProperty("gust")
    private Double gust;
}
