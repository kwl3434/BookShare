package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBconnect {
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/bookshare?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";

	private final String USER_NAME = "root";
	private final String PASSWORD = "thrhd";

	DBconnect() {
		
	}
	public int logincheck(String id,String password)
	{
		/*
		 * 로그인 인증 쿼리
		 * 0 정상처리
		 * 1 아이디에러
		 * 2 패스워드에러 
		 */
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT * FROM user";
			ResultSet rs = state.executeQuery(sql);

			while (rs.next()) {
				String ID = rs.getString("id");
				String password = rs.getString("password");
				String phonenumber = rs.getString("phonenumber");
				System.out.println("ID : " + ID);
				System.out.println("PASSWORD : " + password);
				System.out.println("PHONENUMBER : " + phonenumber);
			}
			rs.close();
			state.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} /*finally {
			try {
				if (state != null)
					state.close();
			} catch (SQLException ex1) {
				ex1.printStackTrace();
			}
		}*/
		System.out.println("\n\n- MySQL Connection Close");
		return 0;
	}
	
	public int createid(String info) 
	{
		/*
		 * 회원가입 쿼리
		 * 0  정상 회원가입
		 * 1 id 에러
		 * 2 password 에러
		 * 3 전화번호 에러
		 * 4 이메일 에러
		*/
		return 0;
	}
	
	public String getmsg() 
	{
		/*
		 * 클라이언트 메세지목록 전달 쿼리
		 * 정상 메세지 리턴
		 * 에러시 null 리턴
		 */
		return "";
	}
	public int setmsg(String msg) 
	{
		/*
		 * 받은메세지 디비저장 쿼리
		 * 0 정상
		 * 1 id에러
		 * 2 msg에러
		 */
		return 0;
	}
	public int removemsg(int msgindex) 
	{
		/*
		 * 메세지 삭제 쿼리
		 * 0 정상
		 * 1 에러
		 */
		return 0;
	}
	public int setboard(String board) 
	{
		/*
		 * 게시판 작성 쿼리
		 * 0 정상
		 * 1 게시판 제목 에러
		 * 2 게시판 패스워드 에러
		 * 3 게시판 본문 에러
		 */
		return 0;
	}
	public String getboard() 
	{
		/*
		 * 게시글 로드 쿼리
		 * 게시글리턴 정상
		 * ""리턴 에러
		 */
		return "";
	}
	public int removeboard(int boardindex) 
	{
		/*
		 * 게시글 삭제 쿼리
		 * 0 정상
		 * 1 패스워드 에러
		 * 2 디비정보없음 에러
		 */
		return 0;
	}
	public int updateboard(int boardindex, String board) 
	{
		/*
		 * 게시글 업데이트 쿼리
		 * 0 정상
		 * 1 패스워드 에러
		 * 2 디비정보없음 에러
		 */
		return 0;
	}
}	
