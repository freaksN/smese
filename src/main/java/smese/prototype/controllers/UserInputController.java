package smese.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smese.prototype.models.KeyAspectsModel;
import smese.prototype.models.SemanticLayerModel;
import smese.prototype.models.UserInputModel;
import smese.prototype.services.MetadataEnrichmentService;
import smese.prototype.services.UserInputValidationService;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static smese.prototype.utils.FormatUtils.getAnnotationsBySemanticLayerModel;

@RestController
@RequestMapping("/api/enrichment")
@CrossOrigin(origins = "*")
public class UserInputController {

    @Autowired
    private UserInputValidationService userInputValidationService;

    @Autowired
    private MetadataEnrichmentService metadataEnrichmentService;

    public UserInputController(UserInputValidationService userInputValidationService, MetadataEnrichmentService metadataEnrichmentService) {
        this.userInputValidationService = requireNonNull(userInputValidationService);
        this.metadataEnrichmentService = requireNonNull(metadataEnrichmentService);
    }

    @PostMapping(path = "/userInputData", consumes = MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<String> handleUserInput(@RequestParam("file") MultipartFile file, @RequestParam("keywords") String keywords, @RequestParam("keyAspects") List<String> keyAspects) throws Exception {
        UserInputModel userInputModel = new UserInputModel(file, keywords, keyAspects);
        KeyAspectsModel keyAspectsModel = new KeyAspectsModel();
        keyAspectsModel = userInputValidationService.handleUserInputKeywordsAndFile(userInputModel, keyAspectsModel, keywords);
        SemanticLayerModel semanticLayerModel = metadataEnrichmentService.generateMetadata(keyAspectsModel.getQueryResults(), keyAspectsModel);
        String resultingAnnotations = getAnnotationsBySemanticLayerModel(semanticLayerModel, keyAspectsModel);

        return new ResponseEntity<>(resultingAnnotations, HttpStatus.CREATED);
    }
}

