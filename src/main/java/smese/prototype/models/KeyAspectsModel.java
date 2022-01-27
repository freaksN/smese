package smese.prototype.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

@Data
@NoArgsConstructor
public class KeyAspectsModel {

    private String uploadedDataName;
    private String fileContent;
    private List<String> combinedKeyAspects = newArrayList();
    private List<String> userInputKeyAspects = newArrayList();
    private List<String> generatedKeyAspects = newArrayList();
    private List<String> conceptualFamilies = newArrayList();
    private Map<String, String> queryResults = newHashMap();
}
