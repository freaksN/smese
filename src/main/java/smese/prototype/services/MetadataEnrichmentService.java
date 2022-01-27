package smese.prototype.services;

import smese.prototype.models.KeyAspectsModel;
import smese.prototype.models.SemanticLayerModel;

import java.util.Map;

public interface MetadataEnrichmentService {

    SemanticLayerModel generateMetadata(Map<String, String> linkedDataResult, KeyAspectsModel keyAspectsModel);
}
