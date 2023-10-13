package com.kkbpro.terminal.Config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {

    /**
     * 欢迎语
     */
    @Value("${kk.welcome:}")
    private String welcome;

    /**
     * github源地址
     */
    @Value("${kk.source:}")
    private String source;


    /**
     * 艺术字标题
     */
    @Value("${kk.title:}")
    private String title;

}
