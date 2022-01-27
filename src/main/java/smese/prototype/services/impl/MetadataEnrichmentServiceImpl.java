package smese.prototype.services.impl;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import smese.prototype.models.KeyAspectsModel;
import smese.prototype.models.SemanticLayerModel;
import smese.prototype.repositories.SemanticLayerRepo;
import smese.prototype.services.MetadataEnrichmentService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static com.github.wnameless.json.flattener.JsonFlattener.flattenAsMap;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.substringBefore;
import static smese.prototype.utils.FormatUtils.splitAndGetMetadata;


/**
 * This Service receives the linked data results and generates the metadata enrichment annotations.
 * These Annotations are then stored in the knowledge base.
 */
@Service
@Lazy
public class MetadataEnrichmentServiceImpl implements MetadataEnrichmentService {

    @Autowired
    private SemanticLayerRepo semanticLayerRepo;
    private int interlinkingDegree;

    /**
     * Formats and stores annotations for unique elements in the semantic layer
     *
     * @param linkedDataResult the resulting Linked Data from the SPARQL query
     * @param keyAspectsModel  the model containing all the necessary information for families, key aspects, etc.
     */
    @Override
    @Transactional
    public SemanticLayerModel generateMetadata(Map<String, String> linkedDataResult, KeyAspectsModel keyAspectsModel) {
        MultiValuedMap<String, String> propertiesAndValues = new ArrayListValuedHashMap<>();
        interlinkingDegree = 0;
        List<String> propsAndValsSimplified = newArrayList();
        List<String> formatedAnnotations = formatAnnotations(linkedDataResult, propertiesAndValues, propsAndValsSimplified);
        List<String> filteredMetadata = splitAndGetMetadata(formatedAnnotations);
        SemanticLayerModel semanticLayerModel = prepareAndSetSemanticLayerModel(formatedAnnotations, filteredMetadata, keyAspectsModel);

        //store in the database
        semanticLayerRepo.saveAndFlush(semanticLayerModel);
        return semanticLayerModel;
    }


    /**
     * Creates new SemanticLayer object and prepares it for storing by setting  all the required and processed values.
     *
     * @param formatedAnnotations the metadata annotations to be saved after they are properly processed
     * @param keyAspectsModel     all extracted and generated keywords as well as key aspects
     * @return the semantic layer which is going to be saved into the knowledge database
     */
    private SemanticLayerModel prepareAndSetSemanticLayerModel(List<String> formatedAnnotations, List<String> filteredMetadata, KeyAspectsModel keyAspectsModel) {
        SemanticLayerModel semanticLayerModel = new SemanticLayerModel();
        semanticLayerModel.setName(keyAspectsModel.getUploadedDataName());
        semanticLayerModel.setMetadata(filteredMetadata);
        semanticLayerModel.setFileContent(keyAspectsModel.getFileContent());
        semanticLayerModel.setPropsAndVals(formatedAnnotations);
        semanticLayerModel.setConceptualFamily(keyAspectsModel.getConceptualFamilies());
        semanticLayerModel.setUserInputKeyAspects(keyAspectsModel.getUserInputKeyAspects());
        semanticLayerModel.setGeneratedKeyAspects(keyAspectsModel.getGeneratedKeyAspects());
        return semanticLayerModel;
    }

    /**
     * Pre-processes the received SPARQL - XML - Result into an  appropriate format suitable for database storing.
     * The properties of each query's results could have multiple duplicates such as wikiLinks or sameAs, etc. However, their values are unique.
     *
     * @param linkedDataResult       the LD - XML String results mapped to a java Map.
     * @param propertiesAndValues    MultivaluedMap containing the values for each property
     * @param propsAndValsSimplified simplified version of the multivalued map as a List
     * @return the simplified version of the multivalued map as a List containing the results format as a (keyAspect) : property ; value.
     */
    private List<String> formatAnnotations(Map<String, String> linkedDataResult, MultiValuedMap<String, String> propertiesAndValues, List<String> propsAndValsSimplified) {
        for (Map.Entry<String, String> stringStringEntry : linkedDataResult.entrySet()) {
            extractValuesFromXMLResult(stringStringEntry.getValue(), stringStringEntry.getKey(), propertiesAndValues, propsAndValsSimplified);
        }
        return propsAndValsSimplified;
    }

    /**
     * Transform the XML into JSONObject for simpler processing of the data
     *
     * @param xml                    LD results
     * @param keyAspect              the current key aspect
     * @param propertiesAndValues    MultivaluedMap containing the values for each property
     * @param propsAndValsSimplified simplified version of the multivalued map as a List
     */
    private void extractValuesFromXMLResult(String xml, String keyAspect, MultiValuedMap<String, String> propertiesAndValues, List<String> propsAndValsSimplified) {
        JSONObject sparqlXmlResultsAvailable = XML.toJSONObject(xml).getJSONObject("sparql");
        if (!sparqlXmlResultsAvailable.optString("results").equals("")) {
            String sparqlXmlResults = sparqlXmlResultsAvailable.getJSONObject("results").toString();
            extractPropertiesAndValues(flattenAsMap(sparqlXmlResults), keyAspect, propertiesAndValues, propsAndValsSimplified);
        }
    }

    /**
     * This method extracts all properties and values from the chaotic flattened map and brings them into order inside a MultiValuedMap and the simplified List version.
     *
     * @param flattenJson            the flatten json map of all LD results
     * @param keyAspect              the current key aspect
     * @param propertiesAndValues    MultivaluedMap containing the values for each property
     * @param propsAndValsSimplified simplified version of the multivalued map as a List
     */
    private void extractPropertiesAndValues(Map<String, Object> flattenJson, String keyAspect, MultiValuedMap<String, String> propertiesAndValues, List<String> propsAndValsSimplified) {
        List<String> properties = newArrayList();
        List<String> values = newArrayList();
        List<String> keyList = newArrayList(flattenJson.keySet());

        for (int i = 0; i <= keyList.size() - 1; i++) {
            if (keyList.get(i).contains("binding[0].uri")) {
                properties.add(keyList.get(i));
            }
            if (keyList.get(i).contains("binding[1]") && !keyList.get(i).contains("binding[1].name")) {
                values.add(keyList.get(i));
            }
        }
        for (Map.Entry<String, String> entry : propertiesAndValues.entries()) {
            if (entry.getKey().contains("sameAs")) {
                interlinkingDegree += 1;
            }
        }


        propertiesAndValuesToMap(propertiesAndValues, properties, values, flattenJson, propsAndValsSimplified, keyAspect);
    }


    /**
     * Puts the extracted properties and values into MultiValuedMap as well as into the simplified List version.
     *
     * @param propertiesAndValues    MultiValuedMap with all unique properties and their values
     * @param keys                   all available 'properties' of the LD results
     * @param values                 all available 'values' of the LD results
     * @param flattenJson            the flatten json map of all LD results
     * @param propsAndValsSimplified simplified version of the multivalued map as a List
     * @param keyAspect              the current key aspect
     * @return the simplified version of the multivalued map as a List containing the results format as a (keyAspect) : property ; value. Additionally the properties and values are also put in the MultiValuedMap for a potential future use.
     */
    private List<String> propertiesAndValuesToMap(MultiValuedMap<String, String> propertiesAndValues, List<String> keys, List<String> values, Map<String, Object> flattenJson, List<String> propsAndValsSimplified, String keyAspect) {
        for (String key : keys) {
            for (String value : values) {
                if (substringBefore(key, "binding").equals(substringBefore(value, "binding"))) {
                    if (!flattenJson.get(value).toString().equals("en")) {
                        String propAndVal = "(" + keyAspect + ") : " + flattenJson.get(key).toString() + " ; " + flattenJson.get(value).toString();
                        propsAndValsSimplified.add(propAndVal);
                    }
                    propertiesAndValues.put(flattenJson.get(key).toString(), flattenJson.get(value).toString());
                }
            }
        }
        return propsAndValsSimplified;
    }

}
