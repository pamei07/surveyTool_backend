package iks.surveytool.utils.builder;

import iks.surveytool.entities.*;

public class AnswerBuilder {
    public Answer createAnswer(Long id, String text, User user, Question question, Checkbox checkbox) {
        Answer newAnswer = new Answer(text, "Default ParticipantName");
        setDefaults(id, user, question, newAnswer);
        newAnswer.setCheckbox(checkbox);
        return newAnswer;
    }

    public Answer createRankingAnswer(Long id, User user, Question question, Option option, Integer rank) {
        Answer newAnswer = new Answer(null, "Default ParticipantName");
        setDefaults(id, user, question, newAnswer);
        newAnswer.setOption(option);
        newAnswer.setRank(rank);
        return newAnswer;
    }

    private void setDefaults(Long id, User user, Question question, Answer newAnswer) {
        newAnswer.setId(id);
        newAnswer.setParticipantId("1234");
        newAnswer.setUser(user);
        newAnswer.setQuestion(question);
    }

}
