package com.pppenger.microblog.repository;

import com.pppenger.microblog.domin.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * Vote 仓库.
 *
 * @since 1.0.0 2017年4月9日
 * @author <a href="https://waylau.com">Way Lau</a> 
 */
public interface VoteRepository extends JpaRepository<Vote, Long>{

    @Query(value = "SELECT id,vote_size,username," +
            "(SELECT count(DISTINCT vote_size) FROM `user` AS b WHERE a.vote_size<b.vote_size)+1 AS rank " +
            "FROM `user` AS a  WHERE username=?1  ORDER BY rank;", nativeQuery=true)
    List<Object[]> selectRankByVoteSize(String username);
}
