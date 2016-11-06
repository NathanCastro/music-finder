package com.indigitous.musicfinder.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Vote.
 */
@Entity
@Table(name = "vote")
@Document(indexName = "vote")
public class Vote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @NotNull
    private Music music;

    @ManyToOne
    @NotNull
    private Tag tag;

    @ManyToOne
    @NotNull
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Music getMusic() {
        return music;
    }

    public Vote music(Music music) {
        this.music = music;
        return this;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public Tag getTag() {
        return tag;
    }

    public Vote tag(Tag tag) {
        this.tag = tag;
        return this;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public User getUser() {
        return user;
    }

    public Vote user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Vote vote = (Vote) o;
        if(vote.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, vote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Vote{" +
            "id=" + id +
            '}';
    }
}
