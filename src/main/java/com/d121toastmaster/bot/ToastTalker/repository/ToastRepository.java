package com.d121toastmaster.bot.ToastTalker.repository;

import com.d121toastmaster.bot.ToastTalker.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ToastRepository extends ElasticsearchRepository<User,String>{
}
