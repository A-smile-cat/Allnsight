package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.example.demo.entity.Articles;
import com.example.demo.entity.Model;
import com.example.demo.entity.UserInfo;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @PostMapping("/saveArticle")
    public Result saveArticle(@RequestBody Articles articles, HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");

        if(articles.getAuthor_id()!=user.getId()){
            return ResultFactory.buildFailResult("没有权限修改他人文章");
        }
        articles.setUser_id(user.getId());
        int f = articleService.saveArticle(articles);
        if(f == 1){
            return ResultFactory.buildSuccessResult("保存成功");
        }else{
            return ResultFactory.buildFailResult("保存失败");
        }
    }

    @GetMapping("/searchArticle")
    public Result searchDataset(@RequestParam(name = "title",defaultValue = "") String title,@RequestParam(name="author",defaultValue = "") String author,@RequestParam("user_id") int uid,HttpSession session){
        if (session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != uid){
            return ResultFactory.buildFailResult("无权限");
        }
        uid = user.getId();
        List<Articles> articleList = new ArrayList<>();

        articleList = articleService.query(uid,title,author);

        if(articleList.isEmpty()){
            return ResultFactory.buildFailResult("无查询结果");
        }else{
            return ResultFactory.buildSuccessResult("查询成功", JSON.toJSONString(articleList));
        }
    }

    @PutMapping("/updateStatus")
    public Result updateArticleStatus(@RequestBody Articles articles,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != articles.getUser_id()){
            return ResultFactory.buildFailResult("无权限");
        }
        articles.setUser_id(user.getId());
        if(!articles.getAuthor_id().equals(articles.getUser_id())){
            return ResultFactory.buildFailResult("没有权限修改他人文章的公开权限");
        }

        int f = articleService.updateStatus(articles);
        if(f == 1){
            return ResultFactory.buildSuccessResult("修改成功");
        }else{
            return ResultFactory.buildFailResult("修改失败");
        }
    }

    @DeleteMapping("/deleteArticle/{article_id}")
    public Result deleteArticle(@PathVariable("article_id") int article_id,@RequestParam("user_id") int user_id,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId() != user_id){
            return ResultFactory.buildFailResult("无权限");
        }
        user_id = user.getId();
        if(articleService.queryById(article_id,user_id)==null){
            return ResultFactory.buildFailResult("该文章不存在");
        }
        int f = articleService.deleteArticle(article_id,user_id);
        if(f == 1){
            return ResultFactory.buildSuccessResult("删除成功");
        }else{
            return ResultFactory.buildFailResult("删除失败");
        }
    }

    @GetMapping("/article/{id}")
    public Result queryArticle(@PathVariable("id") int article_id,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        Articles article = articleService.queryById(article_id);
        if(article==null){
            return ResultFactory.buildFailResult("文章不存在");
        }else{
            return ResultFactory.buildSuccessResult("查询成功",JSON.toJSONString(article));
        }
    }

    @PutMapping("/updateArticle")
    public Result updateArticle(@RequestBody Articles articles,HttpSession session){
        if(session == null || session.getAttribute("user") == null){
            return ResultFactory.buildFailResult("未登录");
        }
        UserInfo user = (UserInfo) session.getAttribute("user");
        if(user.getId()!=articles.getUser_id() || user.getId()!=articles.getAuthor_id()){
            return ResultFactory.buildFailResult("无权限");
        }
        if(articles.getArticle_id()==null){
            return ResultFactory.buildFailResult("文章id为空");
        }
        articles.setUser_id(user.getId());
        articles.setAuthor_id(user.getId());
        int f = articleService.updateArticle(articles);
        if(f == 1){
            return ResultFactory.buildSuccessResult("修改成功","");
        }else{
            return ResultFactory.buildFailResult("修改失败");
        }


    }

}
