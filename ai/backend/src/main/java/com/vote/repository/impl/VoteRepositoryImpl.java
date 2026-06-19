package com.vote.repository.impl;

import com.vote.entity.VoteItem;
import com.vote.repository.VoteRepository;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class VoteRepositoryImpl implements VoteRepository {

  private final JdbcTemplate jdbcTemplate;

  public VoteRepositoryImpl(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private VoteItem mapRow(ResultSet rs, int rowNum) throws SQLException {
    VoteItem item = new VoteItem();
    item.setId(rs.getInt("id"));
    item.setName(rs.getString("name"));
    try {
      item.setVoteCount(rs.getInt("vote_count"));
    } catch (SQLException e) {
      item.setVoteCount(0);
    }
    if (rs.getTimestamp("created_at") != null)
      item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    if (rs.getTimestamp("updated_at") != null)
      item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    return item;
  }

  private List<VoteItem> executeQuery(String sql, int... params) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall(sql)) {
        for (int i = 0; i < params.length; i++) {
          cs.setInt(i + 1, params[i]);
        }
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          List<VoteItem> list = new ArrayList<>();
          int rowNum = 0;
          while (rs.next()) {
            list.add(mapRow(rs, rowNum++));
          }
          return list;
        }
      }
    });
  }

  @Override
  public List<VoteItem> findAll() {
    return executeQuery("{CALL sp_get_all_items()}");
  }

  @Override
  public VoteItem findById(Integer id) {
    List<VoteItem> items = executeQuery("{CALL sp_get_item_by_id(?)}", id);
    return items.isEmpty() ? null : items.get(0);
  }

  @Override
  public VoteItem findByName(String name) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_find_item_by_name(?)}")) {
        cs.setString(1, name);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return mapRow(rs, 0);
          }
        }
      }
      return null;
    });
  }

  @Override
  public int insert(String name) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_insert_item(?)}")) {
        cs.setString(1, name);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return rs.getInt("id");
          }
        }
      }
      return 0;
    });
  }

  @Override
  public int update(Integer id, String name) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_update_item(?, ?)}")) {
        cs.setInt(1, id);
        cs.setString(2, name);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return rs.getInt("affected");
          }
        }
      }
      return 0;
    });
  }

  @Override
  public int delete(Integer id) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_delete_item(?)}")) {
        cs.setInt(1, id);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return rs.getInt("affected");
          }
        }
      }
      return 0;
    });
  }

  @Override
  public void castVotes(String voter, List<Integer> itemIds) {
    jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_cast_votes(?, ?)}")) {
        cs.setString(1, voter);
        String ids = itemIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        cs.setString(2, ids);
        cs.execute();
      }
      return null;
    });
  }

  @Override
  public boolean hasVoted(String voter) {
    return jdbcTemplate.execute((java.sql.Connection connection) -> {
      try (CallableStatement cs = connection.prepareCall("{CALL sp_has_voter_voted(?)}")) {
        cs.setString(1, voter);
        cs.execute();
        try (ResultSet rs = cs.getResultSet()) {
          if (rs.next()) {
            return rs.getInt("cnt") > 0;
          }
        }
      }
      return false;
    });
  }
}
