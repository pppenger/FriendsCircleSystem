package com.pppenger.microblog.service;

import javax.transaction.Transactional;

import com.pppenger.microblog.domin.Comment;
import com.pppenger.microblog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

}
