package com.d121toastmaster.bot.ToastTalker.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ToastTalkService {

    Map<String, Object> search(String query, String output, String inputLanguage, String tuuid) throws JsonProcessingException;
}
