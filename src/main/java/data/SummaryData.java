package data;

/**
 * Data class
 */
public class SummaryData {

    private String name;
    private String value;

    /**
     * Constructor of data object
     * @param name of statistic
     * @param value of statistic
     */
    public SummaryData(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Gets name of statistic
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets value of statistic
     * @return value
     */
    public String getValue() {
        return value;
    }
}
