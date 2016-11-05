package com.indigitous.musicfinder.service;

import com.indigitous.musicfinder.domain.Vote;
import com.indigitous.musicfinder.repository.VoteRepository;
import com.indigitous.musicfinder.repository.search.VoteSearchRepository;
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
 * Service Implementation for managing Vote.
 */
@Service
@Transactional
public class VoteService {

    private final Logger log = LoggerFactory.getLogger(VoteService.class);
    
    @Inject
    private VoteRepository voteRepository;

    @Inject
    private VoteSearchRepository voteSearchRepository;

    /**
     * Save a vote.
     *
     * @param vote the entity to save
     * @return the persisted entity
     */
    public Vote save(Vote vote) {
        log.debug("Request to save Vote : {}", vote);
        Vote result = voteRepository.save(vote);
        voteSearchRepository.save(result);
        return result;
    }

    /**
     *  Get all the votes.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Vote> findAll(Pageable pageable) {
        log.debug("Request to get all Votes");
        Page<Vote> result = voteRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one vote by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Vote findOne(Long id) {
        log.debug("Request to get Vote : {}", id);
        Vote vote = voteRepository.findOne(id);
        return vote;
    }

    /**
     *  Delete the  vote by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Vote : {}", id);
        voteRepository.delete(id);
        voteSearchRepository.delete(id);
    }

    /**
     * Search for the vote corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Vote> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Votes for query {}", query);
        Page<Vote> result = voteSearchRepository.search(queryStringQuery(query), pageable);
        return result;
    }
}
