package com.vote.service;

import com.vote.common.dto.LoginResponseDTO;

public interface AuthService {
  LoginResponseDTO login(String username, String password);
  void register(String username, String password);
}
