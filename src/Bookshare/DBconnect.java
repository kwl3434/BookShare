package Bookshare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

public class DBconnect {
	private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private final String DB_URL = "jdbc:mysql://localhost/bookshare?characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false";
	private static final String SEPARATOR = "|";
	private final String USER_NAME = "root";
	private final String PASSWORD = "thrhd";

	DBconnect() {
		
	}
	public int logincheck(String id,String pass)
	{
		/*
		 * 로그인 인증 쿼리
		 * 0 정상처리
		 * 1 에러
		 */
		Connection conn = null;
		Statement state = null;
		boolean check = false;
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
				System.out.println(ID);
				System.out.println(password);
				if(ID.compareTo(id)==0&&password.compareTo(pass)==0) {
					check = true;
					break;
				}
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
		if(check) return 0;
		else return 1;
	}
	
	public int createid(String id, String pw,String phone) 
	{
		/*
		 * 회원가입 쿼리
		 * 0  정상 회원가입
		 * 1 id 에러
		 * 2 password 에러
		 * 3 전화번호 에러
		*/
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "INSERT INTO user(id,password,phonenumber)";
			sql+= "VALUES('"+id+"','"+pw+"','"+phone+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	public int checksignup(String id,  String pn) {
		Connection conn = null;
		Statement state = null;
		boolean idcheck = false;
		boolean pncheck = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();
			String sql;
			
			sql = "SELECT * FROM user WHERE id='"+id+"'";
			ResultSet rs=null;
			try {
				rs = state.executeQuery(sql);
			}catch(SQLException sq) {
				rs = null;
			}
			if(!rs.next()) idcheck = true;
			sql = "SELECT * FROM user WHERE phonenumber='"+pn+"'";
			try {
				rs = state.executeQuery(sql);
			}catch(SQLException sq) {
				rs = null;
			}
			
			if(!rs.next()) pncheck= true;
			
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
		if(idcheck&&pncheck) return 0;
		else if(!idcheck&&pncheck) return 1;
		else if(idcheck&&!pncheck) return 2;
		else return 3;
	}
	public String[] getmsg(String id) 
	{
		/*
		 * 클라이언트 메세지목록 전달 쿼리
		 * 정상 메세지 리턴
		 * 에러시 null 리턴
		 */
		String [] buffer= new String[1000];
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT msg_no,text,source,dest,date FROM msg where dest='"+id+"' ORDER BY msg_no DESC";
			ResultSet rs = state.executeQuery(sql);
			int i=0;
			while (rs.next()) {
				String msg_No = rs.getString("msg_no");
				String text = rs.getString("text");
				String source = rs.getString("source");
				String dest = rs.getString("dest");
				String date = rs.getString("date");
				System.out.println(text);
				System.out.println(source);
				System.out.println(date);
				buffer[i]=msg_No+SEPARATOR+text+SEPARATOR+source+SEPARATOR+dest+SEPARATOR+date;
				i++;
			}
			buffer[i]="";
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		return buffer;
	}
	public int setmsg(String msg,String source, String dest) 
	{
		/*
		 * 받은메세지 디비저장 쿼리
		 * 0 정상
		 * 1 id에러
		 * 2 msg에러
		 */
		Connection conn = null;
		Statement state = null;
		String msg_No="0";
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "INSERT INTO msg(text,source,dest)";
			sql+= "VALUES('"+msg+"','"+source+"','"+dest+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	public int removemsg(int msgindex) 
	{
		/*
		 * 메세지 삭제 쿼리
		 * 0 정상
		 * 1 에러
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "DELETE FROM msg WHERE msg_no='"+msgindex+"'";
			rs = state.executeUpdate(sql);
			
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
	public int setboard(String title,String text, String id, String password) 
	{
		/*
		 * 게시판 작성 쿼리
		 * 0 정상
		 * 1 게시판 제목 에러
		 * 2 게시판 패스워드 에러
		 * 3 게시판 본문 에러
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();
			String sql;
			sql = "INSERT INTO board(title,text,id,password) ";
			sql+= "VALUES('"+title+"','"+text+"','"+id+"','"+password+"')";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0) return 0;
		else return 1;
	}
	public String[] getboard() 
	{
		/*
		 * 게시글 로드 쿼리
		 * 게시글리턴 정상
		 * ""리턴 에러
		 */
		String [] buffer= new String[1000];
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT board_no,title,text,id,date FROM board ORDER BY board_no DESC";
			ResultSet rs = state.executeQuery(sql);
			int i=0;
			while (rs.next()) {
				String board_No = rs.getString("board_no");
				String title = rs.getString("title");
				String text = rs.getString("text");
				String ID = rs.getString("id");
				String date = rs.getString("date");
				buffer[i]=board_No+SEPARATOR+title+SEPARATOR+text+SEPARATOR+ID+SEPARATOR+date;
				i++;
			}
			buffer[i]="";
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		return buffer;
	}
	public String selectboard(int no) {
		String buffer= new String();
		Connection conn = null;
		Statement state = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			sql = "SELECT board_no,title,text,id,date FROM board WHERE board_no="+"'"+no+"'";
			ResultSet rs = state.executeQuery(sql);
			int i=0;
			while (rs.next()) {
				String board_No = rs.getString("board_no");
				String title = rs.getString("title");
				String text = rs.getString("text");
				String ID = rs.getString("id");
				String date = rs.getString("date");
				buffer=board_No+SEPARATOR+title+SEPARATOR+text+SEPARATOR+ID+SEPARATOR+date;
				i++;
			}
			rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		return buffer;
	}
	public int removeboard(String pw, String boardindex) 
	{
		/*
		 * 게시글 삭제 쿼리
		 * 0 정상
		 * 1 패스워드 에러
		 * 2 디비정보없음 에러
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		System.out.println(boardindex);
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();

			String sql;
			
			sql = "SELECT password FROM board WHERE board_no="+Integer.parseInt(boardindex);
			ResultSet Rs = null;
			String dPw="";
			try {
				Rs = state.executeQuery(sql);
				Rs.next();
				dPw= Rs.getString("password");
			}catch(SQLException sq) {
				Rs=null;
			}
			System.out.println(dPw+"dPw");
			System.out.println(pw);
			if(dPw.equals(pw)) {
				sql = "DELETE FROM board WHERE board_no="+Integer.parseInt(boardindex);
				rs = state.executeUpdate(sql);
			}else rs = 0;
			
			Rs.close();
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
	public int updateboard(int boardindex, String board,String title, String date, String id) 
	{
		/*
		 * 게시글 업데이트 쿼리
		 * 0 정상
		 * 1 패스워드 에러
		 * 2 디비정보없음 에러
		 */
		Connection conn = null;
		Statement state = null;
		int rs=0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
			System.out.println("\n- MySQL Connection");
			state = conn.createStatement();
			
			String sql;
			sql = "UPDATE board SET title='";
			sql+=title+"',"+"board='"+board+"',"+"id='"+id+"', date='"+date+"' WHERE board_no='"+boardindex+"' LIMIT 1";
			rs = state.executeUpdate(sql);
			
			state.close();
			conn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("\n\n- MySQL Connection Close");
		if(rs>0)return 0;
		else return 1;
	}
}	
