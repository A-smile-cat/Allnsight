package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Articles {
    private Integer article_id;
    private Integer author_id;
    private String title;
    private String content_html;
    private String content_md;
    private Boolean is_public;
    private String abstractt;
    private Timestamp article_date;

    @Transient
    private String time;
    @Transient
    private String username;

    @Transient
    private Integer user_id;
}
