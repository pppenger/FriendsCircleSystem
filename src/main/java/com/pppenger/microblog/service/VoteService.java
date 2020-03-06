package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Vote;
import com.pppenger.microblog.vo.UserVoteSizeRankVO;

/**
 * Vote 服务接口.
 * 
 * @since 1.0.0 2017年4月9日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface VoteService {
	/**
	 * 根据id获取 Vote
	 * @param id
	 * @return
	 */
	Vote getVoteById(Long id);
	/**
	 * 删除Vote
	 * @param id
	 * @return
	 */
	void removeVote(Long id);

	UserVoteSizeRankVO selectRankByVoteSize(String username);
}
