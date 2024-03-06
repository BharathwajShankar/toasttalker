package com.d121toastmaster.bot.ToastTalker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private String jugalUuid;
    private String searchText;
    private String chatResponse;
    private String timestamp;
}
