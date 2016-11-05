package com.indigitous.musicfinder.repository.search;

import com.indigitous.musicfinder.domain.Vote;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Vote entity.
 */
public interface VoteSearchRepository extends ElasticsearchRepository<Vote, Long> {
}
