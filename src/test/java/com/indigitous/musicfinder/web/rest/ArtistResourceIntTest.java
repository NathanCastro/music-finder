package com.indigitous.musicfinder.web.rest;

import com.indigitous.musicfinder.MusicFinderApp;

import com.indigitous.musicfinder.domain.Artist;
import com.indigitous.musicfinder.repository.ArtistRepository;
import com.indigitous.musicfinder.service.ArtistService;
import com.indigitous.musicfinder.repository.search.ArtistSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ArtistResource REST controller.
 *
 * @see ArtistResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MusicFinderApp.class)
public class ArtistResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private ArtistRepository artistRepository;

    @Inject
    private ArtistService artistService;

    @Inject
    private ArtistSearchRepository artistSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restArtistMockMvc;

    private Artist artist;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ArtistResource artistResource = new ArtistResource();
        ReflectionTestUtils.setField(artistResource, "artistService", artistService);
        this.restArtistMockMvc = MockMvcBuilders.standaloneSetup(artistResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createEntity(EntityManager em) {
        Artist artist = new Artist()
                .name(DEFAULT_NAME);
        return artist;
    }

    @Before
    public void initTest() {
        artistSearchRepository.deleteAll();
        artist = createEntity(em);
    }

    @Test
    @Transactional
    public void createArtist() throws Exception {
        int databaseSizeBeforeCreate = artistRepository.findAll().size();

        // Create the Artist

        restArtistMockMvc.perform(post("/api/artists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(artist)))
                .andExpect(status().isCreated());

        // Validate the Artist in the database
        List<Artist> artists = artistRepository.findAll();
        assertThat(artists).hasSize(databaseSizeBeforeCreate + 1);
        Artist testArtist = artists.get(artists.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Artist in ElasticSearch
        Artist artistEs = artistSearchRepository.findOne(testArtist.getId());
        assertThat(artistEs).isEqualToComparingFieldByField(testArtist);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().size();
        // set the field null
        artist.setName(null);

        // Create the Artist, which fails.

        restArtistMockMvc.perform(post("/api/artists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(artist)))
                .andExpect(status().isBadRequest());

        List<Artist> artists = artistRepository.findAll();
        assertThat(artists).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllArtists() throws Exception {
        // Initialize the database
        artistRepository.saveAndFlush(artist);

        // Get all the artists
        restArtistMockMvc.perform(get("/api/artists?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(artist.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getArtist() throws Exception {
        // Initialize the database
        artistRepository.saveAndFlush(artist);

        // Get the artist
        restArtistMockMvc.perform(get("/api/artists/{id}", artist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(artist.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingArtist() throws Exception {
        // Get the artist
        restArtistMockMvc.perform(get("/api/artists/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateArtist() throws Exception {
        // Initialize the database
        artistService.save(artist);

        int databaseSizeBeforeUpdate = artistRepository.findAll().size();

        // Update the artist
        Artist updatedArtist = artistRepository.findOne(artist.getId());
        updatedArtist
                .name(UPDATED_NAME);

        restArtistMockMvc.perform(put("/api/artists")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedArtist)))
                .andExpect(status().isOk());

        // Validate the Artist in the database
        List<Artist> artists = artistRepository.findAll();
        assertThat(artists).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artists.get(artists.size() - 1);
        assertThat(testArtist.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Artist in ElasticSearch
        Artist artistEs = artistSearchRepository.findOne(testArtist.getId());
        assertThat(artistEs).isEqualToComparingFieldByField(testArtist);
    }

    @Test
    @Transactional
    public void deleteArtist() throws Exception {
        // Initialize the database
        artistService.save(artist);

        int databaseSizeBeforeDelete = artistRepository.findAll().size();

        // Get the artist
        restArtistMockMvc.perform(delete("/api/artists/{id}", artist.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean artistExistsInEs = artistSearchRepository.exists(artist.getId());
        assertThat(artistExistsInEs).isFalse();

        // Validate the database is empty
        List<Artist> artists = artistRepository.findAll();
        assertThat(artists).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchArtist() throws Exception {
        // Initialize the database
        artistService.save(artist);

        // Search the artist
        restArtistMockMvc.perform(get("/api/_search/artists?query=id:" + artist.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(artist.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
