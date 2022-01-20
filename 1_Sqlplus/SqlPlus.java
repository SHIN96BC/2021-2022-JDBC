import java.io.*;
import java.sql.*;

class SqlPlus {
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	Statement stmt;
	SqlPlus(){
		input();
	}
	void input(){
		try{
			p("오라클 ID(default:127.0.0.1) : ");
			String oid = br.readLine();
			oid = oid.trim();
			if(oid.length() == 0) oid = "localhost";
			p("오라클 SID(default:JAVA) : ");
			String sid = br.readLine();
			sid = sid.trim();
			if(sid.length() == 0) sid = "JAVA";
			p("유저 ID(default:scott) : ");
			String uid = br.readLine();
			uid = uid.trim();
			if(uid.length() == 0) uid = "scott";
			p("유저 PW(default:tiger) : ");
			String upwd = br.readLine();
			upwd = upwd.trim();
			if(upwd.length() == 0) upwd = "tiger";
			String url = "jdbc:oracle:thin:@"+oid+":1521:"+sid;

			connect(url, uid, upwd);
		}catch(IOException ie){
		}
	}
	void connect(String url, String uid, String upwd){
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");  // 파라미터는 데이터베이스에따라 다름
			Connection con = DriverManager.getConnection(url, uid, upwd);
			stmt = con.createStatement();
			pln("데이터베이스에 접속성공");
			sqlplus(con);
		}catch(ClassNotFoundException cnfe){
			pln("드라이버로딩 실패(클래스를 못 찾음)");
		}catch(SQLException se){
			pln("Oracle 접속 정보가 잘못됨 다시 입력하세요.");
			input();
			return;
		}
	}
	void sqlplus(Connection con){
		int i=0;
		while(true){
			try{
				p("SQL> ");
				String msg = br.readLine();
				msg = msg.trim();
				if((i=msg.indexOf(";"))>0) msg = msg.substring(0, i);
				if(msg.toLowerCase().startsWith("create")){
					int c = create(msg);
					if(c==0){
						String temp = msg.substring(msg.indexOf(" ")+1);
						if(temp.toLowerCase().startsWith("table")){
							pln("테이블이 이미 존재합니다");
						}else if(temp.toLowerCase().startsWith("sequence")){
							pln("시퀀스가 이미 존재합니다");
						}
					}
				}else if(msg.toLowerCase().startsWith("drop")){ // toLowerCase는 String 타입을 소문자로 바꿔서 비교할때 씀
					int c = drop(msg);
					if(c==0){
						String temp = msg.substring(msg.indexOf(" ")+1);
						if(temp.toLowerCase().startsWith("table")){
							pln("삭제할 테이블 없습니다");
						}else if(temp.toLowerCase().startsWith("sequence")){
							pln("삭제할 시퀀스가 없습니다");
						}
					}
				}else if(msg.toLowerCase().startsWith("alter")){
					alter(msg);
				}else if(msg.toLowerCase().startsWith("insert")){
					insert(msg);
				}else if(msg.toLowerCase().startsWith("update")){
					update(msg);
				}else if(msg.toLowerCase().startsWith("delete")){
					delete(msg);
				}else if(msg.toLowerCase().startsWith("select")){
					select(msg);
				}else if(msg.equalsIgnoreCase("exit")){
					System.exit(0);
				}else{
					pln("잘못된 sql문 입니다.");
				}
			}catch(IOException ie){
			}finally{
				try{
					stmt.close();
					con.close();
				}catch(SQLException se){
				}
			}
		}
	}

	int create(String sql){
		String temp="";
		int i=0;
		try{
			stmt.execute(sql);
			temp = sql.substring(sql.indexOf(" ")+1);
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블이 생성되었습니다");
				return i+1;
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("시퀀스가 생성되었습니다");
				return i+1;
			}
			pln("일단 생성 성공");
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블을 생성하지 못했습니다.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("시퀀스를 생성하지 못했습니다.");
			}
		}
		return i;
	}
	int drop(String sql){
		String temp="";
		int i=0;
		try{
			stmt.execute(sql);
			temp = sql.substring(sql.indexOf(" ")+1);
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블이 삭제되었습니다.");
				return i+1;
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("시퀀스이 삭제되었습니다.");
				return i+1;
			}
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블을 삭제하지 못했습니다.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("시퀀스를 삭제하지 못했습니다.");
			}
		}
		return i;
	}
	void alter(String sql){
		String temp="";
		try{
			stmt.execute(sql);
			temp = sql.substring(sql.indexOf(" ")+1);
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블이 수정되었습니다.");
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("시퀀스가 수정되었습니다.");
			}
			pln("일단 생성 성공");
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("테이블을 수정하지 못했습니다.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("시퀀스를 수정하지 못했습니다");
			}
		}
	}
	void insert(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"개의 행이 입력되었습니다.");
			else pln("입력데이터가 잘못되었습니다.");
		}catch(SQLException se){
			pln("입력데이터가 잘못되었거나, 무결성 제약조건에 위배되었습니다.");
		}
	}
	void update(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"개의 행이 수정되었습니다.");
			else pln("수정실패");
		}catch(SQLException se){
			pln("수정실패");
		}
	}
	void delete(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"개의 행이 삭제되었습니다.");
			else pln("삭제실패");
		}catch(SQLException se){
			pln("삭제실패");
		}
	}
	void select(String sql){
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int index = rsmd.getColumnCount();
			pln("컬럼갯수: " + index);
			for(int k=1; k<=index; k++){
				p(rsmd.getColumnName(k)+"\t");
			}
			pln("\n--------------------------------------------------------------------------------");
			while(rs.next()){
				for(int j=1; j<=index; j++){
					int type = rsmd.getColumnType(j);
					if(type == Types.NUMERIC || type == Types.INTEGER || type == Types.BIGINT){
						int valInt = rs.getInt(j);
						p(valInt+"\t");
					}else if(type == Types.DOUBLE || type == Types.FLOAT){
						double valDou = rs.getDouble(j);
						p(valDou+"\t");
					}else if(type == Types.CHAR || type == Types.VARCHAR || type == Types.NVARCHAR){
						String valStr = rs.getString(j);
						p(valStr+"\t");
					}else if(type == Types.DATE || type == Types.TIME || type == Types.TIMESTAMP){
						Date valDa = rs.getDate(j);
						p(valDa+"\t");
					}
				}
				pln("");
			}
			pln("\n");

			/*while(rs.next()){   //String 으로 다 땡겨도 가능하긴함
				for(int j=1; j<=index; j++){
					String valStr = rs.getString(j);
						p(valStr+"\t");
					}
					pln("");
				}*/
		}catch(SQLException se){
		}finally{
			try{
				rs.close();
			}catch(SQLException se){
			}
		}
	}
	void p(String str){
		System.out.print(str);
	}
	void pln(String str){
		System.out.println(str);
	}
	public static void main(String[] args) {
		new SqlPlus();
	}
}
