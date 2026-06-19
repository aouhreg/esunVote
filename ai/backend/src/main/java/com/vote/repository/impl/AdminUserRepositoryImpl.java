package com.vote.repository.impl;

import com.vote.entity.AdminUser;
import com.vote.repository.AdminUserRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminUserRepositoryImpl implements AdminUserRepository {

  private final JdbcTemplate jdbcTemplate;

  public AdminUserRepositoryImpl(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private AdminUser mapRow(ResultSet rs, int rowNum) throws SQLException {
    AdminUser user = new AdminUser();
    user.setId(rs.getInt("id"));
    user.setUsername(rs.getString("username"));
    user.setPasswordHash(rs.getString("password_hash"));
    user.setRole(rs.getString("role"));
    try { if (rs.getTimestamp("created_at") != null) user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime()); } catch (Exception ignored) {}
    return user;
  }

  @Override
  public AdminUser findByUsername(String username) {
    List<AdminUser> list = jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (var cs = connection.prepareCall("{CALL sp_get_user_by_username(?)}")) {
        cs.setString(1, username);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          java.util.List<AdminUser> users = new java.util.ArrayList<>();
          int rowNum = 0;
          while (rs.next()) { users.add(mapRow(rs, rowNum++)); }
          return users;
        }
      }
    });
    return list.isEmpty() ? null : list.get(0);
  }

  @Override
  public int insert(String username, String passwordHash, String role) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (var cs = connection.prepareCall("{CALL sp_insert_user(?, ?, ?)}")) {
        cs.setString(1, username);
        cs.setString(2, passwordHash);
        cs.setString(3, role);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) return rs.getInt("id");
        }
      }
      return 0;
    });
  }
}
