import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

@SuppressWarnings("deprecation")
public class RequestURL {

	public static void main(String[] args) throws SecurityException,
			IOException {

		ProxyServer server = new ProxyServer(9999);
		server.start();
		server.setCaptureHeaders(true);
		server.setCaptureContent(true);

		@SuppressWarnings("unused")
		Proxy proxy = server.seleniumProxy();

		/*
		 * FirefoxProfile profile=new FirefoxProfile();
		 * profile.setPreference("general.useragent.override",
		 * "Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; GT-N7100 Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
		 * ); FirefoxDriver driver=new FirefoxDriver(profile);
		 */

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--user-agent=Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; GT-N7100 Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

		DesiredCapabilities caps = DesiredCapabilities.chrome();
		caps.setCapability(CapabilityType.PROXY, proxy);
		ArrayList<String> switches = new ArrayList<String>();
		switches.add("--proxy-server=localhost:9999");
		caps.setCapability(ChromeOptions.CAPABILITY , options);
		caps.setCapability("chrome.switches", switches);
		ChromeDriver driver = new ChromeDriver(caps);

		/*
		 * ChromeOptions options = new ChromeOptions(); options.addArguments(
		 * "--user-agent=Mozilla/5.0 (Linux; U; Android 4.3; zh-cn; GT-N7100 Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"
		 * ); ChromeDriver driver = new ChromeDriver(options);
		 */

		String url = "http://www.qq.com";
		server.newHar(url);
		driver.get(url);

		Har har = server.getHar();
		String strFilePath = "har.txt";
		FileOutputStream fos = new FileOutputStream(strFilePath);
		har.writeTo(fos);
		server.stop();
		driver.quit();

	}

}
