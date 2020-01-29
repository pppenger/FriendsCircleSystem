package com.pppenger.microblog.service;

import com.pppenger.microblog.domin.Comment;
/**
 * Comment 服务接口.
 * 
 * @since 1.0.0 2017年4月9日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface CommentService {
	/**
	 * 根据id获取 Comment
	 * @param id
	 * @return
	 */
	Comment getCommentById(Long id);
	/**
	 * 删除评论
	 * @param id
	 * @return
	 */
	void removeComment(Long id);
	/**
	 * 更新评论——原先的删除功能变成修改内容，内容设置成了系统默认值
	 * @param id
	 * @return
	 */
	void updateComment(Long id);

	/**
	 * 点赞
	 * @param commentId
	 * @return
	 */
	Long createVote(Long commentId);

	/**
	 * 取消点赞
	 * @param commentId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long commentId, Long voteId);
}
