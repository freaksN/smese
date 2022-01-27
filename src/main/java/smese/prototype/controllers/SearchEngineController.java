package smese.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smese.prototype.models.ResponseModel;
import smese.prototype.models.SemanticLayerModel;
import smese.prototype.repositories.SemanticLayerRepo;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static smese.prototype.utils.FormatUtils.formatSearchResults;

@RestController
@RequestMapping("/api/searchEngine")
@CrossOrigin(origins = "*")
public class SearchEngineController {

    @Autowired
    private SemanticLayerRepo semanticLayerRepo;

    @PostMapping(path = "/searchTerm", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseModel> handleUserInput(@RequestParam("searchTerm") String searchTerm, @RequestParam("expertSearch") boolean expertSearch) {
        ResponseModel responseModel;
        if (expertSearch) {
            List<SemanticLayerModel> resultsMetadata = semanticLayerRepo.findByMetadataExpert(searchTerm.toLowerCase());
            List<SemanticLayerModel> resultsGEKA = semanticLayerRepo.findByGeneratedKeyAspectsIgnoreCase(searchTerm);
            List<SemanticLayerModel> resultsUIKA = semanticLayerRepo.findByUserInputKeyAspectsIgnoreCase(searchTerm);
            List<SemanticLayerModel> resultsFamilies = semanticLayerRepo.findDistinctByConceptualFamilyIgnoreCase(searchTerm);
            responseModel = formatSearchResults(resultsMetadata, resultsGEKA, resultsUIKA, resultsFamilies);
        } else {
            List<SemanticLayerModel> resultsMetadata = semanticLayerRepo.findByMetadataNormal(searchTerm.toLowerCase());
            responseModel = formatSearchResults(resultsMetadata);
        }
        return new ResponseEntity<>(responseModel, HttpStatus.CREATED);
    }


}

