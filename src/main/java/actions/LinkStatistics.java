package actions;

public class LinkStatistics {

    private String link;
    private Boolean validity;

    public LinkStatistics() {}

    public LinkStatistics(String link, Boolean validity) {
        this.link = link;
        this.validity = validity;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getValidity() {
        return validity;
    }

    public void setValidity(Boolean validity) {
        this.validity = validity;
    }

    public String validityToString() {
        if (this.validity) {
            return "valid";
        }
        return "invalid";
    }
}
