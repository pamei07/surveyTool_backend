package iks.surveytool.mapping;

import iks.surveytool.dtos.AnswerDTO;
import iks.surveytool.entities.*;
import iks.surveytool.repositories.CheckboxRepository;
import iks.surveytool.repositories.OptionRepository;
import iks.surveytool.repositories.QuestionRepository;
import iks.surveytool.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerConverter {

    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final OptionRepository optionRepository;
    private final UserRepository userRepository;

    public Converter<AnswerDTO, Answer> toDAO = new AbstractConverter<AnswerDTO, Answer>() {
        @Override
        protected Answer convert(AnswerDTO source) {
            return toAnswerEntity(source);
        }


        private Answer toAnswerEntity(AnswerDTO answerDTO) {
            String text = answerDTO.getText();
            String participantName = answerDTO.getParticipantName();

            Answer answer = new Answer(text, participantName);

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

            Long optionId = answerDTO.getOptionId();
            if (optionId != null) {
                Optional<Option> optionOptional = optionRepository.findById(optionId);
                if (optionOptional.isPresent()) {
                    Option option = optionOptional.get();
                    answer.setOption(option);
                }

                answer.setRank(answerDTO.getRank());
            }

            return answer;
        }
    };
}
