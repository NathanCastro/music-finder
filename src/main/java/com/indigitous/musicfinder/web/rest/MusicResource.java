package com.indigitous.musicfinder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.indigitous.musicfinder.domain.Music;
import com.indigitous.musicfinder.service.MusicService;
import com.indigitous.musicfinder.web.rest.util.HeaderUtil;
import com.indigitous.musicfinder.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Music.
 */
@RestController
@RequestMapping("/api")
public class MusicResource {

    private final Logger log = LoggerFactory.getLogger(MusicResource.class);
        
    @Inject
    private MusicService musicService;

    /**
     * POST  /music : Create a new music.
     *
     * @param music the music to create
     * @return the ResponseEntity with status 201 (Created) and with body the new music, or with status 400 (Bad Request) if the music has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/music")
    @Timed
    public ResponseEntity<Music> createMusic(@Valid @RequestBody Music music) throws URISyntaxException {
        log.debug("REST request to save Music : {}", music);
        if (music.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("music", "idexists", "A new music cannot already have an ID")).body(null);
        }
        Music result = musicService.save(music);
        return ResponseEntity.created(new URI("/api/music/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("music", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /music : Updates an existing music.
     *
     * @param music the music to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated music,
     * or with status 400 (Bad Request) if the music is not valid,
     * or with status 500 (Internal Server Error) if the music couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/music")
    @Timed
    public ResponseEntity<Music> updateMusic(@Valid @RequestBody Music music) throws URISyntaxException {
        log.debug("REST request to update Music : {}", music);
        if (music.getId() == null) {
            return createMusic(music);
        }
        Music result = musicService.save(music);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("music", music.getId().toString()))
            .body(result);
    }

    /**
     * GET  /music : get all the music.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of music in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/music")
    @Timed
    public ResponseEntity<List<Music>> getAllMusic(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Music");
        Page<Music> page = musicService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/music");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /music/:id : get the "id" music.
     *
     * @param id the id of the music to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the music, or with status 404 (Not Found)
     */
    @GetMapping("/music/{id}")
    @Timed
    public ResponseEntity<Music> getMusic(@PathVariable Long id) {
        log.debug("REST request to get Music : {}", id);
        Music music = musicService.findOne(id);
        return Optional.ofNullable(music)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /music/:id : delete the "id" music.
     *
     * @param id the id of the music to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/music/{id}")
    @Timed
    public ResponseEntity<Void> deleteMusic(@PathVariable Long id) {
        log.debug("REST request to delete Music : {}", id);
        musicService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("music", id.toString())).build();
    }

    /**
     * SEARCH  /_search/music?query=:query : search for the music corresponding
     * to the query.
     *
     * @param query the query of the music search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/music")
    @Timed
    public ResponseEntity<List<Music>> searchMusic(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Music for query {}", query);
        Page<Music> page = musicService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/music");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
