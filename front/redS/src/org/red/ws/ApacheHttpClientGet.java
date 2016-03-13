package org.red.ws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.google.gson.Gson;

public class ApacheHttpClientGet {

	public static String GET(String url, final HttpSession session) {
		String respuesta = "";
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(url);
			getRequest.addHeader("accept", "application/json");
			Header ts= new Header() {				
				@Override
				public String getValue() {
					return (String) session.getAttribute("TokenWS");
				}
				
				@Override
				public String getName() {
					return "autenticar";
				}
				
				@Override
				public HeaderElement[] getElements() throws ParseException {
					return null;
				}
			};
			getRequest.addHeader(ts);
			System.out.println(ts.getName()+":"+ts.getValue());
			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			Header token=response.getFirstHeader("autenticar");
			if(token!=null){
				System.out.println(token.getName()+":"+token.getValue());
				session.setAttribute("TokenWS", token.getValue());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					(response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				respuesta = respuesta + output;
			}
			System.out.println(respuesta);
			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
		return respuesta;

	}

	public static String POST(String url, Object o, final HttpSession session) {
		Gson g = new Gson();
		String respuesta = "";
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpost = new HttpPost(url);

			if (o != null){
				StringEntity params =new StringEntity(g.toJson(o));
				httpost.setEntity(params);
			}
			
			Header ts= new Header() {				
				@Override
				public String getValue() {
					return (String) session.getAttribute("TokenWS");
				}
				
				@Override
				public String getName() {
					return "autenticar";
				}
				
				@Override
				public HeaderElement[] getElements() throws ParseException {
					return null;
				}
			};
			if(ts!=null){
				//System.out.println(session.getAttribute("TokenWS"));
				httpost.addHeader(ts);
			}
			//System.out.println(ts.getName()+":"+ts.getValue());
			HttpResponse response = httpClient.execute(httpost);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}
			
			Header token=response.getFirstHeader("autenticar");
			if(token!=null){
				System.out.println(token.getName()+":"+token.getValue());
				session.setAttribute("TokenWS", token.getValue());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				respuesta = respuesta + output;
			}

			httpClient.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();
		}
		session.setAttribute("usuario","aaaaa");
		return respuesta;
	}

}