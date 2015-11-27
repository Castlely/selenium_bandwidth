import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.bouncycastle.jcajce.provider.digest.SHA1;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.local.LocalChannel;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.HashedWheelTimer;



public class A {
	public static void main(String[] args) throws Exception {
		
		ScheduledExecutorService executorService=new ScheduledThreadPoolExecutor(1);
		
		// 2G
		//GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(executorService, 2764, 9830);
		GlobalTrafficShapingHandler trafficHandler = new GlobalTrafficShapingHandler(executorService, 10, 10);
		
		LocalChannel lc=new LocalChannel();
		lc.pipeline().addLast(trafficHandler);
		
		URL obj = new URL("http://www.baidu.com");
		URLConnection conn = obj.openConnection();
		//conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
		conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		InputStream inputStream = conn.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String contentString=null;
		String aString=null;
		while((aString=bufferedReader.readLine())!=null){
			contentString+=bufferedReader.readLine();
		}
		
		trafficHandler.release();
		
		
		
	}
}
