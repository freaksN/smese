package smese.prototype.services.impl;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import smese.prototype.models.KeyAspectsModel;
import smese.prototype.models.UserInputModel;
import smese.prototype.services.SPARQLQueryService;
import smese.prototype.services.UserInputValidationService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static smese.prototype.utils.FormatUtils.formatKeyAspects;

/**
 * This Service receives the user input data from the frontend and generates the key aspects and also assigns them to conceptual families
 * Finally, it calls the query service to run SPARQL queries against the DBPedia's endpoint for each of the generated key aspects
 */
@Service
@Lazy
public class UserInputValidationServiceImpl implements UserInputValidationService {

    @Autowired
    private SPARQLQueryService sparqlQueryService;


    /**
     * Handle the user input and generate the key aspects
     *
     * @param userInputModel  the available user input data
     * @param keyAspectsModel the key aspects model with all avaliable data
     * @return the updated and processed data within the keyAspectsModel
     * @throws Exception if the UserInputModel is empty
     */
    @Override
    public KeyAspectsModel handleUserInputKeywordsAndFile(UserInputModel userInputModel, KeyAspectsModel keyAspectsModel, String keywords) throws Exception {
        keyAspectsModel.setUploadedDataName(getBaseName(userInputModel.getFile().getOriginalFilename()));
        if (!userInputModel.getUserInputKeyAspects().isEmpty()) {
            for (String userInputKeyAspect : userInputModel.getUserInputKeyAspects()) {
                keyAspectsModel.getUserInputKeyAspects().add(userInputKeyAspect.toLowerCase());
                System.out.println("User input key aspects are: " + keyAspectsModel.getUserInputKeyAspects());
            }
            keyAspectsModel.getCombinedKeyAspects().addAll(keyAspectsModel.getUserInputKeyAspects());
        }

        keyAspectsModel.setFileContent(new String(userInputModel.getFile().getBytes(), StandardCharsets.UTF_8));
        semanticKeyAspectsGeneration(keyAspectsModel);
        getAspectsAndExecuteQuery(keyAspectsModel);

        return keyAspectsModel;
    }


    /**
     * Generates Key aspects and the appropriate conceptual families by utilization of the CoreNLP annotators
     *
     * @param keyAspectsModel the key aspects model with all the available data
     */
    private void semanticKeyAspectsGeneration(KeyAspectsModel keyAspectsModel) {
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create a document object and format the available text
        String text = keyAspectsModel.getFileContent();
        text.replace(", ", " ");
        CoreDocument document = new CoreDocument(text);
        // annnotate the document
        pipeline.annotate(document);


        Map<String, String> wordAndNer = newHashMap();
        Set<String> entityMentions = newHashSet();
        List<String> nerTags = newArrayList();
        List<CoreSentence> sentences = document.sentences();
        long start = System.currentTimeMillis();
        for (CoreSentence sentence : sentences) {
            for (CoreLabel token : sentence.tokens()) {
                String word = token.word();
                String ner = token.ner();
                if (isNotBlank(token.tag()) && (token.word().length() > 1 && token.ner().length() > 1)) {
                    wordAndNer.put(word, ner);
                    nerTags.add(token.ner());
                }
            }
            // Key Aspects
            for (CoreEntityMention entityMention : sentence.entityMentions()) {
                entityMentions.add(entityMention.text());
            }
        }
        addToKeyAspects(keyAspectsModel, entityMentions);
        keyAspectsModel.setConceptualFamilies(nerTags);
    }

    /**
     * Sets the values in the KeyaspectsModel
     *
     * @param keyAspectsModel the key aspects model with all avaiable data
     * @param entityMentions  the avaiable key aspects
     */
    private void addToKeyAspects(KeyAspectsModel keyAspectsModel, Set<String> entityMentions) {
        for (String entityMention : entityMentions) {
            if (!keyAspectsModel.getCombinedKeyAspects().contains(entityMention)) {
                keyAspectsModel.getCombinedKeyAspects().add(entityMention);
                keyAspectsModel.getGeneratedKeyAspects().add(entityMention);
            }
        }
    }


    /**
     * Passes the KeyAspectsModel to the following service for query generation and execution.
     * @param keyAspectsModel the key aspects model with all the available data
     * @throws Exception if no key aspects are avaiable
     */
    private void getAspectsAndExecuteQuery(KeyAspectsModel keyAspectsModel) throws Exception {
        if (keyAspectsModel.getCombinedKeyAspects().isEmpty()) {
            throw new Exception("No key aspects data available.");
        } else {
            sparqlQueryService.runQuery(formatKeyAspects(keyAspectsModel));
        }
    }


}
