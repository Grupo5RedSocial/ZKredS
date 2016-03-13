package org.zkoss.demo.zkSocial.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.PostBean;
import org.ws.util.entidad.Usuario;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import bsh.org.objectweb.asm.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FakeData {
	// Used to randomize data
	private static Random RANDOM = new Random();
	
	private static String[] LAST_NAMES = {
		"Chen", "Chang", "Claire", "Lee", "Lin", "Pan", "Wang", "Shiao"
	};
	
	private static String[] FIRST_NAMES = {
		"Ian", "Jumper", "Monty", "Nancy", "Neil", "Tom", "Tim", "Wing"
	};
	
	private static AuthorBean currentUser;

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser(AuthorBean currentUser) {
		this.currentUser = currentUser;
	}
	
	
	private static String[] NEWSFEEDS = { 
		"<p>ZK 6.5 introduces new features that enable developers to design user interfaces that take advantage of " +
		"tablet-specific user interaction methods such as swiping and changing device orientations. You can also view" +
		" the <a href='http://blog.zkoss.org/index.php/2012/08/14/zk-6-5-tablet-ui-design/'>blog here</a>.</p>" +
		"<p><img src='http://books.zkoss.org/images/6/6e/ResponsiveApproaches.png'/></p>",
		
		"<p>The timeline below shows ZK and our history. For more information please take a look at our " +
		"<a href='http://www.zkoss.org/support/about'>about page</a>.</p>" +
		"<p><img src='https://lh4.googleusercontent.com/-g636wpLGAU0/UEchnFWxC4I/AAAAAAAAAF4/eRYOVmQ6vRQ/s740/zk_timeline.png'/></p>",
		
		"<p>We have just released a new tutorial named \"Practices Of Using CDI In ZK\". This tutorial introduces " +
		"developers to some programming practices of each layer in CDI & possible solutions for common scenarios. For " +
		"the article, please <a href='http://books.zkoss.org/wiki/Small_Talks/2012/September/Practices_Of_Using_CDI_In_ZK'>" +
		"click here</a>.</p>" +
		"<p><img src='http://books.zkoss.org/images/a/a1/Zk_cdi_integration_demo.png'/></p>",
		
		"<p><a href='https://plus.google.com/106973993942617532337'>Chanwit Kaewkasi</a> released version 2 of the Grails" +
		" plugin for the ZK Framework. New features include the following:</p><ul><li>Model-View-ViewModel pattern</li>" +
		"<li>jQuery-style API</li><li>URL Mapping</li><li>Built-in JavaScript declarative style</li><li>ZK 6.0.2</li></ul>" +
		"<p>For more information please <a href='http://grails.org/plugin/zk'>click here</a>.</p>",
		
		"<p>ZK has released an Online Themer to help you create different themes & colours for your ZK application in " +
		"just a few clicks! You can try now by <a href='http://blog.zkoss.org/index.php/2012/08/23/zk-6-online-theme-generator/'>" +
		"clicking here</a>.</p>" +
		"<p><img src='http://blog.zkoss.org/wp-content/uploads/2012/08/zkthemer00-e1344590891262.png'/></p>"
	};

	private static String[][] COMMENTS = {
		{
			"<p>This looks great!</p>",
			"<p>I am really looking forward to trying it out!</p>"
		},
		
		{
			"<p>Thanks for the information</p>",
			"<p>I like the timeline, really shows how long ZK has been going!</p>",
			"<p>6 years, htat's great!</p>"
		},
		
		{
			"<p>CDI with ZK, I am going to use this ASAP!</p>",
			"<p>If this is as good as the Spring integration then it will be brilliant!</p>"
		},
		
		{
			"<p>I love ZK Grails</p>",
			"<p>Thanks Chanwit, this is great!</p>"
		},
		
		{
			"<p>I love the online themer</p>",
			"<p>This truly is excellent</p>",
			"<p>It is so easy to create a new theme now! That's awesome!</p>"
		}
	};
	public static PostBean lastPublicacion;
	
	public static AuthorBean randomAuthor() {
		int lastIndex  = RANDOM.nextInt(LAST_NAMES.length);
		int firstIndex = RANDOM.nextInt(FIRST_NAMES.length);
		
		String name = FIRST_NAMES[firstIndex] + " " + LAST_NAMES[lastIndex];

		return new AuthorBean(name, "images/avatars/userpic.png",null);
	}
	
	private static List<PostBean> posts = new ArrayList<PostBean>();
	
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	private static Session sess = Sessions.getCurrent();
	private static Usuario userLogin;

	public static List<PostBean> getPosts(HttpSession session) {
		userLogin =  (Usuario) sess.getAttribute("userCredential");
		try	{
			currentUser = userLogin.getUsuarioLogin();
			}catch(Exception e ){ 
				Executions.sendRedirect("/login_effect.zul");
		    }
		String json = null;
		if (currentUser == null)
			 json = ApacheHttpClientGet.GET("http://localhost:8080/Prueba/WsRed/getPublicaciones?id="+0, session);
		
		if (json !=  null){
			Executions.sendRedirect("/login_effect.zul");}
		
			json = ApacheHttpClientGet.GET("http://localhost:8080/Prueba/WsRed/getPublicaciones?id="+currentUser.getId_persona(), session);
			java.lang.reflect.Type type = new TypeToken<List<PostBean>>(){}.getType();
			posts = new Gson().fromJson(json, type);
			if(!posts.isEmpty())
				 lastPublicacion=posts.get(0);
			return posts;
	
	}
	
	
}
