package publicBusDataProject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBUtil {
    public static Connection getConnection() {
        // 오라클 데이타베이스 정보(주소, 유저, 패스워드)
        String filePath = "D:/doitjava/publicBusDataProject/src/publicBusDataProject/homework.properties";
        // 데이타베이스 오라클 객체참조변수
        Connection con = null;
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(filePath));
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            // 오라클에서 제공해주는 jdbc 라이브러리클래스를 로드시켜야된다.
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // System.out.println("데이타베이스 드라이버 로드 성공");
            con = DriverManager.getConnection(url, user, password);
            // System.out.println("데이타베이스 접속 성공");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("데이타베이스 드라이버 로드 실패");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("데이타베이스 연결 실패");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }
}

