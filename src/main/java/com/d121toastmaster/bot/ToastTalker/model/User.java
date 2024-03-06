package com.d121toastmaster.bot.ToastTalker.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Document(indexName = "user-index")
public class User {
    @Id
    private String uuid;
    private String username;
    private String language;
    private String jugalUuid;
    private List<Request> request;

}
