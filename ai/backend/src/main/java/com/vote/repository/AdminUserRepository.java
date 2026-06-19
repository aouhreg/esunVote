package com.vote.repository;

import com.vote.entity.AdminUser;

public interface AdminUserRepository {
  AdminUser findByUsername(String username);
  int insert(String username, String passwordHash, String role);
}
