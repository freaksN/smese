package smese.prototype.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class ResponseModel implements Serializable {

    private List<String> name = newArrayList();
    private List<String> abstracts = newArrayList();
    private List<String> keyaspects = newArrayList();
    private List<String> content = newArrayList();
}
