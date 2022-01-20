package bank;
import java.sql.*;

public class TransferImpl implements Transfer{
	private Connection con;
	private Statement stmt;
	public TransferImpl(){
		String url = "jdbc:oracle:thin:@127.0.0.1:1521:JAVA";
		String uid = "scott";
		String upwd = "tiger";
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, uid, upwd);
			con.setAutoCommit(false);
			stmt = con.createStatement();
		}catch(ClassNotFoundException cfe) {
		}catch(SQLException se) {
		}
	}
	@Override
	public boolean isMember(String email) {
		String sql = "select * from ACC where EMAIL='"+email+"'"; // 고객이 존재하는지만 검사
		ResultSet rs=null;
		try {
			rs = stmt.executeQuery(sql);
			if(rs.next()) {
				return true;
			}
			else {
				pln("고객이 존재하지 않습니다.");
				return false;
			}
		}catch(SQLException se) {
		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		return false;
	}
	@Override
	public boolean checkBalance(String sender, long amount) { // sender의 이메일
		String sql = "select BALANCE from ACC where EMAIL='"+sender+"'";  // sender BALANCE를 amount랑 비교
		long balance=0;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				balance = rs.getLong(1);
			}
			if(balance>=amount){
				return true;
			}
			else {
				pln("잔액이 부족합니다");
				return false;
			}
		}catch(SQLException se) {
		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		return false;
	}
	@Override
	public boolean minus(String sender, long amount) {  
		String sqlS = "select BALANCE from ACC where EMAIL='"+sender+"'"; // sender BALANCE에서 amount를 차감
		long balance=0;
		ResultSet rs=null;
		try {
			rs = stmt.executeQuery(sqlS);
			while(rs.next()) {
				balance = rs.getLong(1);
			}
			balance -= amount;
			String sqlU = "update ACC set BALANCE="+balance+", UDATE=SYSTIMESTAMP where EMAIL='"+sender+"'";
			int re = stmt.executeUpdate(sqlU);
			if(re>0) {
				return true;
			}
			else  {
				con.rollback();
			}
		}catch(SQLException se) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		return false;
	}
	@Override
	public boolean plus(String receiver, long amount){
		String sqlSe = "select BALANCE from ACC where EMAIL='"+receiver+"'"; // receiver의  BALANCE에서 amount를 증가
		long balance=0;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sqlSe);
			while(rs.next()) {
				balance = rs.getLong(1);
			}
			balance += amount;
			String sqlUp = "update ACC set BALANCE="+balance+", UDATE=SYSTIMESTAMP where EMAIL='"+receiver+"'";
			int re = stmt.executeUpdate(sqlUp);
			if(re>0) {
				return true;
			}
			else  {
				con.rollback();
			}
		}catch(SQLException se) {
			try {
				con.rollback();
			}catch (SQLException e) {
		}
		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
		return false;
	}
	@Override
	public boolean log(String sender, String receiver, long amount) {  // 로그 작성
		String sql = "insert into TRAN_LOG values(TRAN_LOG_SEQ.nextval, '"+sender+"', '"+ receiver+"', "+amount+", SYSTIMESTAMP)";
		try {
			int i = stmt.executeUpdate(sql);
			if(i>0) {
				con.commit();
				return true;
			}
			else {
				con.rollback();
			}
		}catch(SQLException se) {
			try {
				con.rollback();
			} catch (SQLException e) {
			}
		}
		return false;
	}
	@Override
	public void showResult(String sender, String receiver) {
		String sqlS1 ="select * from ACC where EMAIL='"+receiver+"' or EMAIL='"+sender+"'";
		String sqlS2 ="select * from ACC where EMAIL='"+receiver+"' or EMAIL='"+sender+"'";
		ResultSet rs = null;
		ResultSetMetaData rsmd = null;
		try {
			pln("받는사람\t보내는사람\t     잔액\t  거래날짜\t\t                   가입날짜");
			pln("----------------------------------------------------------");
			rs = stmt.executeQuery(sqlS1);
			rsmd = rs.getMetaData();
			int index1 = rsmd.getColumnCount();
			while(rs.next()) {
				if(receiver.equals(rs.getString(1))){
					p(rs.getString(2)+"\t");
					break;
				}
			}
			rs = stmt.executeQuery(sqlS2);
			while(rs.next()) {
				if(sender.equals(rs.getString(1))){
					for(int i=2; i<=index1; i++) {
						p(rs.getString(i)+"\t");
					}
					pln("");
				}
			}
		} catch (SQLException e) {
		}finally {
			closeAll();
		}
	}
	@Override
	public void closeAll(){
		try {
			stmt.close();
			con.close();
		}catch(SQLException se) {
		}
	}
	@Override
	public boolean transfer(String sender, String receiver, long amount) throws SQLException{
		if(isMember(sender)) {
			if(checkBalance(sender, amount)) {
				minus(sender, amount);
				plus(receiver, amount);
				log(sender, receiver, amount);
				showResult(sender, receiver);
				return true;
			}else {
				return false;
			}
		}
		return false;
	}
	public void p(String str) {
		System.out.print(str);
	}
	public void pln(String str) {
		System.out.println(str);
	}
}
