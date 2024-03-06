package com.d121toastmaster.bot.ToastTalker.repository;

import com.d121toastmaster.bot.ToastTalker.model.UserChatHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ToastTalkChatRepository extends ElasticsearchRepository<UserChatHistory,String>{
}
