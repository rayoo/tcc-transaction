package com.yonyou.biz.utils.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;

/**
 * HttpClient 工具类
 * 
 * @author rayoo
 */
public class HttpClient4Utils {

	/**
	 * 从ConnectionManager获取Connection超时时间
	 */
	public static final int CONNECTION_REQUEST_TIMEOUT = 2000;

	/**
	 * 连接服务器超时时间
	 */
	public static final int CONNECTION_TIMEOUT_DEFAULT = 3000;

	/**
	 * 请求发送成功后等待数据包返回的超时时间
	 */
	public static final int SOCKET_TIMEOUT_DEFAULT = 4000;

	/**
	 * socket.close()返回时, 设置TCP最长延迟关闭的时间，没超时则等待套接字发送缓冲区中的数据发送完成，超时则最长等待此规定时间后调用close, 设置为0则直接关闭不等待。
	 */
	public static final int SO_LINGER = 0;

	/**
	 * 无限超时时间
	 */
	public static final int INFINITE_TIMEOUT = 0;

	/**
	 * 接收/传输数据时使用的默认socket缓冲字节大小
	 */
	public static final int SOCKET_BUFFER_SIZE_DEFAULT = 8192;

	/**
	 * 请求失败后重新尝试的次数
	 */
	public static final int RETRY_COUNT_DEFAULT = 2;

	/**
	 * 默认字符集
	 */
	public static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

	/**
	 * Http内容类型
	 */
	public static final String HTTP_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET;

	/**
	 * 默认代理名称
	 */
	public static final String DEFAULT_AGENT = "auth-sdk-server.iuap.httpclient";

	/**
	 * URL分隔符
	 */
	public static final String URL_SEPARATOR = "/";

	/**
	 * Cookie的头名称
	 */
	public static final String HEADER_COOKIE = "Cookie";

	public static final CloseableHttpClient httpClient = buildHttpClient(SOCKET_TIMEOUT_DEFAULT);

	public static final CloseableHttpClient httpClientNoTimeout = buildHttpClient(INFINITE_TIMEOUT);

	public static final String COOKIE_STORE_FIELD = "cookieStore";

	/**
	 * 最大连接数
	 */
	private static final int MAX_CONN_TOTAL = 2000;

	private static CloseableHttpClient buildHttpClient(int socketTimeout) {
		// 设置最大连接数和每个host的最大连接数
		HttpClientBuilder httpClientBuilder = HttpClients.custom().setMaxConnTotal(MAX_CONN_TOTAL).setMaxConnPerRoute(MAX_CONN_TOTAL);
		// 内部默认使用 PoolingHttpClientConnectionManager 作为其连接管理器, 再次设置会覆盖下面其它参数的设置
		// httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager());
		// 设置服务器连接超时时间 及 服务器响应超时时间/* .setStaleConnectionCheckEnabled(false) */
		httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom().setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT).setConnectTimeout(CONNECTION_TIMEOUT_DEFAULT)
				.setSocketTimeout(socketTimeout).build());
		// 设置在关闭TCP连接时最大停留时间,是否禁用优化算法延迟发送数据 及 在非阻塞I/O情况下的服务器响应时间
		httpClientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoLinger(SO_LINGER).setSoKeepAlive(false).setTcpNoDelay(true).setSoTimeout(socketTimeout).build());
		// 设置接收/传输数据时的buffer大小,及默认字符集
		httpClientBuilder.setDefaultConnectionConfig(ConnectionConfig.custom().setBufferSize(SOCKET_BUFFER_SIZE_DEFAULT).setCharset(Charset.forName(DEFAULT_CHARSET)).build());
		// 设置失败后重新尝试访问的处理器,不使用已经请求的连接
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(RETRY_COUNT_DEFAULT, false));
		// 代理名称
		httpClientBuilder.setUserAgent(DEFAULT_AGENT);

		ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
				return 5 * 1000; // tomcat默认keepAliveTimeout为20s
			}

		};
		httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);

		// 构建HttpClient
		return httpClientBuilder/* .setConnectionManagerShared(true) */.build();
	}

	public static CloseableHttpClient getHttpClient() {
		return httpClient;
	}

	public static CloseableHttpClient getHttpClientNoTimeout() {
		return httpClientNoTimeout;
	}

	/**
	 * post发送Form表单
	 * 
	 * @param url
	 *            发送的URL
	 * @param paramsPair
	 *            发送的键值对
	 * @return HttpEntity 使用完毕后记得使用 EntityUtils.consume(httpEntity); 关闭流
	 */
	public static HttpResult postForm(String url, List<NameValuePair> paramsPair, EnumConntionType conntionType) {
		return postForm(url, paramsPair, null, conntionType);
	}

	/**
	 * post发送Form表单
	 * 
	 * @param url
	 *            发送的URL
	 * @param paramsPair
	 *            发送的键值对
	 * @param headers
	 *            发送的头信息
	 * @return HttpEntity 使用完毕后记得使用 EntityUtils.consume(httpEntity); 关闭流
	 */
	public static HttpResult postForm(String url, List<NameValuePair> paramsPair, List<Header> headers, EnumConntionType conntionType) {
		return postForm(url, paramsPair, headers, conntionType, false);
	}

	public static HttpResult postForm(String url, List<NameValuePair> paramsPair, List<Header> headers, EnumConntionType conntionType, boolean clearCookieStore) {
		try {
			HttpEntity httpEntity = null;
			if (!CollectionUtils.isEmpty(paramsPair)) {
				httpEntity = new UrlEncodedFormEntity(paramsPair, DEFAULT_CHARSET);
			}
			if (null == headers) {
				headers = Lists.newArrayList();
			}
			headers.add(new BasicHeader("Content-Type", HTTP_CONTENT_TYPE));
			return postForm(url, httpEntity, headers, conntionType, clearCookieStore);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <pre>
	 * 上传文件用法:
	 * HttpEntity httpEntity = MultipartEntityBuilder.create();
	 * httpEntity.addBinaryBody(multipartFile.getName(), multipartFile.getBytes(), ContentType.create(multipartFile.getContentType()), multipartFile.getOriginalFilename()).build();
	 * </pre>
	 * 
	 * @param url
	 * @param httpEntity
	 * @param headers
	 * @param conntionType
	 * @see org.apache.http.entity.mime.MultipartEntityBuilder
	 */
	public static HttpResult postForm(String url, HttpEntity httpEntity, List<Header> headers, EnumConntionType conntionType) {
		return postForm(url, httpEntity, headers, conntionType, false);
	}

	public static HttpResult postForm(String url, HttpEntity httpEntity, List<Header> headers, EnumConntionType conntionType, boolean clearCookieStore) {
		HttpPost httpPost = null;
		CloseableHttpResponse httpResponse = null;
		try {
			httpPost = new HttpPost(url);

			if (null != httpEntity) {
				httpPost.setEntity(httpEntity);
			}
			// httpPost.setProtocolVersion(HttpVersion.HTTP_1_1); // 自动使用Http1.1协议
			if (!CollectionUtils.isEmpty(headers)) {
				for (Header header : headers) {
					httpPost.addHeader(header);
				}
			}

			HttpClientContext httpContext = null;
			if (clearCookieStore) { // 清除cookie的存储, 否则上一次的cookie会覆盖本次的cookie的值,参见:org.apache.http.impl.client.InternalHttpClient.setupContext(HttpClientContext)
				// HttpClientContext localcontext = HttpClientContext.adapt(httpContext);
				httpContext = HttpClientContext.create();
				httpContext.setCookieStore(new BasicCookieStore());
			}

			httpResponse = chooseHttpClient(conntionType).execute(httpPost, httpContext);

			HttpResult httpResult = new HttpResult();
			httpResult.setStatusCode(httpResponse.getStatusLine().getStatusCode());
			httpResult.setResponseString(httpEntityToString(httpResponse.getEntity()));
			return httpResult;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			closeHttpComponent(httpPost, httpResponse, (null == httpResponse ? null : httpResponse.getEntity()));
		}
	}

	/**
	 * post发送Form表单
	 * 
	 * @param url
	 *            发送的地址
	 * @param paramsPair
	 *            发送的键值对
	 * @param responseHandler
	 *            处理响应的Handler
	 * @return 使用responseHandler处理后的返回值
	 */
	public static <T> T postFormCallback(String url, List<NameValuePair> paramsPair, ResponseHandler<T> responseHandler, EnumConntionType conntionType) {
		return postFormCallback(url, paramsPair, null, responseHandler, conntionType);
	}

	/**
	 * post发送Form表单
	 * 
	 * @param url
	 *            发送的地址
	 * @param paramsPair
	 *            发送的键值对
	 * @param headers
	 *            发送的头信息
	 * @param responseHandler
	 *            处理响应的Handler
	 * @return 使用responseHandler处理后的返回值
	 */
	public static <T> T postFormCallback(String url, List<NameValuePair> formParams, List<Header> headers, ResponseHandler<T> responseHandler, EnumConntionType conntionType) {
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			if (!CollectionUtils.isEmpty(formParams)) {
				httpPost.setEntity(new UrlEncodedFormEntity(formParams, DEFAULT_CHARSET));
			}
			// httpPost.setProtocolVersion(HttpVersion.HTTP_1_1);
			httpPost.setHeader("Content-Type", HTTP_CONTENT_TYPE);
			if (!CollectionUtils.isEmpty(headers)) {
				for (Header header : headers) {
					httpPost.addHeader(header);
				}
			}

			T respHandlerRet = chooseHttpClient(conntionType).execute(httpPost, responseHandler);

			return respHandlerRet;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			closeHttpComponent(httpPost, null, null);
		}
	}

	/**
	 * 转换HttpEntity 为String类型
	 * 
	 * @param httpEntity
	 * @return
	 */
	public static String httpEntityToString(HttpEntity httpEntity) {
		try {
			return EntityUtils.toString(httpEntity, DEFAULT_CHARSET);
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	public static CloseableHttpClient chooseHttpClient(EnumConntionType conntionType) {
		if (null == conntionType || conntionType == EnumConntionType.SHORT) {
			return httpClient;
		}
		return httpClientNoTimeout;
	}

	public static boolean closeHttpComponent(HttpRequestBase httpRequestBase, CloseableHttpResponse httpResponse, HttpEntity httpEntity) {
		boolean ret = true;
		try {
			if (null != httpRequestBase) {
				httpRequestBase.abort(); // .reset();
			}
		} catch (Exception e) {

			ret = false;
		}
		try {
			if (null != httpResponse) {
				httpResponse.close();
			}
		} catch (IOException e) {

			ret = false;
		}
		try {
			if (null != httpEntity) {
				EntityUtils.consume(httpEntity);
			}
		} catch (IOException e) {

			ret = false;
		}
		return ret;
	}

	/**
	 * @return 在系统停止时关闭HttpClient
	 */
	public static boolean shutdown() {
		try {
			if (null != httpClient) {
				httpClient.close();
			}
			if (null != httpClientNoTimeout) {
				httpClientNoTimeout.close();
			}
		} catch (Exception e) {

			return false;
		}
		return true;
	}

	/**
	 * 追加url
	 * 
	 * @param url
	 * @param uris
	 * @return
	 */
	public static String buildURL(String url, List<String> uris, List<NameValuePair> queryParams) {
		try {
			if (StringUtils.isNotBlank(url)) {
				StringBuilder ret = new StringBuilder(url).append(HttpClient4Utils.URL_SEPARATOR);
				if (!CollectionUtils.isEmpty(uris)) {
					for (String uri : uris) {
						if (StringUtils.isNotBlank(uri)) {
							ret.append(StringUtils.trimToEmpty(uri)).append(HttpClient4Utils.URL_SEPARATOR);
						}
					}
				}
				if (!CollectionUtils.isEmpty(queryParams)) {
					URIBuilder uriBuilder = new URIBuilder(ret.toString()).setCharset(StandardCharsets.UTF_8);
					return uriBuilder.setParameters(queryParams).build().toString();
				}
				return ret.toString();
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
