import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class B {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		
		StreamManager streamManager=new StreamManager(1000);
		
		URL obj = new URL("http://www.youku.com");
		URLConnection conn = obj.openConnection();
		//conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
		conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(streamManager.registerStream(inputStream));
		streamManager.enable();
		
		
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String contentString=null;
		String aString=null;
		while((aString=bufferedReader.readLine())!=null){
			contentString+=bufferedReader.readLine();
		}
		
		System.out.println("OK");
		
		
	}

}
