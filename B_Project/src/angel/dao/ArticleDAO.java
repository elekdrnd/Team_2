package angel.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import angel.model.Article;
import angel.model.Comment;
import jdbc.JDBCUtil;
import jdbc.connection.ConnectionProvider;
public class ArticleDAO {
	Connection conn = null;
	PreparedStatement pstmt = null;
	PreparedStatement pstmt2 = null;	
	ResultSet rs = null;
	
	//모든 게시물 목록(articleNo, memberid, name, title, regdate, readCnt, content) 불러오기
	public List<Article> selectAll(Connection conn) {
		String sql = "select * from angel_animaltable order by articleNo desc";
		List<Article> article = new LinkedList<>();
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				int articleNo = rs.getInt("articleNo");
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt");
				String content = rs.getString("content");
				
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);
				article.add(arti);	
			}
			return article;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
	}

	//총 게시물 수 가져오기
	public int articleTotal(Connection conn) {
		int articleTotal = 0;
		String sql = "select count(*) from angel_animaltable";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				articleTotal = rs.getInt(1);
				return articleTotal;
			} else {
				return 0; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return articleTotal;
	}
	
	//게시글 상세조회 내용 + 조회에 따른 조회수 증가
	public Article selectContent(Connection conn, int articleNo) {
		String sql1 = "select * from angel_animaltable where articleNo = ?";
		String sql2 = "update angel_animaltable set readCnt = readCnt+1 where articleNo = ?";
		
		try {
			pstmt = conn.prepareStatement(sql1);
			pstmt.setInt(1, articleNo);
			rs = pstmt.executeQuery();
			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setInt(1, articleNo);
			int upCnt = pstmt2.executeUpdate();

			while(rs.next()) {
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt") + 1;
				String content = rs.getString("content");
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);
				
				if(upCnt==1) {
					return arti;
				} else {
					conn.rollback();
					return null;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt2);
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
	}

	//게시물 삭제
	public boolean delete(Connection conn, int articleNo) {
		String sql = "delete from angel_animaltable where articleNo = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, articleNo);
			boolean isDelete = pstmt.execute();
			return !isDelete;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return false;
	}

	//게시글 수정
	public int modify(Connection conn, Article article) {
		int row = 0;
		String sql = "update angel_animaltable set content=?, title=? where articleNo=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, article.getContent());
			pstmt.setString(2, article.getTitle());
			pstmt.setInt(3, article.getArticleNo());
			row = pstmt.executeUpdate();
			return row;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return row;
	}
	
	public int write(Connection conn, Article article) {
		int row = 0;
		String sql = "insert into angel_animaltable(memberid, title, name, content) values(?, ?, ?, ?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, article.getMemberid());
			pstmt.setString(2, article.getTitle());
			pstmt.setString(3, article.getName());
			pstmt.setString(4, article.getContent());
			row = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return row;
	}

	//페이징 처리. 페이지 번호마다 보여질 게시물 목록 가져오기
	public List<Article> pageNoArticle(Connection conn, int pageNo) {
		String sql = "select * from angel_animaltable order by 1 desc limit ?, ?";

		//페이지 번호마다 보여질 게시물 개수
		int ten = 10;
		//페이지 번호마다 시작되는 로우를 나타냄. 1페이지 0, 2페이지 10, 3페이지 20 ... 
		int startNum = 0 + (pageNo-1) * ten;
		int articleTotal = articleTotal(conn);
		if(0 + (pageNo-1) * ten > articleTotal) {
			ten = articleTotal % ten;}
		
		List<Article> pageNoArticle = new LinkedList<>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, startNum);
			pstmt.setInt(2, ten);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int articleNo = rs.getInt("articleNo");
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt");
				String content = rs.getString("content");
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);

				pageNoArticle.add(arti);
			}
			return pageNoArticle;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
	}
	
	public List<Article> select(Connection conn, String type, String subject) { // type은 검색할거 종류 , subject 는 검색 내용
		String sql = "SELECT * FROM angel_animaltable WHERE "+type+" LIKE ?";
		
		List<Article> select = new LinkedList<>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,  "%" + subject + "%");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int articleNo = rs.getInt("articleNo");
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt");
				String content = rs.getString("content");
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);
//				
				select.add(arti);
			}
			return select;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
		
	}

	//이름별로 모아오기
	public List<Article> category(Connection conn, String category) {
		String sql = "select * from angel_animaltable where name = ?";
		//이름별로 모든 컬럼을 조회해야 하기 때문에 List. 컬럼 모델이 Article.
		List<Article> select = new LinkedList<>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, category);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int articleNo = rs.getInt("articleNo");
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt");
				String content = rs.getString("content");
				
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);
				select.add(arti);
			}
			return select;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
	}

	//게시물 번호에 해당하는 모든 댓글 보기
	public List<Comment> comment(int articleNo) {
		String sql = "select * from angel_comment where articleNo= ?";
		
		List<Comment> commentText = new LinkedList<>();
		
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, articleNo);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int commentNo = rs.getInt("commentNo");
				String name = rs.getString("name");
				String comment = rs.getString("comment");
				String regdate = rs.getString("regdate");
				
				Comment commentList = new Comment(articleNo, commentNo, name, comment, regdate);
				commentText.add(commentList);
			}
			return commentText;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
			JDBCUtil.close(conn);
		}
		return null;
	}

	//댓글 작성
	public int writeComment(Connection conn, Comment writeComment) {
		String sql = "insert into angel_comment(articleNo, name, comment) values (?, ?, ?)";
		
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, writeComment.getArticleNo());
			pstmt.setString(2, writeComment.getName());
			pstmt.setString(3, writeComment.getComment());
			int row = pstmt.executeUpdate();
			return row;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return 0; 
	}
	
	//댓글 삭제
	public int commentDelete(Connection conn, int commentNo) {
		String sql = "delete from angel_comment where commentNo = ?";
		
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, commentNo);
			int row = pstmt.executeUpdate();
			return row;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return 0;
	}
	
	public int showArticleNo(Connection conn, int commentNo) {
		String sql = "select articleNo from angel_comment where commentNo = ?";
		int articleNo = 0;
		
		try {
			conn = ConnectionProvider.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, commentNo);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				articleNo = rs.getInt("articleNo");
			}
			return articleNo;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return articleNo;
	}

	public int modifyComment(Connection conn, int commentNo, String comment) {
		String sql = "update angel_comment set comment = ? where commentNo = ?";
		int row = 0;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, comment);
			pstmt.setInt(2, commentNo);
			row = pstmt.executeUpdate();
			return row;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(pstmt);
		}
		return row;
	}

	public int categoryTotal(Connection conn, String category) {
		int categoryTotal = 0;
		String sql = "select count(*) from angel_animaltable where name = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, category);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				categoryTotal = rs.getInt(1);
				return categoryTotal;
			} else {
				return 0; 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return categoryTotal;
	}

	//페이징 처리. 페이지 번호마다 보여질 게시물 목록 가져오기
	public List<Article> pageNoCategory(Connection conn, String category, int pageNo) {
		String sql = "select * from angel_animaltable where name = ? order by 1 desc limit ?, ?";

		//페이지 번호마다 보여질 게시물 개수
		int ten = 5;
		//페이지 번호마다 시작되는 로우를 나타냄. 1페이지 0, 2페이지 10, 3페이지 20 ... 
		int startNum = 0 + (pageNo-1) * ten;
		int categoryTotal = categoryTotal(conn, category);
		/*
		 * if(0 + (pageNo-1) * ten > categoryTotal) { ten = categoryTotal % ten;}
		 */
		
		List<Article> pageNoCategory = new LinkedList<>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, category);
			pstmt.setInt(2, startNum);
			pstmt.setInt(3, ten);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				int articleNo = rs.getInt("articleNo");
				String memberid = rs.getString("memberid");
				String name = rs.getString("name");
				String title = rs.getString("title");
				String regdate = rs.getString("regdate");
				int readCnt = rs.getInt("readCnt");
				String content = rs.getString("content");
				Article arti = new Article(articleNo, memberid, name, title, regdate, readCnt, content);

				pageNoCategory.add(arti);
			}
			return pageNoCategory;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(rs);
			JDBCUtil.close(pstmt);
		}
		return null;
	}	
}

	

