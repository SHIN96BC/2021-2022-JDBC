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
			tfi.p("보내는사람의 이메일을 입력하세요: ");
			String myEmail = br.readLine();
			tfi.p("받는사람의 이메일을 입력하세요: ");
			String email = br.readLine();
			tfi.p("보내실 금액을 입력하세요: ");
			String moneyStr = br.readLine();
			if(myEmail.length()==0) {
				tfi.pln("보내는사람의 이메일이 잘못되었습니다. 다시 입력해주세요.");
				Transfer();
				return;
			}else if(email.length()==0) {
				tfi.pln("상대방의 이메일이 잘못되었습니다. 다시 입력해주세요.");
				Transfer();
				return;
			}else if(moneyStr.length()==0){
				tfi.pln("보내실 금액을 입력해주세요.");
				Transfer();
				return;
			}
			
			long money = Long.parseLong(moneyStr);
			
			boolean flag = tfi.transfer(myEmail, email, money);
			if(flag) {
				tfi.pln("이체성공");
			}else {
				tfi.pln("이체실패 (이메일을 다시 확인하세요)");
			}
		}catch(SQLException se) {
			tfi.pln("이체실패 se: " + se);
		}catch(IOException ie){
		}catch(NumberFormatException nfe) {
			tfi.pln("금액은 정수만 입력해주세요");
			return;
		}
	}
	public static void main(String[] agrs) {
		new Touser().Transfer();
	}
}
