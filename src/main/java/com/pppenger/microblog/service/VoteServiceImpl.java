package com.pppenger.microblog.service;

import javax.transaction.Transactional;

import com.pppenger.microblog.domin.Vote;
import com.pppenger.microblog.repository.VoteRepository;
import com.pppenger.microblog.vo.CollectionBlogVO;
import com.pppenger.microblog.vo.UserVoteSizeRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Vote 服务.
 * 
 * @since 1.0.0 2017年4月9日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class VoteServiceImpl implements VoteService {

	@Autowired
	private VoteRepository voteRepository;
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.VoteService#removeVote(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeVote(Long id) {
		voteRepository.delete(id);
	}
	@Override
	public Vote getVoteById(Long id) {
		return voteRepository.findOne(id);
	}


	@Override
	public UserVoteSizeRankVO selectRankByVoteSize(String username) {
		List<Object[]> objects = voteRepository.selectRankByVoteSize(username);
		UserVoteSizeRankVO userVoteSizeRankVO=null;
		for (Object[] ostr:objects){
			BigInteger a,b,d;
			String c = (String) ostr[2];
			a=(BigInteger)ostr[0];b=(BigInteger)ostr[1];d=(BigInteger)ostr[3];
			userVoteSizeRankVO= new UserVoteSizeRankVO(a.longValue(),b.longValue(),c,d.longValue());
		}
		return userVoteSizeRankVO;
	}

}
