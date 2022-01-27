package smese.prototype.models;

import lombok.Getter;

@Getter
public enum RESPONSEFLAGS {

    METADATA(" by the generated enriched annotations"),
    GEKA(" by the generated key aspects"),
    UIKA(" by the user input"),
    FAMILY(" by  conceptually relevant families");

    RESPONSEFLAGS(String description) {
        this.description = description;
    }

    private String description;
}
