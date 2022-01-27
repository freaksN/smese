package smese.prototype.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smese.prototype.models.SemanticLayerModel;

import java.util.List;

@Repository
public interface SemanticLayerRepo extends JpaRepository<SemanticLayerModel, Long> {

    @Query(value = "SELECT DISTINCT sl.id, sl.name, sl.file_content FROM semantic_layer sl LEFT OUTER JOIN semantic_layer_metadata metadata ON sl.id=metadata.semantic_layer_id WHERE LOWER(metadata.metadata) LIKE %:searchTerm OR LOWER(sl.name) LIKE %:searchTerm", nativeQuery = true)
    List<SemanticLayerModel> findByMetadataNormal(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT DISTINCT sl.id, sl.name, sl.file_content FROM semantic_layer sl LEFT OUTER JOIN semantic_layer_metadata metadata ON sl.id=metadata.semantic_layer_id WHERE LOWER(metadata.metadata) = :searchTerm OR LOWER(sl.name) = :searchTerm", nativeQuery = true)
    List<SemanticLayerModel> findByMetadataExpert(@Param("searchTerm") String searchTerm);

    List<SemanticLayerModel> findByGeneratedKeyAspectsIgnoreCase(String searchTerm);

    List<SemanticLayerModel> findByUserInputKeyAspectsIgnoreCase(String searchTerm);

    List<SemanticLayerModel> findDistinctByConceptualFamilyIgnoreCase(String searchTerm);

    @Query(value = "SELECT families FROM SEMANTIC_LAYER_FAMILIES GROUP BY families ORDER BY count(families) DESC limit 5", nativeQuery = true)
    List<String> getTopFiveFamilies();

}
