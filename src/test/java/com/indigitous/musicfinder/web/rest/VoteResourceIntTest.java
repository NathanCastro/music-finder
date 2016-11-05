package com.indigitous.musicfinder.web.rest;

import com.indigitous.musicfinder.MusicFinderApp;

import com.indigitous.musicfinder.domain.Vote;
import com.indigitous.musicfinder.domain.Music;
import com.indigitous.musicfinder.domain.Tag;
import com.indigitous.musicfinder.domain.User;
import com.indigitous.musicfinder.repository.VoteRepository;
import com.indigitous.musicfinder.service.VoteService;
import com.indigitous.musicfinder.repository.search.VoteSearchRepository;

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
 * Test class for the VoteResource REST controller.
 *
 * @see VoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MusicFinderApp.class)
public class VoteResourceIntTest {

    @Inject
    private VoteRepository voteRepository;

    @Inject
    private VoteService voteService;

    @Inject
    private VoteSearchRepository voteSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restVoteMockMvc;

    private Vote vote;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        VoteResource voteResource = new VoteResource();
        ReflectionTestUtils.setField(voteResource, "voteService", voteService);
        this.restVoteMockMvc = MockMvcBuilders.standaloneSetup(voteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vote createEntity(EntityManager em) {
        Vote vote = new Vote();
        // Add required entity
        Music music = MusicResourceIntTest.createEntity(em);
        em.persist(music);
        em.flush();
        vote.setMusic(music);
        // Add required entity
        Tag tag = TagResourceIntTest.createEntity(em);
        em.persist(tag);
        em.flush();
        vote.setTag(tag);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        vote.setUser(user);
        return vote;
    }

    @Before
    public void initTest() {
        voteSearchRepository.deleteAll();
        vote = createEntity(em);
    }

    @Test
    @Transactional
    public void createVote() throws Exception {
        int databaseSizeBeforeCreate = voteRepository.findAll().size();

        // Create the Vote

        restVoteMockMvc.perform(post("/api/votes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(vote)))
                .andExpect(status().isCreated());

        // Validate the Vote in the database
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeCreate + 1);
        Vote testVote = votes.get(votes.size() - 1);

        // Validate the Vote in ElasticSearch
        Vote voteEs = voteSearchRepository.findOne(testVote.getId());
        assertThat(voteEs).isEqualToComparingFieldByField(testVote);
    }

    @Test
    @Transactional
    public void getAllVotes() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get all the votes
        restVoteMockMvc.perform(get("/api/votes?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())));
    }

    @Test
    @Transactional
    public void getVote() throws Exception {
        // Initialize the database
        voteRepository.saveAndFlush(vote);

        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(vote.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingVote() throws Exception {
        // Get the vote
        restVoteMockMvc.perform(get("/api/votes/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeUpdate = voteRepository.findAll().size();

        // Update the vote
        Vote updatedVote = voteRepository.findOne(vote.getId());

        restVoteMockMvc.perform(put("/api/votes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedVote)))
                .andExpect(status().isOk());

        // Validate the Vote in the database
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeUpdate);
        Vote testVote = votes.get(votes.size() - 1);

        // Validate the Vote in ElasticSearch
        Vote voteEs = voteSearchRepository.findOne(testVote.getId());
        assertThat(voteEs).isEqualToComparingFieldByField(testVote);
    }

    @Test
    @Transactional
    public void deleteVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        int databaseSizeBeforeDelete = voteRepository.findAll().size();

        // Get the vote
        restVoteMockMvc.perform(delete("/api/votes/{id}", vote.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean voteExistsInEs = voteSearchRepository.exists(vote.getId());
        assertThat(voteExistsInEs).isFalse();

        // Validate the database is empty
        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchVote() throws Exception {
        // Initialize the database
        voteService.save(vote);

        // Search the vote
        restVoteMockMvc.perform(get("/api/_search/votes?query=id:" + vote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(vote.getId().intValue())));
    }
}
