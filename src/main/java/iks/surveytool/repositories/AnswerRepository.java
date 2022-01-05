package iks.surveytool.repositories;

import iks.surveytool.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query(value = "SELECT a " +
            "FROM Answer a, Question q, QuestionGroup qg, Survey s " +
            "WHERE a.question = q.id " +
            "AND q.questionGroup = qg.id " +
            "AND qg.survey = s.id " +
            "AND s.id = :surveyId")
    List<Answer> findAnswersBySurvey_Id(@Param("surveyId") Long surveyId);

    List<Answer> findAllByQuestion_Id(Long questionId);
}