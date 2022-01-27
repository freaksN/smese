package smese.prototype.models;

import lombok.Getter;

@Getter
public enum FAMILIES {
    PERSON("People including fictional characters."),
    LOCATION("A particular place or position."),
    ORGANIZATION("Companies, corporations, organizations, agencies, etc."),
    MISC("A mixture of various things that are not usually connected with each other."),
    MONEY("Monetary values, including units."),
    NUMBER("Any kind of number such as 0, 1, 2 .. 9."),
    ORDINAL("Numbers such as 1st, 2nd, 3rd, Fifth, etc."),
    PERCENT("Percentage values such as 85%"),
    DATE("Absolute or relative dates or times of periods."),
    TIME("Normally times that are smaller than a day."),
    DURATION("The length of time a given enitity has lasted."),
    SET("Set story in a particular place or time, the events happen then or there, such as a 'novel is set in the years before the first world war'."),
    EMAIL("Recognized emails such as smese@gmail.com."),
    URL("Uniform resource locator or also known as a website address."),
    CITY("Regognized existing towns."),
    STATE_OR_PROVINCE("Large areas or parts of a country with its own government or different goverment laws, such as California, Saxony, Bavaria,Texas, etc."),
    COUNTRY("An area of land that has its own government, laws, nationality, army, etc."),
    NATIONALITY("Recognized citizenships of a particular nation."),
    RELIGION("Recognized religions."),
    TITLE("Recognized titles including job titles."),
    IDEOLOGY("A set of beliefs or principles, especially one on which a political system, party, or organization is based."),
    CRIMINAL_CHARGE("Formal accusation made by a governmental authority such as theft, indicment, murder, etc."),
    CAUSE_OF_DEATH("Recognized injury or disease that has lead to death such as accident, disease, homicide, etc."),
    SOCIAL_MEDIA("Facebook, Twitter, Instagram, etc.");

    FAMILIES(String description) {
        this.description = description;
    }

    private String description;
}
