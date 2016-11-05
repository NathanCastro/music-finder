package com.indigitous.musicfinder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.indigitous.musicfinder.domain.Artist;
import com.indigitous.musicfinder.service.ArtistService;
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
 * REST controller for managing Artist.
 */
@RestController
@RequestMapping("/api")
public class ArtistResource {

    private final Logger log = LoggerFactory.getLogger(ArtistResource.class);
        
    @Inject
    private ArtistService artistService;

    /**
     * POST  /artists : Create a new artist.
     *
     * @param artist the artist to create
     * @return the ResponseEntity with status 201 (Created) and with body the new artist, or with status 400 (Bad Request) if the artist has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/artists")
    @Timed
    public ResponseEntity<Artist> createArtist(@Valid @RequestBody Artist artist) throws URISyntaxException {
        log.debug("REST request to save Artist : {}", artist);
        if (artist.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("artist", "idexists", "A new artist cannot already have an ID")).body(null);
        }
        Artist result = artistService.save(artist);
        return ResponseEntity.created(new URI("/api/artists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("artist", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /artists : Updates an existing artist.
     *
     * @param artist the artist to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated artist,
     * or with status 400 (Bad Request) if the artist is not valid,
     * or with status 500 (Internal Server Error) if the artist couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/artists")
    @Timed
    public ResponseEntity<Artist> updateArtist(@Valid @RequestBody Artist artist) throws URISyntaxException {
        log.debug("REST request to update Artist : {}", artist);
        if (artist.getId() == null) {
            return createArtist(artist);
        }
        Artist result = artistService.save(artist);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("artist", artist.getId().toString()))
            .body(result);
    }

    /**
     * GET  /artists : get all the artists.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of artists in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/artists")
    @Timed
    public ResponseEntity<List<Artist>> getAllArtists(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Artists");
        Page<Artist> page = artistService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/artists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /artists/:id : get the "id" artist.
     *
     * @param id the id of the artist to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the artist, or with status 404 (Not Found)
     */
    @GetMapping("/artists/{id}")
    @Timed
    public ResponseEntity<Artist> getArtist(@PathVariable Long id) {
        log.debug("REST request to get Artist : {}", id);
        Artist artist = artistService.findOne(id);
        return Optional.ofNullable(artist)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /artists/:id : delete the "id" artist.
     *
     * @param id the id of the artist to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/artists/{id}")
    @Timed
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        log.debug("REST request to delete Artist : {}", id);
        artistService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("artist", id.toString())).build();
    }

    /**
     * SEARCH  /_search/artists?query=:query : search for the artist corresponding
     * to the query.
     *
     * @param query the query of the artist search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/artists")
    @Timed
    public ResponseEntity<List<Artist>> searchArtists(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Artists for query {}", query);
        Page<Artist> page = artistService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/artists");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
