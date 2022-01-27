package smese.prototype.services.impl;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import smese.prototype.models.KeyAspectsModel;
import smese.prototype.services.SPARQLQueryService;

import static org.apache.jena.query.QueryExecutionFactory.sparqlService;
import static org.apache.jena.query.ResultSetFormatter.asXMLString;

/**
 * This Service generates and executes the SPARQL queries against the DBPedia for each of the provided Key Aspects.
 * The results are saved in the queryResults  List as an XML-String
 */
@Service
@Lazy
public class SPARQLQueryServiceImpl implements SPARQLQueryService {

    /**
     * Runs a SPARQL query  against the DBPedia's SPARQL endpoint for each key aspect available within the keyAspectsModel
     *
     * @param keyAspectsModel the key aspects model with all avaliable data
     */
    @Override
    public void runQuery(KeyAspectsModel keyAspectsModel) {
        for (String keyAspect : keyAspectsModel.getCombinedKeyAspects()) {

            //Query
            String queryStr = "SELECT ?property ?value { <http://dbpedia.org/resource/" + keyAspect + "> ?property ?value . FILTER(!isLiteral(?value) || (LANG(?value) = '' || LANGMATCHES(LANG(?value), 'en')))}";
            Query query = QueryFactory.create(queryStr);

            // Iniate Remote execution.
            try (QueryExecution qexec = sparqlService("http://dbpedia.org/sparql", query)) {
                // Set timeout
                ((QueryEngineHTTP) qexec).addParam("timeout", "10000");

                // Execute and Handle results.
                ResultSet rs = qexec.execSelect();

                //Save results as an XMLString -- One can also use asText but it is badly formatted.
                keyAspectsModel.getQueryResults().put(keyAspect, asXMLString(rs));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
