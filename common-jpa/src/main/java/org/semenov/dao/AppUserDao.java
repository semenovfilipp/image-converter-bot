package org.semenov.dao;

import org.semenov.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserDao extends JpaRepository<AppUser,Long> {
    Optional<AppUser> findUserByTelegramUserId(Long id);
    Optional<AppUser> findUserById(Long id);
    Optional<AppUser> findUserByEmail(String email);
}
