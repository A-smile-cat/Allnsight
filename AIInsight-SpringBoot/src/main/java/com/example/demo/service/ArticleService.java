package com.example.demo.service;

import com.example.demo.entity.Articles;
import com.example.demo.entity.Model;
import com.example.demo.mapper.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ArticleService {
    @Autowired
    ArticleMapper articleMapper;

    public int saveArticle(Articles articles) {
        //获取当前时间戳
        articles.setArticle_date(new java.sql.Timestamp(System.currentTimeMillis()));
        return articleMapper.saveArticle(articles);
    }

    public List<Articles> query(int uid, String title, String author) {
        List<Articles> articlesList = articleMapper.query(uid,title,author);
        for (Articles article : articlesList ) {
            Timestamp timestamp = article.getArticle_date();
            LocalDateTime localDateTime = timestamp.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = localDateTime.format(formatter);
            article.setTime(formattedDate);
        }
        return articlesList;
    }

    public int updateStatus(Articles articles) {
        return articleMapper.updateStatus(articles);
    }

    public int deleteArticle(int article_id, int user_id) {
        return articleMapper.deleteArticle(article_id,user_id);
    }

    public Articles queryById(int article_id, int user_id) {
        Articles articles = articleMapper.queryById(article_id,user_id);
        return articles;
    }

    public Articles queryById(int article_id) {
        Articles article = articleMapper.queryByAId(article_id);
        Timestamp timestamp = article.getArticle_date();
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDateTime.format(formatter);
        article.setTime(formattedDate);
        return article;
    }

    public int updateArticle(Articles articles)
    {
        return articleMapper.updateArticle(articles);
    }
}
