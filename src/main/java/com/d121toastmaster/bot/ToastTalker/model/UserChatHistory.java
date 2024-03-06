package com.d121toastmaster.bot.ToastTalker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Document(indexName = "user-chathistory")
@AllArgsConstructor
@NoArgsConstructor
public class UserChatHistory {

    @Id
    private String uuid;
    private String username;
    private String language;
    private List<History> history;

}
