package smese.prototype.models;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@Table(name = "SEMANTIC_LAYER")
public class SemanticLayerModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Long id;

    @Column(columnDefinition = "VARCHAR2(1000 CHAR)")
    private String name;

    @Column(columnDefinition = "CLOB")
    private String fileContent;

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "SEMANTIC_LAYER_METADATA", joinColumns = @JoinColumn(name = "semantic_layer_id"), indexes = {@Index(name = "metadata_index", columnList = "METADATA")})
    @Column(name = "METADATA", columnDefinition = "VARCHAR2(8000  CHAR)")
    private List<String> metadata = newArrayList();

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "SEMANTIC_LAYER_PROPSANDVALS", joinColumns = @JoinColumn(name = "semantic_layer_id"), indexes = {@Index(name = "propsAndVals_index", columnList = "propsAndVals")})
    @Column(name = "propsAndVals", columnDefinition = "VARCHAR2(10000  CHAR)")
    private List<String> propsAndVals = newArrayList();


    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "SEMANTIC_LAYER_FAMILIES", joinColumns = @JoinColumn(name = "semantic_layer_id"), indexes = {@Index(name = "families_index", columnList = "FAMILIES")})
    @Column(name = "FAMILIES")
    private List<String> conceptualFamily = newArrayList();

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "SEMANTIC_LAYER_GEKA", joinColumns = @JoinColumn(name = "semantic_layer_id"), indexes = {@Index(name = "geka_index", columnList = "GEKA")})
    @Column(name = "GEKA", columnDefinition = "VARCHAR2(2000 CHAR)")
    private List<String> generatedKeyAspects = newArrayList();

    @ElementCollection(fetch = LAZY)
    @CollectionTable(name = "SEMANTIC_LAYER_UIKA", joinColumns = @JoinColumn(name = "semantic_layer_id"), indexes = {@Index(name = "uika_index", columnList = "UIKA")})
    @Column(name = "UIKA", columnDefinition = "VARCHAR2(4000 CHAR)")
    private List<String> userInputKeyAspects = newArrayList();

}
