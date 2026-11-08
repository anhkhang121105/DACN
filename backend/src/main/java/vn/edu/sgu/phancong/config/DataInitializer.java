package vn.edu.sgu.phancong.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.NguoiDung;
import vn.edu.sgu.phancong.repository.NguoiDungRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Thay thế các hash mock bằng BCrypt thật cho mật khẩu demo '123456'
        for (NguoiDung nd : nguoiDungRepository.findAll()) {
            if (nd.getPasswordHash() == null || nd.getPasswordHash().contains("MOCK")) {
                nd.setPasswordHash(passwordEncoder.encode("123456"));
                nguoiDungRepository.save(nd);
            }
        }
    }
}
