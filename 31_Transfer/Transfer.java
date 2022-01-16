package bank;
import java.sql.*;

public interface Transfer 
{
    boolean isMember(String email); //1: S
	boolean checkBalance(String sender, long amount); //2: S
    boolean minus(String sender, long amount); //3: U
	boolean plus(String receiver, long amount); //4: U
	boolean log(String sender, String receiver, long amount);//5: I
    void showResult(String sender, String receiver); //6: S
	void closeAll(); //7
    
	boolean transfer(String sender, String receiver, long amount) throws SQLException; //8: for User 
}  

//1. 계좌주의 이메일이 맞는 지 체킹 
//2. 보내는 사람의 잔액이 이체금액보다 같거나 큰지 체킹  
//3. 보내는 사람의 잔액을 - ( up1 )
//4. 받는 사람의 잔액을 +  ( up2 )
//5. 이체 기록 ( in ) 
//6. 계좌 내용을 보여줌 
//7. 연결객체들 닫기

//8. 이체하기 메소드 for
