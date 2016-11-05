package com.indigitous.musicfinder.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.indigitous.musicfinder.domain.Vote;
import com.indigitous.musicfinder.service.VoteService;
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
 * REST controller for managing Vote.
 */
@RestController
@RequestMapping("/api")
public class VoteResource {

    private final Logger log = LoggerFactory.getLogger(VoteResource.class);
        
    @Inject
    private VoteService voteService;

    /**
     * POST  /votes : Create a new vote.
     *
     * @param vote the vote to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vote, or with status 400 (Bad Request) if the vote has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/votes")
    @Timed
    public ResponseEntity<Vote> createVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to save Vote : {}", vote);
        if (vote.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("vote", "idexists", "A new vote cannot already have an ID")).body(null);
        }
        Vote result = voteService.save(vote);
        return ResponseEntity.created(new URI("/api/votes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("vote", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /votes : Updates an existing vote.
     *
     * @param vote the vote to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vote,
     * or with status 400 (Bad Request) if the vote is not valid,
     * or with status 500 (Internal Server Error) if the vote couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/votes")
    @Timed
    public ResponseEntity<Vote> updateVote(@Valid @RequestBody Vote vote) throws URISyntaxException {
        log.debug("REST request to update Vote : {}", vote);
        if (vote.getId() == null) {
            return createVote(vote);
        }
        Vote result = voteService.save(vote);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("vote", vote.getId().toString()))
            .body(result);
    }

    /**
     * GET  /votes : get all the votes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of votes in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/votes")
    @Timed
    public ResponseEntity<List<Vote>> getAllVotes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Votes");
        Page<Vote> page = voteService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/votes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /votes/:id : get the "id" vote.
     *
     * @param id the id of the vote to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vote, or with status 404 (Not Found)
     */
    @GetMapping("/votes/{id}")
    @Timed
    public ResponseEntity<Vote> getVote(@PathVariable Long id) {
        log.debug("REST request to get Vote : {}", id);
        Vote vote = voteService.findOne(id);
        return Optional.ofNullable(vote)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /votes/:id : delete the "id" vote.
     *
     * @param id the id of the vote to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/votes/{id}")
    @Timed
    public ResponseEntity<Void> deleteVote(@PathVariable Long id) {
        log.debug("REST request to delete Vote : {}", id);
        voteService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("vote", id.toString())).build();
    }

    /**
     * SEARCH  /_search/votes?query=:query : search for the vote corresponding
     * to the query.
     *
     * @param query the query of the vote search 
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/_search/votes")
    @Timed
    public ResponseEntity<List<Vote>> searchVotes(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Votes for query {}", query);
        Page<Vote> page = voteService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/votes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
