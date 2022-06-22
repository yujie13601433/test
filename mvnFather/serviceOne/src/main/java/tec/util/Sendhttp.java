package tec.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import mod.util.//Monitor.//Monitor;

public class Sendhttp
{

	public static String ENCODING = "UTF-8";

	private static MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	private int size = 60;

	private static int maxThreadsTotal = 1000;
	private static int maxThreadsPerHost = 100;
	private static int soTimeout = 2000;
	private static int connectionTimeout = 1000;
	static
	{
		
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setConnectionTimeout(connectionTimeout);
		params.setSoTimeout(soTimeout);
		params.setDefaultMaxConnectionsPerHost(maxThreadsPerHost);
		params.setMaxTotalConnections(maxThreadsTotal);
	}
	private static HttpClient client = new HttpClient(connectionManager);

	private static MultiThreadedHttpConnectionManager[] connectionManagers = null;

	private static HttpClient[] clients = null;






	private static HttpURLConnection getHttpConn(URL url, int time) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		conn.setDoOutput(true);
		// Post 请求不能使用缓存
		conn.setUseCaches(false);
		conn.setConnectTimeout(time);
		conn.setReadTimeout(time);
		// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return conn;
	}

	private static HttpURLConnection getHttpPostConn(URL url) throws IOException
	{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		conn.setDoOutput(true);
		// Post 请求不能使用缓存
		conn.setUseCaches(false);
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		// URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		return conn;

	}



	@PostConstruct
	protected void configureClient()
	{
		try
		{
			maxThreadsTotal = 1000;
			maxThreadsPerHost = 100;
			soTimeout = 3000;
			connectionTimeout = 3000;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			HttpConnectionManagerParams params = connectionManager.getParams();
			params.setConnectionTimeout(connectionTimeout);
			params.setSoTimeout(soTimeout);
			params.setMaxTotalConnections(maxThreadsTotal);
			if (maxThreadsTotal > maxThreadsPerHost)
			{
				params.setDefaultMaxConnectionsPerHost(maxThreadsPerHost);
			}
			else
			{
				params.setDefaultMaxConnectionsPerHost(maxThreadsTotal);
			}
			HostConfiguration hostConf = client.getHostConfiguration();
			ArrayList<Header> headers = new ArrayList<Header>();
			headers.add(new Header("Accept-Language", "en-us,zh-cn,zh-tw,en-gb,en;q=0.7,*;q=0.3"));
			headers.add(new Header("Accept-Charset", "big5,gb2312,gbk,utf-8,ISO-8859-1;q=0.7,*;q=0.7"));
			// headers.add(new Header(
			// "Accept",
			// "text/html,application/xml;q=0.9,application/xhtml+xml,text/xml;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5"));
			headers.add(new Header("Accept-Encoding", "x-gzip,   gzip"));
			headers.add(new Header("Connection", "close"));
			hostConf.getParams().setParameter("http.default-headers", headers);
			initConnectionManagers();
			initClients();
		}
	}

	private void initConnectionManagers()
	{
		if (connectionManagers == null)
		{
			connectionManagers = new MultiThreadedHttpConnectionManager[size];
			for (int i = 0; i < size; i++)
			{
				connectionManagers[i] = new MultiThreadedHttpConnectionManager();
				HttpConnectionManagerParams params = connectionManagers[i].getParams();
				params.setConnectionTimeout(3000);
				params.setSoTimeout(3000);
				params.setMaxTotalConnections(maxThreadsTotal / size * 2);
				params.setDefaultMaxConnectionsPerHost(maxThreadsPerHost / size * 2);
			}

		}
	}

	private void initClients()
	{
		if (clients == null)
		{
			clients = new HttpClient[size];
			for (int i = 0; i < size; i++)
			{
				clients[i] = new HttpClient(connectionManagers[i]);
			}
		}
	}




	@SuppressWarnings("deprecation")
	public String sendXMLUTF(String xmlRequest, String url, int timeout)
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url.split("\\?")[0]);
		configureClient();

		if (timeout > size - 1 || timeout < 1)
		{
			return null;
		}
		PostMethod post = new PostMethod(url);
		InputStream in = null;
		BufferedReader reader = null;
		String retStr = null;
		try
		{
			byte[] xmlRequestByte = xmlRequest.getBytes("utf-8");
			Integer length = xmlRequestByte.length;
			post.setRequestBody(new ByteArrayInputStream(xmlRequestByte));
			if (length < Integer.MAX_VALUE)
			{
				post.setRequestContentLength(length);
			}
			else
			{
				post.setRequestContentLength(EntityEnclosingMethod.CONTENT_LENGTH_CHUNKED);
			}
			post.setRequestHeader("Content-type", "text/xml; charset=utf-8");
			post.setRequestHeader("Content-length", "" + length);
			post.setRequestHeader("connection", "close");
			int reCode = 0;
			reCode = clients[timeout - 1].executeMethod(post);
			if (reCode == HttpStatus.SC_OK)
			{
				in = post.getResponseBodyAsStream();
				if (in != null)
				{
					reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
					if (reader != null)
					{
						String line = null;
						StringBuffer sb = new StringBuffer();
						while ((line = reader.readLine()) != null)
						{
							sb.append("\n" + line);
						}
						retStr = sb.toString();
						//Monitor.//MonitorEnd(true, reCode + "");
						return retStr;
					}
				}
			}
			//Monitor.//MonitorEnd(false, reCode + "");
			return retStr;
		}
		catch (Exception e)
		{
			retStr = null;
		}
		finally
		{
			try
			{
				if (in != null)
				{
					in.close();
				}
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				post.releaseConnection();
			}
		}
		//Monitor.//MonitorEnd(false, "false");
		return retStr;
	}


	// 读取超时
	private final static int SOCKET_TIMEOUT = 2000;
	// 连接超时
	private final static int CONNECTION_TIMEOUT = 1000;
	// 每个HOST的最大连接数量
	private final static int MAX_CONN_PRE_HOST = 100;
	// 连接池的最大连接数
	private final static int MAX_CONN = 100;
	// 连接池
	private final static HttpConnectionManager httpConnectionManager;

	static
	{
		httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = httpConnectionManager.getParams();
		params.setConnectionTimeout(CONNECTION_TIMEOUT);
		params.setSoTimeout(SOCKET_TIMEOUT);
		params.setDefaultMaxConnectionsPerHost(MAX_CONN_PRE_HOST);
		params.setMaxTotalConnections(MAX_CONN);
	}




	public static String doPostRequestPoolNewSetTime(String url, String jsonData, Map headMap, int time)
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url.split("\\?")[0]);
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		String Content_Type = "text/json";
		try
		{
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setConnectionTimeout(time);
			params.setSoTimeout(time);
			httpClient = new HttpClient(httpConnectionManager);
			postMethod = new PostMethod(url);
			// 传递参数
			postMethod.setRequestHeader("Accept-Charset", "UTF-8");
			if (headMap != null && !headMap.isEmpty())
			{
				Iterator<String> iterator = headMap.keySet().iterator();
				/**
				 * 因该请求采用新的RequestEntity进行请求，可直接在RequestEntity中填写Content-Type，不需要在head头中再次填充。
				 */
				String key = null;
				while (iterator.hasNext())
				{
					key = iterator.next();
					if ("Content-Type".equals(key))
					{
						Content_Type = (String) headMap.get(key);
					}
					else
					{
						postMethod.setRequestHeader(key, (String) headMap.get(key));
					}
				}
			}
			StringRequestEntity entity = new StringRequestEntity(jsonData, Content_Type, "utf-8");
			postMethod.setRequestEntity(entity);

			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = postMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					//Monitor.//MonitorEnd(true, statusCode + "");
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			//Monitor.//MonitorEnd(false, statusCode + "");
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			postMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		//Monitor.//MonitorEnd(false, false + "");
		return response;
	}


	public static String doGetRequestPool(String url, Map<String, Object> jsonData, int timeout)
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url.split("\\?")[0]);
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try
		{
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setConnectionTimeout(timeout);

			httpClient = new HttpClient(httpConnectionManager);

			// 设置参数信息
			StringBuffer sb = new StringBuffer("");
			if (jsonData != null && !jsonData.isEmpty())
			{

				Iterator<String> iterator = jsonData.keySet().iterator();
				String key = null;
				while (iterator.hasNext())
				{
					key = iterator.next();
					sb.append("&");
					sb.append(key);
					sb.append("=");
					sb.append((String) jsonData.get(key));

				}

				if (sb.length() > 0)
				{
					sb.deleteCharAt(0);
				}
			}

			if (sb.length() > 0)
			{
				url = url + "?" + sb.toString();
			}

			getMethod = new GetMethod(url);
			statusCode = httpClient.executeMethod(getMethod);

			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = getMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					//Monitor.//MonitorEnd(true, statusCode + "");
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			//Monitor.//MonitorEnd(false, statusCode + "");
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			getMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		//Monitor.//MonitorEnd(false, false + "");
		return response;
	}

	// HTTP发送JSON
	public static String sendMsg(String url1, Map<String, String> map, String json, int timeout) throws IOException
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url1.split("\\?")[0]);
		StringBuffer sb = null;
		int connectTimeoutMs = timeout;
		int readTimeoutMs = timeout;
		// 创建连接
		URL url = new URL(url1);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);//是否重定向
		connection.setRequestProperty("Content-Type", "application/json");
		for (String key : map.keySet())
		{
			connection.setRequestProperty(key, map.get(key));
		}
		connection.setConnectTimeout(connectTimeoutMs);
		connection.setReadTimeout(readTimeoutMs);
		connection.connect();

		// POST请求
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		out.writeBytes(json);
		out.flush();
		out.close();

		// 读取响应
		//		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gbk"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String lines;
		sb = new StringBuffer("");
		while ((lines = reader.readLine()) != null)
		{
			// lines = new String(lines.getBytes(), "UTF-8");
			sb.append(lines);
		}
		reader.close();
		// 断开连接
		connection.disconnect();
		//Monitor.//MonitorEnd(true, true + "");
		return sb.toString();
	}

	public static String doGetRequestPoolNew(String url, Map<String, Object> jsonData, int timeout)
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url.split("\\?")[0]);
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try
		{
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setConnectionTimeout(timeout);
			params.setSoTimeout(timeout);
			httpClient = new HttpClient(httpConnectionManager);

			// 设置参数信息
			StringBuffer sb = new StringBuffer("");
			if (jsonData != null && !jsonData.isEmpty())
			{

				Iterator<String> iterator = jsonData.keySet().iterator();
				String key = null;
				while (iterator.hasNext())
				{
					key = iterator.next();
					sb.append(key);
					sb.append("=");
					sb.append((String) jsonData.get(key));
					sb.append("&");
				}

			}

			if (sb.length() > 0)
			{
				if (url.indexOf("?") < 0)
				{
					url = url + "?" + sb.toString();
				}
				else
				{
					url = url + "&" + sb.toString();
				}

			}

			getMethod = new GetMethod(url);

			statusCode = httpClient.executeMethod(getMethod);
			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = getMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					//Monitor.//MonitorEnd(true, statusCode + "");
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			//Monitor.//MonitorEnd(false, statusCode + "");
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			getMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		//Monitor.//MonitorEnd(false, false + "");
		return response;
	}

	public static String doPostRequestPoolGBKSetTime(String url, String jsonData, int time)
	{
		//Monitor.//MonitorBegin(//Monitor.JAVA_HTTP, url.split("\\?")[0]);
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try
		{
			HttpConnectionManagerParams params = httpConnectionManager.getParams();
			params.setSoTimeout(time);
			httpClient = new HttpClient(httpConnectionManager);

			postMethod = new PostMethod(url);

			// 传递参数
			StringRequestEntity entity = new StringRequestEntity(jsonData, "text/json", "GBK");
			postMethod.setRequestEntity(entity);
			postMethod.setRequestHeader("Accept-Charset", "GBK");
			postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + "GBK");
			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = postMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "GBK");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					//Monitor.//MonitorEnd(true, statusCode + "");
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			//Monitor.//MonitorEnd(false, statusCode + "");
			return response;

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			postMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		//Monitor.//MonitorEnd(false, false + "");
		return response;
	}



	/**
	 * 发送Http（生活专用）
	 *
	 * @param httpurl  httpurl
	 * @param paramMap 参数映射
	 * @return {@link String}
	 */
	public static String sendPostWithChannel(String httpurl, String paramMap)
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(httpurl);
		httpPost.setHeader("channel", "jsmcc_channel");
		httpPost.setHeader("Content-Type", "application/json");
		// 前两个是连接超时时间，后一个 是请求超时时间
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		httpPost.setConfig(requestConfig);
		CloseableHttpResponse response = null;
		String httpStr = null;
		try
		{
			// httpPost.setConfig(requestConfig);
			StringEntity stringEntity = new StringEntity(paramMap, "UTF-8");//
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("jsmcc_channel");
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200)
			{
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity == null)
			{
				return null;
			}
			httpStr = EntityUtils.toString(entity, "utf-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (response != null)
			{
				try
				{
					EntityUtils.consume(response.getEntity());
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return httpStr;
	}

	public static String doGetRequestPoolHeBao(String url, Map<String, String> jsonData, String appid, String sign)
	{
		HttpClient httpClient = null;
		GetMethod getMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try
		{
			httpClient = new HttpClient(httpConnectionManager);

			// 设置参数信息
			StringBuffer sb = new StringBuffer("");
			if (jsonData != null && !jsonData.isEmpty())
			{

				Iterator<String> iterator = jsonData.keySet().iterator();
				String key = null;
				while (iterator.hasNext())
				{
					key = iterator.next();
					sb.append("&");
					sb.append(key);
					sb.append("=");
					sb.append(URLEncoder.encode(jsonData.get(key), "UTF-8"));

				}

				if (sb.length() > 0)
				{
					sb.deleteCharAt(0);
				}
			}

			if (sb.length() > 0)
			{
				url = url + "?" + sb.toString();
			}

			getMethod = new GetMethod(url);
			getMethod.setRequestHeader("x-dev-id", appid);
			getMethod.setRequestHeader("x-lemon-sign", sign);

			statusCode = httpClient.executeMethod(getMethod);

			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = getMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			getMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		return response;
	}

	public static String doPostRequestPoolHeBao(String url, String jsonData, String appid, String sign)
	{
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		int statusCode = 0;
		String response = "";
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try
		{
			httpClient = new HttpClient(httpConnectionManager);

			postMethod = new PostMethod(url);
			postMethod.setRequestHeader("x-dev-id", appid);
			postMethod.setRequestHeader("x-lemon-sign", sign);

			StringRequestEntity entity = new StringRequestEntity(jsonData, "application/json", "UTF-8");
			postMethod.setRequestEntity(entity);

			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK)
			{
				// statusCode=200 返回成功
				String tmp = null;
				// 读取返回报文
				StringBuffer resp = new StringBuffer();
				try
				{
					inputStream = postMethod.getResponseBodyAsStream();
					inputStreamReader = new InputStreamReader(inputStream, "utf-8");
					bufferedReader = new BufferedReader(inputStreamReader);
					while ((tmp = bufferedReader.readLine()) != null)
					{
						resp.append(tmp).append("\n");
					}
					response = resp.toString();
					return response;
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			return response;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// 关闭client链接 关闭流
			postMethod.releaseConnection();
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(inputStreamReader);
			IOUtils.closeQuietly(bufferedReader);
		}
		return response;
	}
}
