package org.snp.blood_pressure.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.snp.blood_pressure.repository.PressureRecordRepository;
import org.snp.blood_pressure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class initService {
    private final PressureRecordRepository pressureRecordRepository;
    private final UserRepository userRepository;

    //@PostConstruct
    public void init() {
//        userRepository.createUser(new AppUser("Alex", "Alex@alex.com", "tg001"));
//        userRepository.createUser(new AppUser("Jules", "Jules@Jules.com", "tg002"));
//        userRepository.createUser(new AppUser("Sofi", "Sofi@sofi.com", "tg003"));

//        pressureRepository.save(new Pressure(1, 120, 80, 60));
//        pressureRepository.save(new Pressure(1, 128, 88, 68));
//        pressureRepository.save(new Pressure(1, 140, 100, 90));
//        pressureRepository.save(new Pressure(2, 110, 60, 70));
//        pressureRepository.save(new Pressure(2, 115, 75, 85));
//        pressureRepository.save(new Pressure(1, 120, 80, 60));
//        pressureRepository.save(new Pressure(1, 130, 90, 70));
//        System.out.println(userRepository.findAllAppUsers());
//        System.out.println(pressureRepository.findByUserId(1));
//        System.out.println("______________________");
//        System.out.println(pressureRepository.findByUserId(2));
//        System.out.println("______________________");
//        System.out.println("______________________");
//        System.out.println(pressureRepository.findByUserIdAndDate(1, LocalDate.parse("2025-05-15")));
//        System.out.println("______________________");
//        System.out.println(pressureRepository.findByUserIdAndDate(1, LocalDate.parse("2025-05-16")));
        System.out.println(pressureRecordRepository.findByUserIdAndLastNDays(1, 1));
        System.out.println("______________________");
        System.out.println("______________________");
        System.out.println(pressureRecordRepository.findByUserIdAndLastNDays(1, 2));
    }
}
