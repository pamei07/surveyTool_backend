package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SurveyEndDateDTO extends AbstractDTO {

    private LocalDateTime endDate;

    public boolean checkIfEndDateValid() {
        ZoneId berlinTime = ZoneId.of("Europe/Berlin");
        ZonedDateTime zonedEndDate = ZonedDateTime.of(this.endDate, berlinTime);
        ZonedDateTime currentDateTime = ZonedDateTime.now(berlinTime);
        return zonedEndDate.isAfter(currentDateTime);
    }
}
