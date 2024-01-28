package org.semenov.service.impl;

import lombok.RequiredArgsConstructor;
import org.semenov.dao.AppUserDao;
import org.semenov.service.UserActivationService;
import org.semenov.utils.CryptoTool;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserDao appUserDao;
    private final CryptoTool cryptoTool;
    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserDao.findById(userId);
        if (optional.isPresent()){
            var user = optional.get();
            user.setIsActive(true);
            appUserDao.save(user);
            return true;
        }
        return false;
    }
}
