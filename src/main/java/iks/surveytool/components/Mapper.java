package iks.surveytool.components;

import iks.surveytool.dtos.*;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final UserRepository userRepository;

    // User/-DTO:
    public List<UserDTO> toParticipatingUserDTOList(List<User> users) {
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = toUserDTO(user);
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    public UserDTO toUserDTO(User user) {
        Long userId;
        String userName;
        if (user != null) {
            userId = user.getId();
            userName = user.getName();
        } else {
            userId = null;
            userName = "Anonym";
        }
        return new UserDTO(userId, userName);
    }

    public User toUserEntity(UserDTO userDTO) {
        String name = userDTO.getName();
        return new User(name);
    }

    // Survey/-DTO (and Classes that "belong" to Survey):
    public List<SurveyOverviewDTO> toOpenAccessSurveyDTOList(List<Survey> openAccessSurveys) {
        List<SurveyOverviewDTO> openAccessSurveyDTOs = new ArrayList<>();
        for (Survey survey : openAccessSurveys) {
            SurveyOverviewDTO surveyOverviewDTO = toSurveyDTO(survey, false);
            openAccessSurveyDTOs.add(surveyOverviewDTO);
        }
        return openAccessSurveyDTOs;
    }

    public SurveyOverviewDTO toSurveyDTO(Survey survey, boolean complete) {
        Long id = survey.getId();
        String name = survey.getName();
        String description = survey.getDescription();
        LocalDateTime startDate = survey.getStartDate();
        LocalDateTime endDate = survey.getEndDate();
        boolean openAccess = survey.isOpenAccess();
        String accessId = survey.getAccessId();
        String participationId = survey.getParticipationId();

        User user = survey.getUser();
        Long userId;
        String userName;
        if (user != null) {
            userId = user.getId();
            userName = user.getName();
        } else {
            userId = null;
            userName = "Anonym";
        }

        if (complete) {
            List<QuestionGroup> questionGroups = survey.getQuestionGroups();
            List<QuestionGroupDTO> questionGroupDTOs = toQuestionGroupDTOList(questionGroups);
            return new CompleteSurveyDTO(id, name, description, startDate, endDate, openAccess, accessId, participationId, userId, userName,
                    questionGroupDTOs);
        } else {
            return new SurveyOverviewDTO(id, name, description, startDate, endDate, openAccess, accessId, participationId, userId, userName);
        }
    }

    private List<QuestionGroupDTO> toQuestionGroupDTOList(List<QuestionGroup> questionGroups) {
        List<QuestionGroupDTO> questionGroupDTOs = new ArrayList<>();
        for (QuestionGroup questionGroup : questionGroups) {
            QuestionGroupDTO questionGroupDTO = toQuestionGroupDTO(questionGroup);
            questionGroupDTOs.add(questionGroupDTO);
        }
        return questionGroupDTOs;
    }

    private QuestionGroupDTO toQuestionGroupDTO(QuestionGroup questionGroup) {
        Long id = questionGroup.getId();
        String title = questionGroup.getTitle();
        List<Question> questions = questionGroup.getQuestions();
        List<QuestionDTO> questionDTOs = toQuestionDTOList(questions);

        return new QuestionGroupDTO(id, title, questionDTOs);
    }

    private List<QuestionDTO> toQuestionDTOList(List<Question> questions) {
        List<QuestionDTO> questionDTOs = new ArrayList<>();
        for (Question question : questions) {
            QuestionDTO questionDTO = toQuestionDTO(question);
            questionDTOs.add(questionDTO);
        }
        return questionDTOs;
    }

    private QuestionDTO toQuestionDTO(Question question) {
        Long id = question.getId();
        String text = question.getText();
        boolean required = question.isRequired();
        boolean hasCheckbox = question.isHasCheckbox();

        QuestionDTO questionDTO;

        if (hasCheckbox) {
            CheckboxGroup checkboxGroup = question.getCheckboxGroup();
            CheckboxGroupDTO checkboxGroupDTO = toCheckboxGroupDTO(checkboxGroup);
            questionDTO = new QuestionDTO(id, text, required, hasCheckbox, checkboxGroupDTO);
        } else {
            questionDTO = new QuestionDTO(id, text, required, hasCheckbox);
        }
        return questionDTO;
    }

    private CheckboxGroupDTO toCheckboxGroupDTO(CheckboxGroup checkboxGroup) {
        Long id = checkboxGroup.getId();
        boolean multipleSelect = checkboxGroup.isMultipleSelect();
        int minSelect = checkboxGroup.getMinSelect();
        int maxSelect = checkboxGroup.getMaxSelect();

        List<Checkbox> checkboxes = checkboxGroup.getCheckboxes();
        List<CheckboxDTO> checkboxDTOs = toCheckboxDTOList(checkboxes);

        return new CheckboxGroupDTO(id, multipleSelect, minSelect, maxSelect, checkboxDTOs);
    }

    private List<CheckboxDTO> toCheckboxDTOList(List<Checkbox> checkboxes) {
        List<CheckboxDTO> checkboxDTOs = new ArrayList<>();
        for (Checkbox checkbox : checkboxes) {
            CheckboxDTO checkboxDTO = toCheckboxDTO(checkbox);
            checkboxDTOs.add(checkboxDTO);
        }
        return checkboxDTOs;
    }

    private CheckboxDTO toCheckboxDTO(Checkbox checkbox) {
        Long id = checkbox.getId();
        String text = checkbox.getText();
        boolean hasTextField = checkbox.isHasTextField();

        return new CheckboxDTO(id, text, hasTextField);
    }


    // Answer/-DTO:
    public List<AnswerDTO> toAnswerDTOList(List<Answer> answers) {
        List<AnswerDTO> answerDTOs = new ArrayList<>();
        for (Answer answer : answers) {
            AnswerDTO answerDTO = toAnswerDTO(answer);
            answerDTOs.add(answerDTO);
        }
        return answerDTOs;
    }

    private AnswerDTO toAnswerDTO(Answer answer) {
        Long id = answer.getId();
        String text = answer.getText();

        User user = answer.getUser();
        Long userId = user.getId();
        String userName = user.getName();

        AnswerDTO answerDTO;

        Checkbox checkbox = answer.getCheckbox();
        if (checkbox != null) {
            Long checkboxId = checkbox.getId();
            answerDTO = new AnswerDTO(id, text, userId, userName, checkboxId);
        } else {
            answerDTO = new AnswerDTO(id, text, userId, userName);
        }
        return answerDTO;
    }

    public List<Answer> toAnswerEntityList(List<AnswerDTO> answerDTOs) {
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : answerDTOs) {
            Answer answer = toAnswerEntity(answerDTO);
            answers.add(answer);
        }
        return answers;
    }

    public Answer toAnswerEntity(AnswerDTO answerDTO) {
        String text = answerDTO.getText();

        Answer answer = new Answer(text);

        Long userId = answerDTO.getUserId();
        if (userId != null) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                answer.setUser(user);
            }
        }

        Long questionId = answerDTO.getQuestionId();
        if (questionId != null) {
            Optional<Question> questionOptional = questionRepository.findById(questionId);
            if (questionOptional.isPresent()) {
                Question question = questionOptional.get();
                answer.setQuestion(question);
            }
        }

        Long checkboxId = answerDTO.getCheckboxId();
        if (checkboxId != null) {
            Optional<Checkbox> checkboxOptional = checkboxRepository.findById(checkboxId);
            if (checkboxOptional.isPresent()) {
                Checkbox checkbox = checkboxOptional.get();
                answer.setCheckbox(checkbox);
            }
        }

        return answer;
    }
}
