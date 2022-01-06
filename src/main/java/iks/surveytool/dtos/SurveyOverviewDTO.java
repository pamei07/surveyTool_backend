package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class SurveyOverviewDTO extends AbstractDTO {

    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean openAccess;
    private boolean anonymousParticipation;
    private String accessId;
    private String participationId;

    private Long userId;
    private String creatorName;

    public SurveyOverviewDTO(Long id,
                             String name,
                             String description,
                             LocalDateTime startDate,
                             LocalDateTime endDate,
                             boolean openAccess,
                             boolean anonymousParticipation,
                             String accessId,
                             String participationId,
                             Long userId,
                             String creatorName) {
        super(id);
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openAccess = openAccess;
        this.anonymousParticipation = anonymousParticipation;
        this.accessId = accessId;
        this.participationId = participationId;
        this.userId = userId;
        this.creatorName = creatorName;
    }
}
