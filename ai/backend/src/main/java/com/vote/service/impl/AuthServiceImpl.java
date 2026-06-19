package com.vote.service.impl;

import com.vote.common.dto.LoginResponseDTO;
import com.vote.common.util.JwtUtil;
import com.vote.common.util.HtmlSanitizer;
import com.vote.entity.AdminUser;
import com.vote.repository.AdminUserRepository;
import com.vote.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthServiceImpl(AdminUserRepository adminUserRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public LoginResponseDTO login(String username, String password) {
    String safeUser = HtmlSanitizer.sanitize(username);
    if (safeUser == null) throw new IllegalArgumentException("帳號不能為空");

    AdminUser user = adminUserRepository.findByUsername(safeUser);
    if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new IllegalArgumentException("帳號或密碼錯誤");
    }

    String token = jwtUtil.generateToken(safeUser, user.getRole());
    return new LoginResponseDTO(token, safeUser, user.getRole());
  }

  @Override
  @Transactional
  public void register(String username, String password) {
    String safeUser = HtmlSanitizer.sanitize(username);
    if (safeUser == null || safeUser.isBlank()) throw new IllegalArgumentException("帳號不能為空");
    if (password == null || password.length() < 4) throw new IllegalArgumentException("密碼至少需要4個字元");

    AdminUser existing = adminUserRepository.findByUsername(safeUser);
    if (existing != null) throw new IllegalArgumentException("帳號已存在");

    String hash = passwordEncoder.encode(password);
    adminUserRepository.insert(safeUser, hash, "USER");
  }
}
