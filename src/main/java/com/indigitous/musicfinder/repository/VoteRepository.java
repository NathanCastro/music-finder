package com.indigitous.musicfinder.repository;

import com.indigitous.musicfinder.domain.Vote;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Vote entity.
 */
@SuppressWarnings("unused")
public interface VoteRepository extends JpaRepository<Vote,Long> {

    @Query("select vote from Vote vote where vote.user.login = ?#{principal.username}")
    List<Vote> findByUserIsCurrentUser();

}
