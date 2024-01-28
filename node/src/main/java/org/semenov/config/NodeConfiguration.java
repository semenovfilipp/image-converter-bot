package org.semenov.config;

import org.semenov.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Этот конфигурационный класс создает bean CryptoTool
 * В качестве параметра передается salt - ключ для криптографической обработки
 * CryptoTool -  класс, реализующий шифрование/дешифрование
 */
@Configuration
public class NodeConfiguration {
    @Value("${salt}")
    private String salt;

    @Bean
    public CryptoTool getCryptoTool() {
        return new CryptoTool(salt);
    }
}
