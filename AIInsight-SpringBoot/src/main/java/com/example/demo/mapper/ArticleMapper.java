package com.example.demo.mapper;

import com.example.demo.entity.Articles;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ArticleMapper {

    int saveArticle(Articles articles);

    List<Articles> query(int uid, String title, String author);

    int updateStatus(Articles articles);

    int deleteArticle(int article_id, int user_id);

    Articles queryById(int article_id, int user_id);

    Articles queryByAId(int article_id);

    int updateArticle(Articles articles);
}
