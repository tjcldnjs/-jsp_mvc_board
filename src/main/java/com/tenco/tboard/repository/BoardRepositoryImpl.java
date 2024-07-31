package com.tenco.tboard.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.tenco.tboard.model.Board;
import com.tenco.tboard.repository.interfaces.BoardRepository;
import com.tenco.tboard.util.DBUtil;

public class BoardRepositoryImpl implements BoardRepository{

	private static final String SELECT_ALL_BOARDS = " select * from board order by created_at desc limit ? offset ?; ";
	private static final String COUNT_ALLBOARDS = " select count(*) as count from board; ";
	private static final String INSERT_BOARD_SQL = " insert into board(user_id, title, content) values (?, ?, ?) "; 
	private static final String DELETE_BOARD_SQL = " delete from board where id = ? ";
	private static final String SELECT_BOARD_BY_ID = " select * from board where id = ? ";
	private static final String UPDATE_BOARD_SQL = " update board set title = ? , content = ? where id = ? ";
	
	
	
	@Override
	public void addBoard(Board board) {
		// 트랜잭션 - 논리적인 하나의 작업에 단위
		try (Connection conn = DBUtil.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(INSERT_BOARD_SQL)){
				pstmt.setInt(1, board.getUserId());
				pstmt.setString(2, board.getTitle());
				pstmt.setString(3, board.getContent());
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
	public void updateBoard(Board board) {
		try (Connection conn = DBUtil.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_BOARD_SQL)){
				pstmt.setString(1, board.getTitle());
				pstmt.setString(2, board.getContent());
				pstmt.setInt(3, board.getId());
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
	public void deleteBoard(int id) {
		try (Connection conn = DBUtil.getConnection()){
			conn.setAutoCommit(false);
			try (PreparedStatement pstmt = conn.prepareStatement(DELETE_BOARD_SQL)){
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
	public Board getBoardById(int id) {
		Board board = null;
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(SELECT_BOARD_BY_ID)){
			pstmt.setInt(1, id);
		
			try (ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					board = Board.builder()
							.id(rs.getInt("id"))
							.userId(rs.getInt("user_id"))
							.title(rs.getString("title"))
							.content(rs.getString("content"))
							.createdAt(rs.getTimestamp("created_at"))
							.build();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return board;
	}

	@Override
	public List<Board> getAllBoards(int limit, int offset) {
		List<Board> boardList = new ArrayList<>();
		
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_BOARDS)){
			pstmt.setInt(1, limit);
			pstmt.setInt(2, offset);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Board board = new Board().builder().id(rs.getInt("id"))
				.userId(rs.getInt("user_id"))
				.title(rs.getString("title"))
				.content(rs.getString("content"))
				.createdAt(rs.getTimestamp("created_at")).build();
				boardList.add(board);
			}
			System.out.println("BoardRepositoryImpl - 로깅 : count" +  boardList.size());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return boardList;
	}

	@Override
	public int getTotalBoardCount() {
		int count = 0;
		
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(COUNT_ALLBOARDS)){
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				count = rs.getInt("count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(" 로깅 totalCount : " + count);
		return count;
	}

}

