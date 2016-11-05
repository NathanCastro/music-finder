package com.indigitous.musicfinder.repository.search;

import com.indigitous.musicfinder.domain.Music;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Music entity.
 */
public interface MusicSearchRepository extends ElasticsearchRepository<Music, Long> {
}
