package iks.surveytool.controller;

import iks.surveytool.dtos.CompleteSurveyDTO;
import iks.surveytool.dtos.SurveyEndDateDTO;
import iks.surveytool.dtos.SurveyOverviewDTO;
import iks.surveytool.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
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

    @GetMapping("/users/{id}")
    @RolesAllowed("surveytool-user")
    public ResponseEntity<List<SurveyOverviewDTO>> findSurveysByUserId(@PathVariable Long id) {
        return surveyService.processSurveyByUserId(id);
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("surveytool-user")
    public ResponseEntity<?> deleteSurveyById(@PathVariable Long id) {
        // TODO: Check if user id is equal survey.userid
        return surveyService.processDeletionOfSurveyById(id);
    }

    @PutMapping
    @RolesAllowed("surveytool-user")
    public ResponseEntity<SurveyOverviewDTO> updateSurvey(@RequestBody CompleteSurveyDTO surveyDTO) {
        return surveyService.processUpdateOfSurvey(surveyDTO);
    }

    @PatchMapping
    @RolesAllowed("surveytool-user")
    public ResponseEntity<SurveyOverviewDTO> patchSurveyEndDate(@RequestBody SurveyEndDateDTO surveyEndDateDTO) {
        return surveyService.processPatchingSurveyEndDate(surveyEndDateDTO);
    }

}
