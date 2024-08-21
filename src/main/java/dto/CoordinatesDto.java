package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoordinatesDto {

    @JsonProperty("lon")
    private double lon;

    @JsonProperty("lat")
    private double lat;
}
