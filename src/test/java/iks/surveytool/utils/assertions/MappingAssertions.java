package iks.surveytool.utils.assertions;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MappingAssertions {
    public static void assertSurveyMapping(Survey survey, CompleteSurveyDTO surveyDTO) {
        assertEquals(survey.getId(), surveyDTO.getId());
        assertEquals(survey.getName(), surveyDTO.getName());
        assertEquals(survey.getDescription(), surveyDTO.getDescription());
        assertEquals(survey.getStartDate(), surveyDTO.getStartDate());
        assertEquals(survey.getEndDate(), surveyDTO.getEndDate());
        assertEquals(survey.isOpenAccess(), surveyDTO.isOpenAccess());
        assertEquals(survey.isAnonymousParticipation(), surveyDTO.isAnonymousParticipation());
        assertEquals(survey.getAccessId(), surveyDTO.getAccessId());
        assertEquals(survey.getParticipationId(), surveyDTO.getParticipationId());
        assertEquals(survey.getCreatorName(), surveyDTO.getCreatorName());

        if (survey.getUser() != null) {
            assertEquals(survey.getUser().getId(), surveyDTO.getUserId());
        }

        List<QuestionGroup> questionGroups = survey.getQuestionGroups();
        List<QuestionGroupDTO> questionGroupDTOs = surveyDTO.getQuestionGroups();
        for (int i = 0; i < questionGroups.size(); i++) {
            assertQuestionGroup(questionGroups.get(i), questionGroupDTOs.get(i));
        }
    }

    private static void assertQuestionGroup(QuestionGroup questionGroup, QuestionGroupDTO questionGroupDTO) {
        assertEquals(questionGroup.getId(), questionGroupDTO.getId());
        assertEquals(questionGroup.getTitle(), questionGroupDTO.getTitle());

        List<Question> questions = questionGroup.getQuestions();
        List<QuestionDTO> questionDTOs = questionGroupDTO.getQuestions();
        for (int i = 0; i < questionDTOs.size(); i++) {
            assertQuestion(questions.get(i), questionDTOs.get(i));
        }
    }

    private static void assertQuestion(Question question, QuestionDTO questionDTO) {
        assertEquals(question.getId(), questionDTO.getId());
        assertEquals(question.getText(), questionDTO.getText());
        assertEquals(question.isRequired(), questionDTO.isRequired());
        assertEquals(question.isHasCheckbox(), questionDTO.isHasCheckbox());
        assertEquals(question.getQuestionType(), questionDTO.getQuestionType());

        if (questionDTO.isHasCheckbox()) {
            CheckboxGroup checkboxGroup = question.getCheckboxGroup();
            CheckboxGroupDTO checkboxGroupDTO = questionDTO.getCheckboxGroup();
            assertCheckboxGroup(checkboxGroup, checkboxGroupDTO);
        }
    }

    private static void assertCheckboxGroup(CheckboxGroup checkboxGroup, CheckboxGroupDTO checkboxGroupDTO) {
        assertEquals(checkboxGroup.getId(), checkboxGroupDTO.getId());
        assertEquals(checkboxGroup.isMultipleSelect(), checkboxGroupDTO.isMultipleSelect());
        assertEquals(checkboxGroup.getMinSelect(), checkboxGroupDTO.getMinSelect());
        assertEquals(checkboxGroup.getMaxSelect(), checkboxGroupDTO.getMaxSelect());

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        List<CheckboxDTO> checkboxDTOs = checkboxGroupDTO.getCheckboxes();
        for (int i = 0; i < checkboxDTOs.size(); i++) {
            assertCheckbox(checkboxes.get(i), checkboxDTOs.get(i));
        }
    }

    private static void assertCheckbox(Checkbox checkbox, CheckboxDTO checkboxDTO) {
        assertEquals(checkbox.getText(), checkboxDTO.getText());
        assertEquals(checkbox.isHasTextField(), checkboxDTO.isHasTextField());
        assertEquals(checkbox.getPlaceholder(), checkboxDTO.getPlaceholder());
    }

    public static void assertAnswerMapping(List<Answer> answers, List<AnswerDTO> answerDTOs) {
        for (int i = 0; i < answerDTOs.size(); i++) {
            assertAnswer(answers.get(i), answerDTOs.get(i));
        }
    }

    private static void assertAnswer(Answer answer, AnswerDTO answerDTO) {
        assertEquals(answer.getId(), answerDTO.getId());
        assertEquals(answer.getText(), answerDTO.getText());
        assertEquals(answer.getParticipantName(), answerDTO.getParticipantName());
        assertEquals(answer.getParticipantId(), answerDTO.getParticipantId());

        if (answer.getUser() != null) {
            assertEquals(answer.getUser().getId(), answerDTO.getUserId());
        }

        if (answer.getCheckbox() != null) {
            assertEquals(answer.getCheckbox().getId(), answerDTO.getCheckboxId());
        }

        if (answer.getQuestion() != null) {
            assertEquals(answer.getQuestion().getId(), answerDTO.getQuestionId());
        }
    }

    public static void assertUserMapping(User user, UserDTO userDTO) {
        assertEquals(user.getId(), userDTO.getId());
        assertEquals(user.getName(), userDTO.getName());
        assertEquals(user.getFirstName(), userDTO.getFirstName());
        assertEquals(user.getLastName(), userDTO.getLastName());
        assertEquals(user.getEmail(), userDTO.getEmail());
    }
}
