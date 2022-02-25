package iks.surveytool.dtos;

import iks.surveytool.entities.QuestionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuestionDTO extends AbstractDTO {

    private String text;
    private boolean required;
    private QuestionType questionType;

    private CheckboxGroupDTO checkboxGroup;
    private RankingGroupDTO rankingGroup;

}
