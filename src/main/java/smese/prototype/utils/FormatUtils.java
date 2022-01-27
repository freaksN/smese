package smese.prototype.utils;

import smese.prototype.models.*;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.join;
import static org.apache.commons.lang3.EnumUtils.isValidEnum;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;
import static smese.prototype.models.RESPONSEFLAGS.*;

/**
 * This is a Utility class for bringing different kinds of data into the appropriate format either for storing into the database
 * or for displaying results back to the user.
 */
public class FormatUtils {

    /**
     * Prepare and display the first-20 enriched metadata annotations  in a format suitable for users to comprehend.
     *
     * @param semanticLayerModel the stored model
     * @param keyAspectsModel    the key aspects model with all the available data
     * @return formatted results
     */
    public static String getAnnotationsBySemanticLayerModel(SemanticLayerModel semanticLayerModel, KeyAspectsModel keyAspectsModel) {
        return getFormattedAnnotations(semanticLayerModel, keyAspectsModel);
    }


    /**
     * Gets the cokas and passes them together with the enriched metadata annotations for further processing
     *
     * @param semanticLayerModel the stored model
     * @param keyAspectsModel    the key aspects model with all the available data
     * @return the properly fo
     */
    private static String getFormattedAnnotations(SemanticLayerModel semanticLayerModel, KeyAspectsModel keyAspectsModel) {
        StringBuilder results = new StringBuilder();
        String cokas = join(", ", keyAspectsModel.getCombinedKeyAspects());
        results = getAbstract(semanticLayerModel);
        results.append("<p>For enrichment, some following key aspects were used: ").append("</p><p><b>").append(cokas).append("</b></p><br/>").append("<p>Among the first 20 results are the following:</p><p><b>");
        getAnnotations(semanticLayerModel.getMetadata(), results);

        return results.append("</b></p>").toString();
    }

    /**
     * Filters out unwanted/not easily comprehensible annotations
     *
     * @param annotations all the available annotations
     * @param results     the formatted results
     */
    private static void getAnnotations(List<String> annotations, StringBuilder results) {
        int count = 0;
        Set<String> removeDuplicates = newHashSet();
        removeDuplicates.addAll(annotations);
        for (String annotation : removeDuplicates) {
            if (isAlpha(annotation.substring(annotation.lastIndexOf("/") + 1)) && count < 21 && !annotation.contains("en") && !annotation.contains("target")) {
                if (annotation.matches("^[a-zA-Z0-9]+$") && annotation.length() > 3) {
                    results.append(annotation.substring(annotation.lastIndexOf("/") + 1)).append(count != 20 ? ", " : ".");
                    count++;
                }
            }
        }
    }

    /**
     * Gets the abstracts within the found results.
     *
     * @param semanticLayerModel the found result in the DB
     * @return
     */
    private static StringBuilder getAbstract(SemanticLayerModel semanticLayerModel) {
        StringBuilder entityAbstract = new StringBuilder();
        for (String propsAndVal : semanticLayerModel.getPropsAndVals()) {
            if (propsAndVal.contains("abstract")) {
                entityAbstract.append("<p><b>").append(substringBefore(propsAndVal, " : ")).append("</b>").append(" - ").append(substringAfter(propsAndVal, " ; ")).append("</p>");
            }
        }
        return entityAbstract;
    }

    /**
     * Format Key Aspects by removing whitespaces and replacing them with underscore.
     * This is one of the requirements of DBPedia to run the query with /resource.
     *
     * @param keyAspectsModel the key aspects model containing all of the processed data
     */
    public static KeyAspectsModel formatKeyAspects(KeyAspectsModel keyAspectsModel) {
        List<String> formatedKeyAspects = newArrayList();
        for (String keyAspect : keyAspectsModel.getCombinedKeyAspects()) {
            keyAspect = capitalizeFully(keyAspect);
            formatedKeyAspects.add(keyAspect.replace(" ", "_"));
        }
        keyAspectsModel.getCombinedKeyAspects().clear();
        keyAspectsModel.getCombinedKeyAspects().addAll(formatedKeyAspects);
        return keyAspectsModel;
    }

    /**
     * Gets and formats the found results based on the user input for the expert search.
     *
     * @param resultsMetadata found metadata results
     * @param resultsGEKA     found geka results
     * @param resultsUIKA     found UIKA results
     * @param resultsFamilies found families results
     * @return the response model containing all the found results for the expert search.
     */
    public static ResponseModel formatSearchResults(List<SemanticLayerModel> resultsMetadata, List<SemanticLayerModel> resultsGEKA, List<SemanticLayerModel> resultsUIKA, List<SemanticLayerModel> resultsFamilies) {
        ResponseModel responseModel = new ResponseModel();
        if (resultsMetadata != null && !resultsMetadata.isEmpty()) {
            addResultsToResponse(responseModel, resultsMetadata, METADATA);
        }
        if (resultsGEKA != null && !resultsGEKA.isEmpty()) {
            addResultsToResponse(responseModel, resultsGEKA, GEKA);
        }
        if (resultsUIKA != null && !resultsUIKA.isEmpty()) {
            addResultsToResponse(responseModel, resultsUIKA, UIKA);
        }
        if (resultsFamilies != null && !resultsFamilies.isEmpty()) {
            addResultsToResponse(responseModel, resultsFamilies, FAMILY);
        }
        return responseModel;
    }

    /**
     * Gets and formats the found results based on the user input for the normal/default search.
     *
     * @param resultingModel the found semantic layer model
     * @return the response model containg all the found results.
     */
    public static ResponseModel formatSearchResults(List<SemanticLayerModel> resultingModel) {
        ResponseModel responseModel = new ResponseModel();
        if (resultingModel != null && !resultingModel.isEmpty()) {
            addResultsToResponse(responseModel, resultingModel, null);
        }
        return responseModel;
    }

    /**
     * Adds the respective elements to the results's response based on the type of search with the help of a flag.
     *
     * @param responseModel  the response to the users.
     * @param resultingModel the found results
     * @param flag           flags for the found results' source
     */
    private static void addResultsToResponse(ResponseModel responseModel, List<SemanticLayerModel> resultingModel, RESPONSEFLAGS flag) {
        for (SemanticLayerModel result : resultingModel) {
            if (flag != null) {
                responseModel.getName().add(result.getName() + flag.getDescription());
            } else {
                responseModel.getName().add(result.getName());
            }
            addAbstractsToResponseModel(responseModel, result.getPropsAndVals());
            addOtherKeyAspectsToResponseModel(responseModel, result.getGeneratedKeyAspects());
            addDownloadFileToResponse(result, responseModel);
        }
    }


    /**
     * Helper method for adding some relevant key aspects to the response model.
     *
     * @param responseModel       the response model containing all found results
     * @param generatedKeyAspects the semantic layer's COKAs
     */
    private static void addOtherKeyAspectsToResponseModel(ResponseModel responseModel, List<String> generatedKeyAspects) {
        int count = 0;
        Set<String> removeDuplicates = newHashSet();
        removeDuplicates.addAll(generatedKeyAspects);
        for (String annotation : removeDuplicates) {
            if (isAlpha(annotation.substring(annotation.lastIndexOf("/") + 1)) && count < 21 && !annotation.contains("en") && !annotation.contains("target")) {
                if (annotation.matches("^[a-zA-Z0-9]+$")) {
                    responseModel.getKeyaspects().add(annotation.substring(annotation.lastIndexOf("/") + 1));
                    count++;
                }
            }
        }
    }

    /**
     * Adds the found abstracts to the found result's response.
     *
     * @param responseModel the response to the users.
     * @param propsAndVals  the properties and values of the LD results.
     */
    private static void addAbstractsToResponseModel(ResponseModel responseModel, List<String> propsAndVals) {
        for (String propsAndVal : propsAndVals) {
            if (propsAndVal.contains("abstract")) {
                responseModel.getAbstracts().add(substringAfter(propsAndVal, " ; "));
            }
        }
    }

    /**
     * Gets the top 5 most active families in a prorper format for the landing page.
     *
     * @param results the found  top 5 conceptual families
     * @return top 5 most active conceptual families
     */
    public static List<String> getTopFamiliesFormatted(List<String> results) {
        List<String> formattedResults = newArrayList();
        if (results != null && !results.isEmpty()) {
            for (String result : results) {
                String getFamily = substringBefore(result, ",");
                if (isValidEnum(FAMILIES.class, getFamily)) {
                    formattedResults.add(getFamily + ", " + FAMILIES.valueOf(getFamily).getDescription());
                }
            }
        }
        return formattedResults;
    }


    /**
     * Gets the metadata after the data has be processed and the metadata has been generated in the MetadataEnrichmentService.
     *
     * @param formatedAnnotations the enriched and initially formatted metadata annotations
     * @return the metadata annotation without its Ontology's URi
     */
    public static List<String> splitAndGetMetadata(List<String> formatedAnnotations) {
        List<String> metadata = newArrayList();
        for (String formatedAnnotation : formatedAnnotations) {
            metadata.add(substringAfter(formatedAnnotation, " ; "));
        }
        return metadata;
    }

    /**
     * Adds the download file to the found result's response.
     *
     * @param semanticLayerModel the results found in the database.
     * @param responseModel      the response to the users.
     */
    private static void addDownloadFileToResponse(SemanticLayerModel semanticLayerModel, ResponseModel responseModel) {
        responseModel.getContent().add(semanticLayerModel.getFileContent());
    }

}
