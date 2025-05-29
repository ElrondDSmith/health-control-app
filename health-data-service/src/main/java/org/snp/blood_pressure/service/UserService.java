package org.snp.blood_pressure.service;

import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.dto.TelegramUserDtoRq;
import org.snp.blood_pressure.entity.AppUser;
import org.snp.blood_pressure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUserIfNotExists(TelegramUserDtoRq telegramUserDtoRq) {
        boolean exists = userRepository.findUserByTelegramId(telegramUserDtoRq.getTgId()).isPresent();
        if (!exists) {
            AppUser appUser = new AppUser();
            appUser.setTgId(telegramUserDtoRq.getTgId());
            appUser.setName(telegramUserDtoRq.getUserName());
            userRepository.createUser(appUser);
        }
    }

    public Optional<Integer> findAppUserIdByTgId (String tgId) {
        return userRepository.findUserByTelegramId(tgId).map(AppUser::getId);
    }


}
