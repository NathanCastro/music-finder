package com.indigitous.musicfinder.repository;

import com.indigitous.musicfinder.domain.Artist;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Artist entity.
 */
@SuppressWarnings("unused")
public interface ArtistRepository extends JpaRepository<Artist,Long> {

}
