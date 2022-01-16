package user;

import bank.Transfer;
import bank.TransferImpl;
import java.sql.*;
import java.io.*;

public class Touser {
	public void Transfer() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		TransferImpl tfi = new TransferImpl();
		try {
			tfi.p("�����»���� �̸����� �Է��ϼ���: ");
			String myEmail = br.readLine();
			tfi.p("�޴»���� �̸����� �Է��ϼ���: ");
			String email = br.readLine();
			tfi.p("������ �ݾ��� �Է��ϼ���: ");
			String moneyStr = br.readLine();
			if(myEmail.length()==0) {
				tfi.pln("�����»���� �̸����� �߸��Ǿ����ϴ�. �ٽ� �Է����ּ���.");
				Transfer();
				return;
			}else if(email.length()==0) {
				tfi.pln("������ �̸����� �߸��Ǿ����ϴ�. �ٽ� �Է����ּ���.");
				Transfer();
				return;
			}else if(moneyStr.length()==0){
				tfi.pln("������ �ݾ��� �Է����ּ���.");
				Transfer();
				return;
			}
			
			long money = Long.parseLong(moneyStr);
			
			boolean flag = tfi.transfer(myEmail, email, money);
			if(flag) {
				tfi.pln("��ü����");
			}else {
				tfi.pln("��ü���� (�̸����� �ٽ� Ȯ���ϼ���)");
			}
		}catch(SQLException se) {
			tfi.pln("��ü���� se: " + se);
		}catch(IOException ie){
		}catch(NumberFormatException nfe) {
			tfi.pln("�ݾ��� ������ �Է����ּ���");
			return;
		}
	}
	public static void main(String[] agrs) {
		new Touser().Transfer();
	}
}
