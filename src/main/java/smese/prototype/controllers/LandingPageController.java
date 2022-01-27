package smese.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import smese.prototype.repositories.SemanticLayerRepo;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static smese.prototype.utils.FormatUtils.getTopFamiliesFormatted;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LandingPageController {

    private SemanticLayerRepo semanticLayerRepo;

    @Autowired
    public LandingPageController(SemanticLayerRepo semanticLayerRepo) {
        this.semanticLayerRepo = requireNonNull(semanticLayerRepo);
    }

    @GetMapping("/")
    @ResponseBody
    private List<String> getTopFiveFamilies() {
        List<String> results = semanticLayerRepo.getTopFiveFamilies();
        return getTopFamiliesFormatted(results);
    }


}
