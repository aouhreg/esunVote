package com.vote.common.config;

import com.vote.entity.AdminUser;
import com.vote.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(AdminUserRepository adminUserRepository,
                         PasswordEncoder passwordEncoder) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    AdminUser existing = adminUserRepository.findByUsername("admin");
    if (existing == null) {
      String hash = passwordEncoder.encode("admin123");
      adminUserRepository.insert("admin", hash, "ADMIN");
      System.out.println("[DataInitializer] Default admin created (admin / admin123)");
    }
  }
}
