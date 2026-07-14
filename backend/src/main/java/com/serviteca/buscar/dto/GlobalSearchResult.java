package com.serviteca.buscar.dto;

public class GlobalSearchResult {

    private String type;
    private Long id;
    private String label;
    private String subtitle;
    private String url;

    public GlobalSearchResult() {}

    public GlobalSearchResult(String type, Long id, String label, String subtitle, String url) {
        this.type = type;
        this.id = id;
        this.label = label;
        this.subtitle = subtitle;
        this.url = url;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
