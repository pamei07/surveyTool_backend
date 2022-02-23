package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SurveyConverter {
    private final UserRepository userRepository;

    public Converter<CompleteSurveyDTO, Survey> toDAO = new AbstractConverter<CompleteSurveyDTO, Survey>() {
        @Override
        protected Survey convert(CompleteSurveyDTO source) {
            return toSurveyEntity(source);
        }

        public Survey toSurveyEntity(CompleteSurveyDTO surveyDTO) {
            List<QuestionGroup> questionGroups = toQuestionGroupEntityList(surveyDTO.getQuestionGroups());

            Survey newSurvey = new Survey(surveyDTO.getId(), surveyDTO.getName(), surveyDTO.getDescription(), surveyDTO.getCreatorName(), surveyDTO.getStartDate(), surveyDTO.getEndDate(),
                    surveyDTO.isOpenAccess(), surveyDTO.isAnonymousParticipation(), surveyDTO.getAccessId(), surveyDTO.getParticipationId(), questionGroups);

            // Need to fetch user from db for hibernate to recognize it
            Long userId = surveyDTO.getUserId();
            if (userId != null) {
                Optional<User> userOptional = userRepository.findById(userId);
                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    newSurvey.setUser(user);
                }
            }

            return newSurvey;
        }

        private List<QuestionGroup> toQuestionGroupEntityList(List<QuestionGroupDTO> questionGroupDTOs) {
            return questionGroupDTOs.stream().map(this::toQuestionGroupEntity).collect(Collectors.toList());
        }

        private QuestionGroup toQuestionGroupEntity(QuestionGroupDTO questionGroupDTO) {
            return new QuestionGroup(questionGroupDTO.getTitle(), toQuestionEntityList(questionGroupDTO.getQuestions()));
        }

        private List<Question> toQuestionEntityList(List<QuestionDTO> questionDTOs) {
            return questionDTOs.stream().map(this::toQuestionEntity).collect(Collectors.toList());
        }

        private Question toQuestionEntity(QuestionDTO questionDTO) {
            Question question = new Question(questionDTO.getText(), questionDTO.isRequired(),
                    questionDTO.getQuestionType());

            if (questionDTO.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                CheckboxGroup checkboxGroup = toCheckboxGroupEntity(questionDTO.getCheckboxGroup());
                // Set references for the one-to-one-relationship
                checkboxGroup.setQuestion(question);
                question.setCheckboxGroup(checkboxGroup);
            }

            return question;
        }

        private CheckboxGroup toCheckboxGroupEntity(CheckboxGroupDTO checkboxGroupDTO) {
            return new CheckboxGroup(checkboxGroupDTO.isMultipleSelect(), checkboxGroupDTO.getMinSelect(),
                    checkboxGroupDTO.getMaxSelect(), toCheckboxEntityList(checkboxGroupDTO.getCheckboxes()));
        }

        private List<Checkbox> toCheckboxEntityList(List<CheckboxDTO> checkboxDTOs) {
            return checkboxDTOs.stream().map(this::toCheckboxEntity).collect(Collectors.toList());
        }

        private Checkbox toCheckboxEntity(CheckboxDTO checkboxDTO) {
            return new Checkbox(checkboxDTO.getText(), checkboxDTO.isHasTextField(), checkboxDTO.getPlaceholder());
        }
    };

}
