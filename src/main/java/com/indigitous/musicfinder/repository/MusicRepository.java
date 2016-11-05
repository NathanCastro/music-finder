package com.indigitous.musicfinder.repository;

import com.indigitous.musicfinder.domain.Music;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Music entity.
 */
@SuppressWarnings("unused")
public interface MusicRepository extends JpaRepository<Music,Long> {

    @Query("select distinct music from Music music left join fetch music.tags")
    List<Music> findAllWithEagerRelationships();

    @Query("select music from Music music left join fetch music.tags where music.id =:id")
    Music findOneWithEagerRelationships(@Param("id") Long id);

}
