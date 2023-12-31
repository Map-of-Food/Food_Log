package Food_Log;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class NaverMap implements ActionListener {

	Review review;
	String address;
	public NaverMap(Review naverMap, String address) {
		this.review = naverMap;
		this.address = address;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		final String clientId = "9mamhjhzl9";
		final String clientSecret = "GHhSOiJz4wQOx8JhDY7ZXGpoxfcTRA2WC2ASgjZW";
		AddressVO vo = null;

		if (review != null) {
			try {
				String address = review.f_address.getText();
				String addr = URLEncoder.encode(address, "UTF-8");
				String apiURL = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + addr;
				URL url = new URL(apiURL);
		
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
				con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
		
				int responseCode = con.getResponseCode();
				BufferedReader br;
				if (responseCode == 200) {
					br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				} else {
					br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				}
		
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
		
				JSONTokener tokener = new JSONTokener(response.toString());
				JSONObject object = new JSONObject(tokener);
		
				JSONArray arr = object.getJSONArray("addresses");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject temp = (JSONObject) arr.get(i);
					vo = new AddressVO();
					vo.setRoadAddress((String) temp.get("roadAddress"));
					vo.setJibunAddress((String)temp.get("jibunAddress"));
					vo.setX((String)temp.get("x"));
					vo.setY((String)temp.get("y"));
				}
		
				map_service(vo);
		
			} catch (Exception err) {
				System.out.println(err);
			}
		}
	}

	public void map_service(AddressVO vo) {
		String URL_STATICMAP = "https://naveropenapi.apigw.ntruss.com/map-static/v2/raster?";

		try {
			String pos = URLEncoder.encode(vo.getX() + " " + vo.getY(), "UTF-8");
			URL_STATICMAP += "center=" + vo.getX() + "," + vo.getY();
			URL_STATICMAP += "&level=16&w=350&h=600"; //사이즈
			URL_STATICMAP += "&markers=type:t|size:mid|pos:" + pos + "|label:" + URLEncoder.encode(vo.getRoadAddress(), "UTF-8");

			URL url = new URL(URL_STATICMAP);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "9mamhjhzl9");
			con.setRequestProperty("X-NCP-APIGW-API-KEY", "GHhSOiJz4wQOx8JhDY7ZXGpoxfcTRA2WC2ASgjZW");

			int responseCode = con.getResponseCode();
			BufferedReader br;

			// 정상호출인 경우.
			if (responseCode == 200) {
				InputStream is = con.getInputStream();

				int read = 0;
				byte[] bytes = new byte[1024];

				// 랜덤 파일명으로 파일 생성
				String tempName = Long.valueOf(new Date().getTime()).toString();
				//String filePath = "./map_img/"+tempName+".jpg";//파일 경로 설정
				File file = new File(tempName+".jpg");	// 파일 생성.

				file.createNewFile();

				OutputStream out = new FileOutputStream(file);

				while ((read = is.read(bytes)) != -1) {
					out.write(bytes, 0, read);	// 파일 작성
				}

				is.close();
				ImageIcon img = new ImageIcon(file.getName());
				review.l_map.setIcon(img);

			}

		} catch(Exception e) {
			System.out.println(e);
		}

	}
}