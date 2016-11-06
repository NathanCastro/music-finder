package com.indigitous.musicfinder.service;

import com.indigitous.musicfinder.domain.Music;
import com.indigitous.musicfinder.repository.MusicRepository;
import com.indigitous.musicfinder.repository.search.MusicSearchRepository;
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
 * Service Implementation for managing Music.
 */
@Service
@Transactional
public class MusicService {

    private final Logger log = LoggerFactory.getLogger(MusicService.class);
    
    @Inject
    private MusicRepository musicRepository;

    @Inject
    private MusicSearchRepository musicSearchRepository;

    /**
     * Save a music.
     *
     * @param music the entity to save
     * @return the persisted entity
     */
    public Music save(Music music) {
        log.debug("Request to save Music : {}", music);
        Music result = musicRepository.save(music);
        musicSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the music.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Music> findAll(Pageable pageable) {
        log.debug("Request to get all Music");
        Page<Music> result = musicRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one music by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Music findOne(Long id) {
        log.debug("Request to get Music : {}", id);
        Music music = musicRepository.findOne(id);
        return music;
    }

    /**
     *  Delete the  music by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Music : {}", id);
        musicRepository.delete(id);
        musicSearchRepository.delete(id);
    }

    /**
     * Search for the music corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Music> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Music for query {}", query);
        Page<Music> result = musicSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
