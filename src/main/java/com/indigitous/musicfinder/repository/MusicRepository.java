package com.indigitous.musicfinder.repository;

import com.indigitous.musicfinder.domain.Music;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Music entity.
 */
@SuppressWarnings("unused")
public interface MusicRepository extends JpaRepository<Music,Long> {

}
