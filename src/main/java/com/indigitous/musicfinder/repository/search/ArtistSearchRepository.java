package com.indigitous.musicfinder.repository.search;

import com.indigitous.musicfinder.domain.Artist;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Artist entity.
 */
public interface ArtistSearchRepository extends ElasticsearchRepository<Artist, Long> {
}
