package com.d121toastmaster.bot.ToastTalker.configuration;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Configuration
public class ESConfiguration {

    @Value("${services.esindexer.host}")
    private String indexServiceHost;

    @Value("${services.esindexer.host.search}")
    private String indexServiceHostSearchEndpoint;

    // ES Config

    @Value("${es.user.index}")
    private String esUserIndex;

    @Value("${es.user.chathistory.index}")
    private String esUserChatHistoryIndex;

}

