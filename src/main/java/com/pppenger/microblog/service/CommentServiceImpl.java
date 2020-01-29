package com.pppenger.microblog.service;

import javax.transaction.Transactional;

import com.pppenger.microblog.domin.Comment;
import com.pppenger.microblog.domin.User;
import com.pppenger.microblog.domin.Vote;
import com.pppenger.microblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Comment 服务.
 * 
 * @since 1.0.0 2017年4月9日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.CommentService#removeComment(java.lang.Long)
	 */
	@Override
	@Transactional
	public void removeComment(Long id) {
		commentRepository.delete(id);
	}

	@Override
	@Transactional
	public void updateComment(Long id) {
		Comment comment = commentRepository.getOne(id);
		comment.setContent("[系统提示：改评论内容已被删除！]");
		commentRepository.save(comment);
	}

	@Override
	public Comment getCommentById(Long id) {
		return commentRepository.findOne(id);
	}


	@Override
	public Long createVote(Long commentId) {
		Comment originalComment = commentRepository.findOne(commentId);
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Vote newvote = new Vote(user);
		boolean isExist = originalComment.addVote(newvote);
		if (isExist) {
			throw new IllegalArgumentException("该用户已经点过赞了");
		}

		commentRepository.save(originalComment);
		Long voteId=0L;
		for (Vote vote : originalComment.getVotes()){
			if (vote.getUser().getUsername().equals(user.getUsername())){
				voteId = vote.getId();
				break;
			}
		}
		return voteId;
		//return commentRepository.save(originalComment);
	}

	@Override
	public void removeVote(Long commentId, Long voteId) {
		Comment originalComment = commentRepository.findOne(commentId);
		originalComment.removeVote(voteId);
		commentRepository.save(originalComment);
	}
}
