package iks.surveytool.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerDTO extends AbstractDTO {

    private String text;

    private Long userId;
    private String participantName;
    private String participantId;
    private int rank;

    private Long checkboxId;
    private Long questionId;
    private Long optionId;

    public AnswerDTO(Long id, String text, Long userId, String participantName) {
        super(id);
        this.text = text;
        this.userId = userId;
        this.participantName = participantName;
    }

    public AnswerDTO(Long id, String text, Long userId, String participantName, Long checkboxId) {
        super(id);
        this.text = text;
        this.userId = userId;
        this.participantName = participantName;
        this.checkboxId = checkboxId;
    }
}
