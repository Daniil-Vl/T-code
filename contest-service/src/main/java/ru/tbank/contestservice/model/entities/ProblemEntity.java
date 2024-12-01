package ru.tbank.contestservice.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "problem")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemEntity {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Getter
    @Column(name = "title", nullable = false)
    private String title;

    @Getter
    @Setter
    @Column(name = "description", nullable = false)
    private String description;

    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private UserEntity owner;


    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ProblemEntity other)) {
            return false;
        }

        return id == other.id
                && title.equals(other.title)
                && description.equals(other.description)
                && owner.equals(other.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, owner);
    }

    @Override
    public String toString() {
        return "ProblemEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", owner=" + owner +
                '}';
    }
}
