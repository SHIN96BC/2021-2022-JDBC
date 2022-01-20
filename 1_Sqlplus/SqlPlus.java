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
			p("����Ŭ ID(default:127.0.0.1) : ");
			String oid = br.readLine();
			oid = oid.trim();
			if(oid.length() == 0) oid = "localhost";
			p("����Ŭ SID(default:JAVA) : ");
			String sid = br.readLine();
			sid = sid.trim();
			if(sid.length() == 0) sid = "JAVA";
			p("���� ID(default:scott) : ");
			String uid = br.readLine();
			uid = uid.trim();
			if(uid.length() == 0) uid = "scott";
			p("���� PW(default:tiger) : ");
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
			Class.forName("oracle.jdbc.driver.OracleDriver");  // �Ķ���ʹ� �����ͺ��̽������� �ٸ�
			Connection con = DriverManager.getConnection(url, uid, upwd);
			stmt = con.createStatement();
			pln("�����ͺ��̽��� ���Ӽ���");
			sqlplus(con);
		}catch(ClassNotFoundException cnfe){
			pln("����̹��ε� ����(Ŭ������ �� ã��)");
		}catch(SQLException se){
			pln("Oracle ���� ������ �߸��� �ٽ� �Է��ϼ���.");
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
							pln("���̺��� �̹� �����մϴ�");
						}else if(temp.toLowerCase().startsWith("sequence")){
							pln("�������� �̹� �����մϴ�");
						}
					}
				}else if(msg.toLowerCase().startsWith("drop")){ // toLowerCase�� String Ÿ���� �ҹ��ڷ� �ٲ㼭 ���Ҷ� ��
					int c = drop(msg);
					if(c==0){
						String temp = msg.substring(msg.indexOf(" ")+1);
						if(temp.toLowerCase().startsWith("table")){
							pln("������ ���̺� �����ϴ�");
						}else if(temp.toLowerCase().startsWith("sequence")){
							pln("������ �������� �����ϴ�");
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
					pln("�߸��� sql�� �Դϴ�.");
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
				pln("���̺��� �����Ǿ����ϴ�");
				return i+1;
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("�������� �����Ǿ����ϴ�");
				return i+1;
			}
			pln("�ϴ� ���� ����");
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("���̺��� �������� ���߽��ϴ�.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("�������� �������� ���߽��ϴ�.");
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
				pln("���̺��� �����Ǿ����ϴ�.");
				return i+1;
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("�������� �����Ǿ����ϴ�.");
				return i+1;
			}
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("���̺��� �������� ���߽��ϴ�.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("�������� �������� ���߽��ϴ�.");
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
				pln("���̺��� �����Ǿ����ϴ�.");
			}else if(temp.toLowerCase().startsWith("sequence")){
				pln("�������� �����Ǿ����ϴ�.");
			}
			pln("�ϴ� ���� ����");
		}catch(SQLException se){
			if(temp.toLowerCase().startsWith("table")){
				pln("���̺��� �������� ���߽��ϴ�.");
			}else if(temp.toLowerCase().startsWith("sequence")){ 
				pln("�������� �������� ���߽��ϴ�");
			}
		}
	}
	void insert(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"���� ���� �ԷµǾ����ϴ�.");
			else pln("�Էµ����Ͱ� �߸��Ǿ����ϴ�.");
		}catch(SQLException se){
			pln("�Էµ����Ͱ� �߸��Ǿ��ų�, ���Ἲ �������ǿ� ����Ǿ����ϴ�.");
		}
	}
	void update(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"���� ���� �����Ǿ����ϴ�.");
			else pln("��������");
		}catch(SQLException se){
			pln("��������");
		}
	}
	void delete(String sql){
		try{
			int i = stmt.executeUpdate(sql);
			if(i>0) pln(i+"���� ���� �����Ǿ����ϴ�.");
			else pln("��������");
		}catch(SQLException se){
			pln("��������");
		}
	}
	void select(String sql){
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int index = rsmd.getColumnCount();
			pln("�÷�����: " + index);
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

			/*while(rs.next()){   //String ���� �� ���ܵ� �����ϱ���
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
