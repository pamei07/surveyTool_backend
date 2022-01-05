package iks.surveytool.services;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.Answer;
import iks.surveytool.repositories.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    public ResponseEntity<List<AnswerDTO>> processAnswerDTOs(AnswerDTO[] answerDTOs) {
        List<AnswerDTO> answerDTOList = Arrays.asList(answerDTOs);
        List<Answer> answerList = mapAnswersToEntity(answerDTOList);
        if (validate(answerList)) {
            List<Answer> savedAnswers = saveAnswers(answerList);
            List<AnswerDTO> savedAnswerDTOs = mapAnswersToDTO(savedAnswers);
            return ResponseEntity.ok(savedAnswerDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    private List<Answer> mapAnswersToEntity(List<AnswerDTO> answerDTOList) {
        Type answerList = new TypeToken<List<Answer>>() {
        }.getType();
        return modelMapper.map(answerDTOList, answerList);
    }

    private boolean validate(List<Answer> answerList) {
        return answerList.stream().allMatch(Answer::validate);
    }

    private List<Answer> saveAnswers(List<Answer> answerList) {
        return answerRepository.saveAll(answerList);
    }

    public ResponseEntity<List<AnswerDTO>> processAnswersByQuestionId(Long questionId) {
        List<AnswerDTO> answerDTOs = mapAnswersToDTOByQuestionId(questionId);
        return ResponseEntity.ok(answerDTOs);
    }

    private List<AnswerDTO> mapAnswersToDTOByQuestionId(Long questionId) {
        List<Answer> answers = findAnswersByQuestionId(questionId);
        return mapAnswersToDTO(answers);
    }

    private List<AnswerDTO> mapAnswersToDTO(List<Answer> answers) {
        Type answerDTOList = new TypeToken<List<AnswerDTO>>() {
        }.getType();
        return modelMapper.map(answers, answerDTOList);
    }

    private List<Answer> findAnswersByQuestionId(Long questionId) {
        return answerRepository.findAllByQuestion_Id(questionId);
    }

    public ResponseEntity<List<AnswerDTO>> processAnswersBySurveyId(Long surveyId) {
        List<AnswerDTO> answerDTOs = mapAnswersToDTOBySurveyId(surveyId);
        return ResponseEntity.ok(answerDTOs);
    }

    private List<AnswerDTO> mapAnswersToDTOBySurveyId(Long surveyId) {
        List<Answer> answers = findAnswersBySurveyId(surveyId);
        return mapAnswersToDTO(answers);
    }

    private List<Answer> findAnswersBySurveyId(Long surveyId) {
        return answerRepository.findAnswersBySurvey_Id(surveyId);
    }
}
