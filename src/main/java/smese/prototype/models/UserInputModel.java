package smese.prototype.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@AllArgsConstructor
public class UserInputModel {

    private MultipartFile file;
    private String keywords;
    private List<String> userInputKeyAspects = newArrayList();
}
