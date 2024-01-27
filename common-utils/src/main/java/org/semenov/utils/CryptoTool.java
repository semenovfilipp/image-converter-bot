package org.semenov.utils;

import org.hashids.Hashids;
/*
 * Класс выполняет шифрование/расшифровку id из параметров запроса
 * Данный класс защищает id от прямого поиска в БД  пользователя
 */

public class CryptoTool {
    private final Hashids hashids;

    public CryptoTool(String salt) {
        var minHashLength = 10;
        this.hashids = new Hashids(salt, minHashLength);
    }

    // Делает хэш
    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    // Расшифровывает хэш в id
    public Long idOf(String value) {
        long[] result = hashids.decode(value);
        if (result != null && result.length > 0) {
            return result[0];
        }
        return null;
    }
}
