package com.pppenger.microblog.service;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pppenger.microblog.domin.es.EsBlog;
import com.pppenger.microblog.repository.es.EsBlogRepository;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;


/**
 * EsBlog 服务.
 * 
 * @since 1.0.0 2017年4月12日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
@Service
public class EsBlogServiceImpl implements EsBlogService {
	@Autowired
	private EsBlogRepository esBlogRepository;
	@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	@Autowired
	private UserService userService;
	
	private static final Pageable TOP_5_PAGEABLE = new PageRequest(0, 5);
	private static final String EMPTY_KEYWORD = "";
	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.EsBlogService#removeEsBlog(java.lang.String)
	 */
	@Override
	public void removeEsBlog(String id) {
		esBlogRepository.delete(id);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.EsBlogService#updateEsBlog(com.waylau.spring.boot.blog.domain.es.EsBlog)
	 */
	@Override
	public EsBlog updateEsBlog(EsBlog esBlog) {
		return esBlogRepository.save(esBlog);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.EsBlogService#getEsBlogByBlogId(java.lang.Long)
	 */
	@Override
	public EsBlog getEsBlogByBlogId(Long blogId) {
		return esBlogRepository.findByBlogId(blogId);
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.EsBlogService#listNewestEsBlogs(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable) throws SearchParseException {
		Page<EsBlog> pages = null;
		Sort sort = new Sort(Direction.DESC,"createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}
 
		pages = esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContaining(keyword,keyword, pageable);
 
		return pages;
	}

	/* (non-Javadoc)
	 * @see com.waylau.spring.boot.blog.service.EsBlogService#listHotestEsBlogs(java.lang.String, org.springframework.data.domain.Pageable)
	 */
	@Override
	public Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable) throws SearchParseException{
 
		Sort sort = new Sort(Direction.DESC,"readSize","commentSize","voteSize","createTime"); 
		if (pageable.getSort() == null) {
			pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
		}

		return esBlogRepository.findDistinctEsBlogByTitleContainingOrSummaryContaining(keyword,keyword, pageable);
	}

	@Override
	public Page<EsBlog> listEsBlogs(Pageable pageable) {
		return esBlogRepository.findAll(pageable);
	}

}
