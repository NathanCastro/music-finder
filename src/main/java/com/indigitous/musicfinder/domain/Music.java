package com.indigitous.musicfinder.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Music.
 */
@Entity
@Table(name = "music")
@Document(indexName = "music")
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "title", length = 256, nullable = false)
    private String title;

    @Size(max = 40000)
    @Column(name = "lyrics", length = 40000)
    private String lyrics;

    @Size(max = 2048)
    @Pattern(regexp = ".+")
    @Column(name = "link", length = 2048)
    private String link;

    @ManyToMany
    @NotNull
    @JoinTable(name = "music_tags",
               joinColumns = @JoinColumn(name="music_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="tags_id", referencedColumnName="ID"))
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @NotNull
    private Artist artist;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Music title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLyrics() {
        return lyrics;
    }

    public Music lyrics(String lyrics) {
        this.lyrics = lyrics;
        return this;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLink() {
        return link;
    }

    public Music link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Music tags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }

    public Music addTags(Tag tag) {
        tags.add(tag);
        // tag.getMusics().add(this);
        return this;
    }

    public Music removeTags(Tag tag) {
        tags.remove(tag);
        // tag.getMusics().remove(this);
        return this;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Artist getArtist() {
        return artist;
    }

    public Music artist(Artist artist) {
        this.artist = artist;
        return this;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Music music = (Music) o;
        if(music.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, music.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Music{" +
            "id=" + id +
            ", title='" + title + "'" +
            ", lyrics='" + lyrics + "'" +
            ", link='" + link + "'" +
            '}';
    }
}
