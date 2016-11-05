package com.indigitous.musicfinder.service;

import com.indigitous.musicfinder.domain.Artist;
import com.indigitous.musicfinder.repository.ArtistRepository;
import com.indigitous.musicfinder.repository.search.ArtistSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Artist.
 */
@Service
@Transactional
public class ArtistService {

    private final Logger log = LoggerFactory.getLogger(ArtistService.class);
    
    @Inject
    private ArtistRepository artistRepository;

    @Inject
    private ArtistSearchRepository artistSearchRepository;

    /**
     * Save a artist.
     *
     * @param artist the entity to save
     * @return the persisted entity
     */
    public Artist save(Artist artist) {
        log.debug("Request to save Artist : {}", artist);
        Artist result = artistRepository.save(artist);
        artistSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the artists.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Artist> findAll(Pageable pageable) {
        log.debug("Request to get all Artists");
        Page<Artist> result = artistRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one artist by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Artist findOne(Long id) {
        log.debug("Request to get Artist : {}", id);
        Artist artist = artistRepository.findOne(id);
        return artist;
    }

    /**
     *  Delete the  artist by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Artist : {}", id);
        artistRepository.delete(id);
        artistSearchRepository.delete(id);
    }

    /**
     * Search for the artist corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Artist> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Artists for query {}", query);
        Page<Artist> result = artistSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
