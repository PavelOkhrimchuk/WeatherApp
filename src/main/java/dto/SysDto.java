package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysDto {

    @JsonProperty("type")
    private int type;

    @JsonProperty("id")
    private int id;

    @JsonProperty("country")
    private String country;

    @JsonProperty("sunrise")
    private long sunrise;

    @JsonProperty("sunset")
    private long sunset;
}
