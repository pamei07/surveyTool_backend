package iks.surveytool.controller;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<SurveyOverviewDTO> createSurvey(@RequestBody CompleteSurveyDTO surveyDTO) {
        return surveyService.processSurveyDTO(surveyDTO);
    }

    @GetMapping(params = {"accessId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByAccessId(@RequestParam String accessId) {
        return surveyService.processSurveyByAccessId(accessId);
    }

    @GetMapping(params = {"participationId"})
    public ResponseEntity<SurveyOverviewDTO> findSurveyByParticipationId(@RequestParam String participationId) {
        return surveyService.processSurveyByParticipationId(participationId);
    }

    @GetMapping
    public ResponseEntity<List<SurveyOverviewDTO>> findOpenAccessSurveys() {
        return surveyService.processOpenAccessSurveys();
    }

    // TODO: Change parameter by which the Surveys are fetched
    @GetMapping("/users/{id}")
    public ResponseEntity<List<SurveyOverviewDTO>> findSurveysByUserId(@PathVariable Long id) {
        return surveyService.processSurveyByUserId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSurveyById(@PathVariable Long id) {
        return surveyService.processDeletionOfSurveyById(id);
    }
}
