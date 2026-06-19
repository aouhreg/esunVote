package com.vote.controller;

import com.vote.common.dto.ApiResponse;
import com.vote.common.dto.LoginRequestDTO;
import com.vote.common.dto.LoginResponseDTO;
import com.vote.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
      @Valid @RequestBody LoginRequestDTO dto) {
    LoginResponseDTO result = authService.login(dto.getUsername(), dto.getPassword());
    return ResponseEntity.ok(ApiResponse.ok("登入成功", result));
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(
      @Valid @RequestBody LoginRequestDTO dto) {
    authService.register(dto.getUsername(), dto.getPassword());
    return ResponseEntity.ok(ApiResponse.ok("註冊成功，請重新登入", null));
  }
}
