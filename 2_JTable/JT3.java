package jtable;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;
import java.sql.*;
import java.io.*;

public class JT3 extends JFrame implements ActionListener, KeyListener, MouseListener{
	Container cp;
	DefaultTableModel dftm;
	JButton insertB, updateB, deleteB;
	JPanel jp, jpBB, jpBT;
	JScrollPane tsp;
	JTable t;
	JTextField insertT, updateT, deleteT, search;
	JComboBox combo;

	Vector<String> columnNames = new Vector<String>();
	Vector<Vector> rowDate = new Vector<Vector>();
	Connection con;
	Statement stmt;
	JT3(){
		input();
		setTable();
		init();
		setUI();
	}
	void input(){
		try{
			FileReader fr = new FileReader("setting.txt");
			BufferedReader br = new BufferedReader(fr);
			String msg="";
			String url="";
			String uid="";
			String upwd="";
			while((msg = br.readLine())!= null){  // strip은 trim보다 더 많은 공백을 제거할 수 있다.
				msg = msg.strip();
				if(msg.startsWith("jdbc")){
					url = msg;
				}else if(msg.startsWith("s")){
					uid = msg;
				}else if(msg.startsWith("t")){
					upwd = msg;
				}
			}
			connect(url, uid, upwd);
		}catch(FileNotFoundException fe){
		}catch(IOException ie){
		}
	}
	void connect(String url, String uid, String upwd){
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");  // 파라미터는 데이터베이스에따라 다름
			con = DriverManager.getConnection(url, uid, upwd);
			pln("데이터베이스에 접속성공");
		}catch(ClassNotFoundException cnfe){
			pln("드라이버로딩 실패(클래스를 못 찾음): " + cnfe);
		}catch(SQLException se){
			pln("Oracle 접속 정보가 잘못됨 다시 입력하세요.");
		}
	}
	void setTable(){
		ResultSet rs=null;
		try{
			stmt = con.createStatement();
			rs = stmt.executeQuery("select * from DEPT order by DEPTNO");
			ResultSetMetaData rsmd = rs.getMetaData();
			int index = rsmd.getColumnCount();
			rowDate.clear();
			columnNames.clear();
			for(int i=1; i<=index; i++) columnNames.add(rsmd.getColumnName(i));
			while(rs.next()){
				Vector<String> v = new Vector<String>();
				for(int j=1; j<=index; j++){
					v.add(rs.getString(j));
				}
				rowDate.add(v);
			}
		}catch(SQLException se){
			pln("se1: " + se);
		}finally {
			try {
				rs.close();
			}catch(SQLException se){
				pln("se2: " + se);
			}
		}
	}
	void init(){
		cp = getContentPane();
		dftm = new DefaultTableModel(rowDate, columnNames);
		t = new JTable(dftm);
		tsp = new JScrollPane(t);
		//t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //하나의 행만 선택할 수 있게 하는거
		t.addMouseListener(this);
		
		jp = new JPanel();
		jpBB = new JPanel();
		jpBT = new JPanel();
		jpBB.setLayout(new GridLayout(2,3));
		jpBT.setLayout(new GridLayout(1,2));

		insertB = new JButton("추가");
		updateB = new JButton("수정");
		deleteB = new JButton("삭제");
		insertB.addActionListener(this);
		updateB.addActionListener(this);
		deleteB.addActionListener(this);

		insertT = new JTextField();
		updateT = new JTextField();
		deleteT = new JTextField();
		
		
		search = new JTextField();
		search.addKeyListener(this);

		combo = new JComboBox();
		combo.setModel(new DefaultComboBoxModel(columnNames));
		
		jpBB.add(insertT); jpBB.add(updateT); jpBB.add(deleteT);
		jpBB.add(insertB); jpBB.add(updateB); jpBB.add(deleteB); 
		jpBT.add(combo); jpBT.add(search);
		
		cp.add(jpBT, BorderLayout.NORTH);
		cp.add(jpBB, BorderLayout.SOUTH);
		cp.add(tsp, BorderLayout.CENTER);
	}
	void setUI(){
		setTitle("JT3");
		setSize(350, 500);
		setVisible(true);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	@Override
	public void actionPerformed(ActionEvent e){
		String deptno = insertT.getText();
		String dname = updateT.getText();
		String loc = deleteT.getText();
		if(e.getSource() == insertB){
			insertM(deptno, dname, loc);
			insertT.setEditable(true);
			insertT.setText("");
			updateT.setText("");
			deleteT.setText("");
		}else if(e.getSource() == updateB){
			updateM(deptno, dname, loc);
			insertT.setEditable(true);
			insertT.setText("");
			updateT.setText("");
			deleteT.setText("");
		}else if(e.getSource() == deleteB){
			deleteM(deptno, dname, loc);
			insertT.setEditable(true);
			insertT.setText("");
			updateT.setText("");
			deleteT.setText("");
		}
	}
	@Override
	public void keyPressed(KeyEvent e){}
	@Override
	public void keyReleased(KeyEvent e){
		String like = search.getText();
		insertT.setEditable(true);
		insertT.setText("");
		updateT.setText("");
		deleteT.setText("");
		if(like.length() == 0){
			setTable();
			dftm.setDataVector(rowDate,columnNames);
		}else{
			String cname = (String)combo.getSelectedItem();
			if(cname.equals("DEPTNO")){
				like = like.trim();
				try {
					int likeI = Integer.parseInt(like);
				}catch(NumberFormatException nfe) {
					noSearch();
				}
				if(like.length() == 0) noSearch();
				else selectM(cname, like);
			}else if(cname.equals("DNAME")){
				like = like.trim();
				selectM(cname, like);
			}else if(cname.equals("LOC")){
				like = like.trim();
				selectM(cname, like);
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent e){}
	@Override
	public void mouseClicked(MouseEvent e ){
		int row = t.getSelectedRow();
		for(int i=0; i<t.getColumnCount();i++) {
			String val = (String) t.getValueAt(row, i);
			if(i==0) {
				insertT.setEditable(false);
				insertT.setText(val);
			}else if(i==1) {
				updateT.setText(val);
			}else if(i==2) {
				deleteT.setText(val);
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e ){}
	@Override
	public void mouseExited(MouseEvent e ){}
	@Override
	public void mousePressed(MouseEvent e ){}
	@Override
	public void mouseReleased(MouseEvent e ){}
	
	void noSearch() {
		Vector<String> v = new Vector<String>();
		rowDate.add(v);
		dftm.setDataVector(rowDate,columnNames);
	}
	void insertM(String deptno, String dname, String loc){
		try {
			int deptnoI =0;
			try {
				deptnoI = Integer.parseInt(deptno);
			}catch(NumberFormatException nfe) {
				JOptionPane.showMessageDialog(null, " DEPTNO에는 숫자만 입력하세요. ", "ERROR!", JOptionPane.WARNING_MESSAGE);
				return;
			}
			String sql = "insert into DEPT values("+deptnoI+", '"+dname+"', '"+loc+"')";
			stmt.executeUpdate(sql);
			setTable();
			dftm.setDataVector(rowDate,columnNames);
		}catch(SQLException se) {
			JOptionPane.showMessageDialog(null, " 무결성 제약조건에 위배 되었거나, 열의 값이 너무 큽니다. ", "ERROR!", JOptionPane.WARNING_MESSAGE);
		}
	}
	void updateM(String deptno, String dname, String loc){
		try {
			String sql = "update DEPT set " +"DNAME='"+dname+"', LOC= '"+loc+"'"+" where DEPTNO="+deptno;
			stmt.executeUpdate(sql);
			setTable();
			dftm.setDataVector(rowDate,columnNames);
		}catch(SQLException se) {
			pln("se: "+se);
		}
	}
	void deleteM(String deptno, String dname, String loc){
		try {
			String sql = "delete from DEPT where DEPTNO="+deptno+" or DNAME='"+dname+"' or LOC= '"+loc+"'";
			stmt.executeUpdate(sql);
			setTable();
			dftm.setDataVector(rowDate,columnNames);
		}catch(SQLException se) {
			pln("se: "+se);
		}
	}
	void selectM(String cname, String like){
		ResultSet rs=null;
		try{
			String sql = "select * from DEPT where "+cname+" like '%"+like+"%'";
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int index = rsmd.getColumnCount();
			rowDate.clear();
			while(rs.next()){
				Vector<String> v = new Vector<String>();
				for(int j=1; j<=index; j++){
					v.add(rs.getString(j));
				}
				rowDate.add(v);
			}
			dftm.setDataVector(rowDate,columnNames);
		}catch(SQLException se){
		}finally {
			try {
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
		new JT3();
	}
}

