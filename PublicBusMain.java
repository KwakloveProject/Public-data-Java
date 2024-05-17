package publicBusDataProject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PublicBusMain {
	public static Scanner input = new Scanner (System.in);
	public static void main(String[] args) 
	{
		ArrayList<BusInfo> busInfoList = webConnention();
		ArrayList<BusInfo> busInfoSelectList= new ArrayList<BusInfo>();
		boolean exitFlag = false;
		while(!exitFlag) 
		{
			System.out.println("1.웹정보가져오기, 2.저장하기 3.테이블읽어오기, 4.수정하기 5.삭제하기, 6.종료");
			System.out.println("선택>>");
			int count =Integer.parseInt(input.nextLine());
			switch(count) 
			{
			case 1:busInfoList = webConnention();
				break;
			case 2:if(busInfoList.size() < 1) 
			{
				System.out.println("공공데이터로부터 가져온 자료가 없습니다");
				continue;
			}
			insertBusInfo(busInfoList);
				break;
			case 3:busInfoSelectList = selectBusInfo();
				printBusInfo(busInfoSelectList);
				break;
			case 4:     
				int data = updateInputNodeno();
            if(data != 0) {
                updateBusInfo(data);
            }
            break;
			
			case 5:deleteBusInfo();
				break;
			case 6:
				exitFlag = true;
				break;
			}
		}
		}
	private static void updateBusInfo(int data) {
		   //버스정보 수정
	    
	        String sql = "UPDATE businfo SET curdate = SYSDATE WHERE nodeno = ?";
	        Connection con = null; 
	        PreparedStatement pstmt = null; 
	        try {
	            con = DBUtil.getConnection();
	            pstmt = con.prepareStatement(sql);
	            pstmt.setInt(1, data);
	            int value = pstmt.executeUpdate();

	            if(value == 1) {
	                System.out.println(data+" 수정완료");
	            }else {
	                System.out.println(data+" 수정실패");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	                try {
	                    if(pstmt != null) {
	                        pstmt.close();
	                    }
	                    if(con != null) {
	                        con.close();
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	        }
	    
		
	}
	private static int updateInputNodeno() {
        ArrayList<BusInfo> busInfoList = selectBusInfo();
        printBusInfo(busInfoList);
        System.out.println("update nodeNo >> ");
        int data = Integer.parseInt(input.nextLine());
        return data;
	}
	private static void deleteBusInfo() {
		//공공데이터 버스정보 삭제하기
	
	        int count = getCountBusInfo();
	        if(count == 0) {
	            System.out.println("버스정보내용이 없습니다.");
	            return; 
	        }
	        String sql = "delete from businfo";
	        Connection con = null; 
	        PreparedStatement pstmt = null; 
	        try {
	            con = DBUtil.getConnection();
	            pstmt = con.prepareStatement(sql);
	            int value = pstmt.executeUpdate();
	            if(value != 0) {
	                System.out.println("모든 버스정보 삭제완료");
	            }else {
	                System.out.println("모든 버스정보 삭제실패");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	                try {
	                    if(pstmt != null) {
	                        pstmt.close();
	                    }
	                    if(con != null) {
	                        con.close();
	                    }
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	        }
	    
	        
	}
	private static int getCountBusInfo() {
		int count = 0; 
        String sql = "select count(*) as cnt from businfo";
        Connection con = null; 
        PreparedStatement pstmt = null; 
        ResultSet rs = null; 
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                count = rs.getInt("cnt");
                System.out.println("count="+count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
                try {
                    if(rs != null) {
                        rs.close();
                    }
                    if(pstmt != null) {
                        pstmt.close();
                    }
                    if(con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return count; 
	}
	private static ArrayList<BusInfo> selectBusInfo() {
        ArrayList<BusInfo> busInfoList = null;
        String sql = "select * from businfo";
        Connection con = null; 
        PreparedStatement pstmt = null; 
        ResultSet rs = null; 
        try {
            con = DBUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            busInfoList = new ArrayList<BusInfo>(); 
            while(rs.next()) {
                BusInfo bif = new BusInfo();
                bif.setNodeno(rs.getInt("NODENO"));
                bif.setGpslati(rs.getDouble("GPSLATI"));
                bif.setGpslong(rs.getDouble("GPSLONG"));
                bif.setNodeid(rs.getString("NODEID"));
                bif.setNodenm(rs.getString("NODENM"));
                bif.setCurdate(rs.getDate("CURDATE"));
                busInfoList.add(bif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
                try {
                    if(rs != null) {
                        rs.close();
                    }
                    if(pstmt != null) {
                        pstmt.close();
                    }
                    if(con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return busInfoList; 
    }
//버스정보 출력하기
    public static void printBusInfo(ArrayList<BusInfo> busInfoSelectList) {
        if(busInfoSelectList.size() < 1) {
            System.out.println("출력할 버스정보가 없습니다.");
            return;
        }
        for( BusInfo data  : busInfoSelectList) {
            System.out.println(data.toString());
        }

    }
	//공공데이터를 테이블 저장하기
    public static void insertBusInfo(ArrayList<BusInfo> busInfoList) {
        if(busInfoList.size() <1) {
            System.out.println("입력할 데이터가 없어요!");
            return;
        }

        Connection con = null; 
        PreparedStatement pstmt = null; 
        try {
            con = DBUtil.getConnection();
            for(BusInfo data  : busInfoList ) {
                String sql = "insert into businfo values(?, ?, ?, ?, ?,?)";
                pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, data.getNodeno());
                pstmt.setDouble(2, data.getGpslati());
                pstmt.setDouble(3, data.getGpslong());
                pstmt.setString(4, data.getNodeid());
                pstmt.setString(5, data.getNodenm());
                pstmt.setDate(6, data.getCurdate());
                int value = pstmt.executeUpdate();

                if(value == 1) {
                    System.out.println(data.getNodenm()+"정류장 등록완료");
                }else {
                    System.out.println(data.getNodenm()+"정류장 등록실패");
                }
            }//end of for
        } catch (SQLException e) {
            e.printStackTrace();
		} finally {
                try {
                    if(pstmt != null) {
                        pstmt.close();
                    }
                    if(con != null) {
                        con.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
    }
	
	public static ArrayList<BusInfo> webConnention()
	{
		// 1. 요청 url 생성
		ArrayList<BusInfo> list = new ArrayList<>();
		
		StringBuilder urlBuilder = new StringBuilder(
				"https://apis.data.go.kr/1613000/BusSttnInfoInqireService/getSttnNoList");
		try {
			urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8")
			+ "=SwVVLmU%2BGO3CAoCXd8fveS7cPiWqMjoStk7ipjzNe7uMlKIsrxfDpdVtXOhVxlgBT5WPd8NsS7%2BokQEiVaR8zQ%3D%3D");
			urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("cityCode", "UTF-8") + "=" + URLEncoder.encode("25", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("nodeNm", "UTF-8") + "=" + URLEncoder.encode("전통시장", "UTF-8"));
			urlBuilder.append("&" + URLEncoder.encode("nodeNo", "UTF-8") + "=" + URLEncoder.encode("44810", "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//2.서버주소 Connection con
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(urlBuilder.toString()); 				//웹서버주소 action 
			conn = (HttpURLConnection) url.openConnection();	// 접속요청 get방식
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//3. 요청내용을 전송 및 응답처리
		BufferedReader br = null;
		try {
			//conn.getResponseCode() 서버에서 상태코드를 알려주는 값
			int statusCode = conn.getResponseCode();
			System.out.println(statusCode);
			if (statusCode >= 200 && statusCode <= 300) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			Document doc = parseXML(conn.getInputStream());
			// a. field 태그객체 목록으로 가져온다.
			NodeList descNodes = doc.getElementsByTagName("item");
			// b. Corona19Data List객체 생성
			// c. 각 item 태그의 자식태그에서 정보 가져오기
			for (int i = 0; i < descNodes.getLength(); i++) {
				// item
				Node item = descNodes.item(i);
				BusInfo busInfo = new BusInfo();
				// item 자식태그에 순차적으로 접근
				for (Node node = item.getFirstChild(); node != null; node = node.getNextSibling()) {
					System.out.println(node.getNodeName() + " : " + node.getTextContent());
					
					switch (node.getNodeName()) {
					case "gpslati":
						busInfo.setGpslati(Double.parseDouble(node.getTextContent())); 
						break;
					case "gpslong":
						busInfo.setGpslong(Double.parseDouble(node.getTextContent()));
						break;
					case "nodeid":
						busInfo.setNodeid(node.getTextContent());
						break;
					case "nodenm":
						busInfo.setNodenm(node.getTextContent());
						break;
					case "nodeno":
						busInfo.setNodeno(Integer.parseInt(node.getTextContent()));
						break;
					}
				}
				// d. List객체에 추가
				list.add(busInfo);
			}
			// e.최종확인
			for (BusInfo data : list) {
				System.out.println(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	//xml을 객체로 바꿔주는 역할
	public static Document parseXML(InputStream inputStream) {
		DocumentBuilderFactory objDocumentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder objDocumentBuilder = null;
		Document doc = null;
		try {
			objDocumentBuilder = objDocumentBuilderFactory.newDocumentBuilder();
			doc = objDocumentBuilder.parse(inputStream);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) { // Simple API for XML e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
		
	}

