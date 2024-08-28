package dto.main.forecast;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysForecastDto {

    @JsonProperty("pod")
    private String pod;
}
