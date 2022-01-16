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

//1. �������� �̸����� �´� �� üŷ 
//2. ������ ����� �ܾ��� ��ü�ݾ׺��� ���ų� ū�� üŷ  
//3. ������ ����� �ܾ��� - ( up1 )
//4. �޴� ����� �ܾ��� +  ( up2 )
//5. ��ü ��� ( in ) 
//6. ���� ������ ������ 
//7. ���ᰴü�� �ݱ�

//8. ��ü�ϱ� �޼ҵ� for
