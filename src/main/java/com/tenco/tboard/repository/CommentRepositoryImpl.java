package com.tenco.tboard.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tenco.tboard.model.Comment;
import com.tenco.tboard.repository.interfaces.CommentRepository;
import com.tenco.tboard.util.DBUtil;

public class CommentRepositoryImpl implements CommentRepository {

	private static final String INSERT_COMMENT_SQL = " insert into comments (board_id, user_id, content) values (?,?,?) ";
	private static final String DELETE_COMMENT_SQL = " delete from comments where id = ? ";
	private static final String SELECT_COMMENT_BY_ID = " select * from comments where id = ? ";
	private static final String SELECT_COMMENT_BY_BOARD_ID = " select c.*,u.username from comments as c join users as u on c.user_id = u.id where board_id = ? order by c.created_at desc; ";
	
	private static final String UPDATE_COMMENT_SQL = " update comments set content = ? where id = ? ";

	@Override
	public void addComment(Comment comment) {

		try (Connection conn = DBUtil.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(INSERT_COMMENT_SQL)) {
				pstmt.setInt(1, comment.getBoardId());
				pstmt.setInt(2, comment.getUserId());
				pstmt.setString(3, comment.getContent());
				pstmt.executeUpdate();
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void deleteComment(int id) {
		try (Connection conn = DBUtil.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(DELETE_COMMENT_SQL)) {
				pstmt.setInt(1, id);
				pstmt.executeUpdate();
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Comment getCommentById(int id) {
		Comment comment = null;
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(SELECT_COMMENT_BY_ID)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					comment = new Comment(rs.getInt("id"), rs.getInt("board_id"), rs.getInt("user_id"),
							rs.getString("content"), rs.getTimestamp("created_at"),null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comment;
	}

	@Override
	public List<Comment> getCommentsByBoardId(int boardId) {
		List<Comment> commentList = new ArrayList<>();
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(SELECT_COMMENT_BY_BOARD_ID)) {
			pstmt.setInt(1, boardId);
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					Comment comment = new Comment(rs.getInt("id"), rs.getInt("board_id"), rs.getInt("user_id"),
							rs.getString("content"), rs.getTimestamp("created_at"),rs.getString("username"));
					commentList.add(comment);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return commentList;
	}

	@Override
	public void updateComment(Comment comment) {
		try (Connection conn = DBUtil.getConnection()) {
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_COMMENT_SQL)) {
				pstmt.setString(1, comment.getContent());
				pstmt.setInt(2, comment.getId());
				pstmt.executeUpdate();
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
