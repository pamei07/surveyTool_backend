package iks.surveytool.mapping;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.repositories.UserRepository;
import iks.surveytool.utils.assertions.MappingAssertions;
import iks.surveytool.utils.builder.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Testcontainers
@DisplayName("Testing Mapper")
class MappingTest {

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Fill database as SurveyConverter and AnswerConverter retrieve objects from db to convert DTOs into DAOs
     */
    @BeforeAll
    static void fillDatabase(@Autowired UserRepository userRepository,
                             @Autowired SurveyRepository surveyRepository,
                             @Autowired CheckboxRepository checkboxRepository) {
        User user1 = new UserBuilder().createUser(1L, "Test Person", "user1@default.de");
        User user2 = new UserBuilder().createUser(2L, "Test Person #2", "user2@default.de");
        userRepository.saveAll(List.of(user1, user2));

        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox));

        Question question1 = new QuestionBuilder().createQuestion(1L, "Frage 1", true, false);
        Question question2 = new QuestionBuilder().createQuestion(2L, "Frage 2", false, true);
        question2.setCheckboxGroup(checkboxGroup);
        checkboxGroup.setQuestion(question2);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Questions");
        questionGroupWithQuestion.setQuestions(List.of(question1, question2));

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey init", user1);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        surveyRepository.save(survey);
    }

    @Test
    @DisplayName("Survey to CompleteSurveyDTO")
    void mapSurveyToDTO() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);
        Checkbox thirdCheckbox = new CheckboxBuilder()
                .createCheckbox(3L, "Third Test Checkbox", true);
        Checkbox fourthCheckbox = new CheckboxBuilder()
                .createCheckbox(4L, "Fourth Test Checkbox", false);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, true, 1, 3);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox, thirdCheckbox, fourthCheckbox));

        Question question = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, true);
        question.setCheckboxGroup(checkboxGroup);

        QuestionGroup questionGroupWithQuestion = new QuestionGroupBuilder()
                .createQuestionGroup(1L, "QuestionGroup with Question");
        questionGroupWithQuestion.setQuestions(List.of(question));

        User user = new UserBuilder().createUser(1L, "Test Person");

        Survey survey = new SurveyBuilder()
                .createSurveyWithUserAndDefaultDate(1L, "Complete Survey", user);
        survey.setQuestionGroups(List.of(questionGroupWithQuestion));

        CompleteSurveyDTO surveyDTO = modelMapper.map(survey, CompleteSurveyDTO.class);

        MappingAssertions.assertCompleteSurveyDTO(surveyDTO, survey);
    }

    @Test
    @DisplayName("CompleteSurveyDTO to Survey")
    void mapSurveyDTOtoEntity() {
        CheckboxDTO firstCheckboxDTO = new CheckboxDTO();
        firstCheckboxDTO.setText("First Checkbox DTO");
        firstCheckboxDTO.setHasTextField(false);
        CheckboxDTO secondCheckboxDTO = new CheckboxDTO();
        secondCheckboxDTO.setText("Second Checkbox DTO");
        secondCheckboxDTO.setHasTextField(true);

        CheckboxGroupDTO checkboxGroupDTO = new CheckboxGroupDTO();
        checkboxGroupDTO.setMultipleSelect(false);
        checkboxGroupDTO.setCheckboxes(List.of(firstCheckboxDTO, secondCheckboxDTO));

        QuestionDTO firstQuestionDTO = new QuestionDTO();
        firstQuestionDTO.setText("Test Checkbox Question");
        firstQuestionDTO.setRequired(false);
        firstQuestionDTO.setHasCheckbox(true);
        firstQuestionDTO.setCheckboxGroup(checkboxGroupDTO);

        QuestionDTO secondQuestionDTO = new QuestionDTO();
        secondQuestionDTO.setText("Test Text Question");
        secondQuestionDTO.setRequired(true);
        secondQuestionDTO.setHasCheckbox(false);

        QuestionGroupDTO questionGroupDTO = new QuestionGroupDTO();
        questionGroupDTO.setTitle("Test QuestionGroup");
        questionGroupDTO.setQuestions(List.of(firstQuestionDTO, secondQuestionDTO));

        CompleteSurveyDTO surveyDTO = new CompleteSurveyDTO();
        surveyDTO.setName("Test Survey");
        surveyDTO.setStartDate(LocalDateTime.of(2050, 1, 1, 12, 0));
        surveyDTO.setEndDate(surveyDTO.getStartDate().plusWeeks(1L));
        surveyDTO.setOpenAccess(false);
        surveyDTO.setAnonymousParticipation(true);
        surveyDTO.setUserId(1L);
        surveyDTO.setCreatorName("Max Mustermann");
        surveyDTO.setQuestionGroups(List.of(questionGroupDTO));

        Survey surveyConverted = modelMapper.map(surveyDTO, Survey.class);

        MappingAssertions.assertSurvey(surveyConverted, surveyDTO);
    }

    @Test
    @DisplayName("AnswerList to AnswerDTOList")
    void mapAnswerListToAnswerDTOs() {
        Checkbox firstCheckbox = new CheckboxBuilder()
                .createCheckbox(1L, "First Test Checkbox", false);
        Checkbox secondCheckbox = new CheckboxBuilder()
                .createCheckbox(2L, "Second Test Checkbox", true);

        CheckboxGroup checkboxGroup = new CheckboxGroupBuilder()
                .createCheckboxGroup(1L, false, 0, 2);
        checkboxGroup.setCheckboxes(List.of(firstCheckbox, secondCheckbox));

        Question firstQuestion = new QuestionBuilder()
                .createQuestion(1L, "Test Question", false, false);
        Question secondQuestion = new QuestionBuilder()
                .createQuestion(2L, "Test Question", false, true);
        secondQuestion.setCheckboxGroup(checkboxGroup);

        User user = new UserBuilder().createUser(2L, "Test Person #2");

        Answer firstAnswer = new AnswerBuilder()
                .createAnswer(1L, "Test Answer", user, firstQuestion, null);
        Answer secondAnswer = new AnswerBuilder()
                .createAnswer(2L, null, user, secondQuestion, firstCheckbox);

        List<Answer> answers = List.of(firstAnswer, secondAnswer);

        Type answerDTOListType = new TypeToken<List<AnswerDTO>>() {
        }.getType();
        List<AnswerDTO> answerDTOList = modelMapper.map(answers, answerDTOListType);

        MappingAssertions.assertAnswerDTOs(answerDTOList, answers);
    }

    @Test
    @DisplayName("AnswerDTOList to AnswerList")
    void mapAnswerDTOsToAnswers() {
        AnswerDTO firstAnswerDTO = new AnswerDTO();
        firstAnswerDTO.setText("Text");
        firstAnswerDTO.setUserId(2L);
        firstAnswerDTO.setParticipantName("Test User");
        firstAnswerDTO.setQuestionId(1L);
        AnswerDTO secondAnswerDTO = new AnswerDTO();
        secondAnswerDTO.setUserId(2L);
        secondAnswerDTO.setParticipantName("Test User");
        secondAnswerDTO.setQuestionId(2L);
        secondAnswerDTO.setCheckboxId(1L);

        List<AnswerDTO> answerDTOs = List.of(secondAnswerDTO, secondAnswerDTO);

        Type answerListType = new TypeToken<List<Answer>>() {
        }.getType();
        List<Answer> answers = modelMapper.map(answerDTOs, answerListType);

        MappingAssertions.assertAnswers(answers, answerDTOs);
    }

    @Test
    @DisplayName("User to UserDTO")
    void mapUserToUserDTO() {
        User user = new UserBuilder()
                .createUser(1L, "Test User");

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        MappingAssertions.assertUserDTO(userDTO, user);
    }

    @Test
    @DisplayName("UserDTO to User")
    void mapUserDTOToUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");

        User user = modelMapper.map(userDTO, User.class);

        MappingAssertions.assertUser(user, userDTO);
    }

}
