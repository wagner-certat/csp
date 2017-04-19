package com.sastix.csp.commons.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "short_name",
        "name",
        "url",
        "host_organisation",
        "description",
        "country",
        "additional_countries",
        "established",
        "nis_team_types",
        "nis_sectors",
        "created",
        "status"
})
public class Team {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("short_name")
    private String shortName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("host_organisation")
    private String hostOrganisation;
    @JsonProperty("description")
    private String description;
    @JsonProperty("country")
    private String country;
    @JsonProperty("additional_countries")
    private List<Object> additionalCountries = null;
    @JsonProperty("established")
    private String established;
    @JsonProperty("nis_team_types")
    private List<String> nisTeamTypes = null;
    @JsonProperty("nis_sectors")
    private List<String> nisSectors = null;
    @JsonProperty("created")
    private String created;
    @JsonProperty("status")
    private Integer status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("short_name")
    public String getShortName() {
        return shortName;
    }

    @JsonProperty("short_name")
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("host_organisation")
    public String getHostOrganisation() {
        return hostOrganisation;
    }

    @JsonProperty("host_organisation")
    public void setHostOrganisation(String hostOrganisation) {
        this.hostOrganisation = hostOrganisation;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("country")
    public String getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty("additional_countries")
    public List<Object> getAdditionalCountries() {
        return additionalCountries;
    }

    @JsonProperty("additional_countries")
    public void setAdditionalCountries(List<Object> additionalCountries) {
        this.additionalCountries = additionalCountries;
    }

    @JsonProperty("established")
    public String getEstablished() {
        return established;
    }

    @JsonProperty("established")
    public void setEstablished(String established) {
        this.established = established;
    }

    @JsonProperty("nis_team_types")
    public List<String> getNisTeamTypes() {
        return nisTeamTypes;
    }

    @JsonProperty("nis_team_types")
    public void setNisTeamTypes(List<String> nisTeamTypes) {
        this.nisTeamTypes = nisTeamTypes;
    }

    @JsonProperty("nis_sectors")
    public List<String> getNisSectors() {
        return nisSectors;
    }

    @JsonProperty("nis_sectors")
    public void setNisSectors(List<String> nisSectors) {
        this.nisSectors = nisSectors;
    }

    @JsonProperty("created")
    public String getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(String created) {
        this.created = created;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "Team{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", hostOrganisation='" + hostOrganisation + '\'' +
                ", description='" + description + '\'' +
                ", country='" + country + '\'' +
                ", additionalCountries=" + additionalCountries +
                ", established='" + established + '\'' +
                ", nisTeamTypes=" + nisTeamTypes +
                ", nisSectors=" + nisSectors +
                ", created='" + created + '\'' +
                ", status=" + status +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
