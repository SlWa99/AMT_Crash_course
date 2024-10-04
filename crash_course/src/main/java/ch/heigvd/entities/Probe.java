package ch.heigvd.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;


@Entity
@Table(
        uniqueConstraints =  {
                @UniqueConstraint(columnNames = {"url"})
        }
)

public class Probe {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotBlank
    @URL
    private String url;

    public Probe() {
    }

    public Probe(Long id, String url) {
        this.id = id;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}