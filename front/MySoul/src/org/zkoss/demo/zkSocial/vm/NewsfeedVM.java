package org.zkoss.demo.zkSocial.vm;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;

import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.PostBean;
import org.ws.util.entidad.Usuario;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.demo.zkSocial.composite.Contact;
import org.zkoss.demo.zkSocial.composite.MenuItem;
import org.zkoss.demo.zkSocial.data.FakeData;
import org.zkoss.demo.zkSocial.vo.ContactBean;
import org.zkoss.demo.zkSocial.vo.ContactGroupBean;
import org.zkoss.demo.zkSocial.vo.MenuGroupBean;
import org.zkoss.demo.zkSocial.vo.MenuItemBean;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

import com.google.gson.Gson;

public class NewsfeedVM {

	@Wire
	private Popup feedbackPopupPub;

	@Wire
	private Popup feedbackPopup;

	private boolean mobile;

	private Timer timer;
	
	//private MenuItem Menulista;
	private MenuItemBean Menulista;

	// If mobile, assume started out as "landscape"
	// Changed when orientation change is detected.
	private String orient = "landscape";

	// -------------------------------------------------------

	private String css;

	/**
	 * Tablet and desktop may use different set of CSS
	 */
	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	// -------------------------------------------------------

	private AuthorBean currentUser;

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}

	// -------------------------------------------------------

	private MenuGroupBean[] menuGroups;
	private Vlayout listaMenu;

	/**
	 * Menu items are grouped into categories.
	 */
	public MenuGroupBean[] getMenuGroups() {
		return menuGroups;
	}

	public void setMenuGroups(MenuGroupBean[] menuGroups) {
		this.menuGroups = menuGroups;
	}

	// -------------------------------------------------------

	public ListModel<String[]> getToolbarModel() {
		return new ListModelList<String[]>(
				new String[][] {
						{
							"Publicar",
							//"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAANFJREFUeNpiZEhrYSADTAPis0A8lwkqADLlPx78CkmzHBD7AfFMIDZjgQoWEbDxHZTuBWIDIHYD4nwgvgAzgBNKswPxLxyG9CJZBKJTQAwmNEW4NHchaX4ExPUwCSYiAgwUPqVQ9msgdgbijUC8DiTAQkAzyGsVUPZ3IPYB4jtAbAzEKsQYIA31FgjHAfEpdAX4DGCGOt0WiG8C8RdsivAZ8BeI0wkFEBMDhYDqBggRoUcUWxj8hQbaWxIs/4Xsgj5coYwngEF6GBjJzM5wABBgALS1KzDd2YvyAAAAAElFTkSuQmCC" },
					     	"data:image/png;base64,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" },
							{
							"Mis Contactos",
							//"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA0dJREFUeNrUVl1MUmEY9vBzDiAqEoE6ieVg+bMVZWSsQSZbOTfn5uaFrsyLrprdVF6RmuOi1k3ZupHm6obUvLAtYl6J4lUL5+bGHJsylZYDqdEskP/e1x3YEaHM1oXf9nDgfb/ve96f5/sORCqVKjjSg3A4HPuMer2eAw8hDT4gCfgO2AZEYU2KMfe3BJwcm5OlpaXy3t7eW5WVlZckEkmVUCgsttlsL30+38fx8XEHzAkASfwgGXBy9KCkv7//YX19/XWmsb29/S4+6+rqnoD/EawLHpagSC6XX87XfMjuHJbtXwhIDocjyEfA5XKxL2RjYyP2phggAMToHoUAibwETU1NbIyOxWJx8xEkEgmcU9Lc3Fzd0tJyB3pUHYlEtufn51/Pzs6+93g8n1EIGYJkMslcz4fmXhMIBEVZ9swoLy9X1dbWqru7ux9IpVJV2t7V1fWspqbmTF9f3334+S1fiTiwgRLlmy8DUJRIrVaf5fP5suw5JEkq4MFj2lgYaRqY2vr6+qednZ0I086E3+/3zs3NudlsNpnti0ajWD4SA8wQYBRpwIiYzeZ3Vqv1MdOeRjgcDhmNxns9PT0aiJaX7VepVBook3QPATMCOEyogK8jIyPmqampp7FYLMmILj45OWlaW1tzUhQVzZUdqI+SyWRk3hIhIHo8oVtA8hY2zWwUCAT8FovFBr7NgYGBYa/X62KuA3WlxsbGjKCkZZiT2tfk1tZWgr46UNcirVarBs1TaT9BEOg7Rmt/a2ho6Pbg4OCrioqKKvQ7nU4rBDABX4N7CJC9ra2Ngu/HQR2nGhoa9AqFQqdUKrWweUZNYrFYOjo6alldXbUvLy/PQAndLpdrpqysbJdgCQZ9yFj05bi7EKOmQHpKiOY5bHoFIiUOcgWEQqHt6enpN/F4nOro6LgJJdxcWFiYsNvtH9xu9wpmCQijrCQmk+kFKOAqs/t/Glg+UMz5xcXFjyKRSAzn5wRmbTAYbuh0OgMobgUE8QXTKYbOX8gly4NAo9Fc3NjY8DCkTkBfTnd2dg5j8EgQ4/F41GEJoDeFwWBwKYf95K424KMQIKPfXqy/fCNiU3/Sl5uQcU1gKj8AvoIjP4j//bfllwADAA3FOQiAHgGqAAAAAElFTkSuQmCC",},
							"data:image/png;base64,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",},
					{
							"Agregar Contactos",
							//"data:image/png;base64,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"},
					"data:image/png;base64,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"},
							{
							"Salir",
							// "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABcAAAATCAYAAAB7u5a2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAEkSURBVDhPrZSxjkZAFEavLXXovIFHQE/590qVBxCJVkSh4EWUap1KqVGqNBKliMLumtzNRmYMdv+TTO65gy+TmQnh8xs4IUkSNIBlWdAAtm0jNY5jUs/4wMrF8zyIoogM27Zx9ppb4bIsowFomkaqoiik8rgV/lceh+97X1UVNE2DM+dww8uyJIPFuq5o5zDDXdcFwzCwYzMMw+U7zPCu69CuyfMcjYa656/XC8ZxxO4eRVGAqqrY/UKt/Gnwju/7aEcO4fM8oz2j73u0I4dwURTR3gPzQJ+i6zraEepA67qGIAiwu8f+DQtq5aZpot0jTVM0Gua27Cv5+VkJgnA6wjDkLob7P9+ZpgmyLIO2bUGSJLAsCxzHwad8LsP/w1tuCxuAL7kZYZC329YrAAAAAElFTkSuQmCC"} });
	"data:image/png;base64,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"} });

	}

	// -------------------------------------------------------

	private ContactGroupBean[] contactGroups;

	public ContactGroupBean[] getContactGroups() {
		return contactGroups;
	}

	// -------------------------------------------------------

	private PostBean currentPost = null;

	/**
	 * Current post that the user is interested in getting feedback
	 */
	public PostBean getCurrentPost() {
		return currentPost;
	}

	public void setCurrentPost(PostBean currentPost) {
		this.currentPost = currentPost;
	}

	// -------------------------------------------------------

	private String currentComment = null;

	/**
	 * Comment for current post
	 */
	public String getCurrentComment() {
		return currentComment;
	}

	public void setCurrentComment(String currentComment) {
		this.currentComment = currentComment;
	}

	// -------------------------------------------------------

	private boolean modalShow = false;

	/**
	 * If true, the modal filter is shown.
	 */
	public boolean isModalShow() {
		return modalShow;
	}

	public void setModalShow(boolean modalShow) {
		this.modalShow = modalShow;
	}

	// -------------------------------------------------------

	private boolean menuOpen = false;

	/**
	 * If true, the menu panel is opened.
	 */
	public boolean isMenuOpen() {
		return menuOpen;
	}

	public void setMenuOpen(boolean menuOpen) {
		this.menuOpen = menuOpen;
	}

	// -------------------------------------------------------

	private boolean contactOpen = true;
	private boolean hideContact = false;

	/**
	 * If true, the contact panel is opened
	 */
	public boolean isContactOpen() {
		return contactOpen;
	}

	public void setContactOpen(boolean contactOpen) {
		this.contactOpen = contactOpen;
	}

	// -------------------------------------------------------

	private boolean likeStatus = false;

	/**
	 * If true, the current post has been liked.
	 */
	public boolean isLikeStatus() {
		return (currentPost == null) ? false : currentPost.getLikeList()
				.contains(currentUser);
	}

	public void setLikeStatus(boolean likeStatus) {
		this.likeStatus = likeStatus;
	}

	// -------------------------------------------------------

	/**
	 * Generate fake newsfeed posts
	 */
	public List<PostBean> getPostModel() {
		return FakeData.getPosts(request.getSession());
	}

	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();

	private Session sess = Sessions.getCurrent();
	private Usuario userLogin;

	@Init
	public void init() {
		// Detect if client is mobile (such as Android or iOS devices)
		userLogin = (Usuario) sess.getAttribute("userCredential");
		mobile = Servlets.getBrowser(request, "mobile") != null;
		Principal user2 =request.getUserPrincipal();
			if (mobile) {
			css = "css/tablet.css.dsp";
		} else {
			css = "css/desktop.css.dsp";
		}

		// -------------------------------------------------------------------------------
		//new AuthorBean("ZK Team", "images/avatars/zk.jpg");
		if (userLogin == null)	
			Executions.sendRedirect("/login.zul");
		try	{
		currentUser = userLogin.getUsuarioLogin();
		}catch(Exception e ){ 
			Executions.sendRedirect("/login.zul");
	    }
		
		// -------------------------------------------------------------------------------
		// For generating categorized Menuitems in the Sidebar

		menuGroups = new MenuGroupBean[] {
				new MenuGroupBean(
						"FAVORITOS",
						new ListModelArray<MenuItemBean>(
								new MenuItemBean[] {
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAutJREFUeNq0VU1IG0EUdmOi6yYmIT9EKaU1JFg0hxIJVEQNJPbixdKUFjw0OQg59OClQqmHgBJKPbSn2KSC0LMX8ZCLgSLUetCDYq6VnkoJarL9cdfort+TbFmSVDYGH3wsM7PzvXnvfTOvpeWGjdnY2GiKYGRk5Mp1fZ0NrfiYAQfA0SEa9CkDInAIHOtlWa7+oXN6ejo2Pj4+19bWxl0nqvPz8/Lm5ubi7OzsHFOJwgTQyf8AXSsrK58dDsedZlJXLpeFsbGx+0RuTSaTL51O5710Ov16e3tbZhiGpciOjo6Os9nsR0EQvmNOrpzOGgwGJ71ebz/mxbW1teVSqbSv0+kkkNpAGnW73R69Xs/id5Yc2Px+/wtKBzZ+gYN1Iifs7e3llpaWMvjnh+pwphOYx+N5x/P871QqlcPcN0AC7N3d3YGenh6PIiJy0A6yViI8PT01UqoUBzAeKOVyub8KeygUkgwGwy9aRxrtq6ury/DHX7IxTCvmXOq6XqpIIVQWqsZyHSH8mzMajSaCaqFWpmpy9fjs7IzyaAqHwyeqPRxyz9F6sVgszcAODg7yFXlaE4nEzNDQ0KimCHp7e0cnJiaecRzHKxuQRnZgYOBR5V8d6nd3cHCQeGSkysSybEdNiiRJuoTaAY1dLteteDz+pp4Mab0TNjU19aremtYayIVC4SdJUNMVlmXGarU6IALDlREo46+w+fn5t5gqaLxfllgs9jwSiTzVFEFfX58PNZyE/Ioaby/r8/mC/5VptYqQYvPw8PCTRp8JzfcAN5Xf3d1dRwSHWoghaw4RPMRlc15VA0YZ5/P5/YWFhU+N1CAajbZD2pGafkDkoihSH7ArzwX0/iCTyXxoREUWi8VRnSJZSUkgEHhss9n6zWazncYg1iHcriZqIJEDEdUX8by24yW8Taj3pjRqyAb1FkFHrW1ra+s9Jk7UxW4GeE6EnZ2dReJWOpqlkvuOa/Tgej1ZqPTkYstN24UAAwCE6hRrSCrxTwAAAABJRU5ErkJggg==",
												"Actualizar Datos"),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAB4AAAAYCAIAAABiC9roAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAX8SURBVEhLTZVbbBRVGMfnujuz126hDbVpqiHYJ9MX5BKND0KtgZjwYCzwRAQSxMYgFhLTVFNR3wgUohEBW0pIMARIi9FEbE0UyuUFUkvTAsullZbd3ru7M7N3f2cnNZxMJuec+c7/fN//+3/fyBcvX1q3bp2maUVZliQpm816VS2fz9u2bZrm3bt3Ozs7h4eH0+n0mjVrNm7c+Mabb2FWXBovztljGZ+bMr3G3Nyc3NnV3dDQoChKoVDgA6DFopzJZDy62tKyf+zpY6/Xy8X5fFaW5WTSqn6ppv3QV16v6YJjr6g6Z13cglywHFuVFRAUv9/vIuJvLpfDKFcs6Lre1dU1MDCA44oilR6FzUDANzg4ePZMN8YMNlVVdXGXhgidOZtKMBgEEWiGO+EbZ3p6esrKyvDUo2qarPCWC0VdUSORyNW+fsMwsMmU3AHFRc9LAvH/SzBQHMeBSnZd31kSWSKxUF5erhKboshSUVMVVcxkw2emCVZVLdvGZeFQMZcn7BdwXSgFTwVH8PCCy6lUCn7ZExalNLjREAT+wj5L+LEsi68uy//n00UThOCvu3AJYqRSCRKA76D7DRPmQHTDFGaylLBSWe6SBAOSqizhkv883riZE6zOJxYz+RwWvDlQJGZZtp2U1yucAgWy8vmCx+MVZ3LFRCIBDwiAKLnbjbUklSy42WyaTb4CIu504/V4PMSInIlxenp6w4aGiYkJ7BCJxr7j4BIjmbCoAw4TmSsSUDgYCoXYxB4y3RzIp890A1pdXR0IBFjDI1SE/IFIWWh2dvbdxoaqqioMGLF4PG1nrvz6m254+QQKXmF/qL399u3bXLBy5cr6+vp3Nm22kinsRRIIanJykgUpgnpDN/A9Gn28bNmyj5s/oSKmZ+aij56EI+UfbN0WLo/Mz8+jxXwmq6tqx5EjVGw4GFpRWfFsfKy3t3dqagqBCq9//KnTDaqmpiYcDgsxFhUoC4dCGK1d+3osFhsaGsoV8qtWrap7te5q/5/E4aQsjnQcO9LX11dXV9ey/7P6+te2bNmiez1jE7GDLQdqa2uFsEATtR+Pj4yMzE3PpNN29OHD7du3t7W1btu2bXx8HC8qKyun4jPvN21t/+LL9zZtFvK37Xv37nFfPpvjmtHRUSj1m4FXal8+fPgwdCuISVJk+gd5NgwfJCYTC8c7ji4vjxhePfZ84sQP3/t9hun1nO3uik88Y5M0BAO+63//5dH0xMIiAaWzmWC4bHZ+gQaSTllB0wddGlzjgq56YAe1hfy+0ydPyaRAojILUBSNRtknaaMjw6bhRX+4qSkqLvt8vl27dgVCNIvg9Oxsa1sbhXfr+q3f/7g6MzMjhCk0lSt6Nd0wzU/37atasRxRUyW8MxknFAq0tn4uysdv4oQqq37TdJVbUVEhMk8VlKp3bmaGTahjU+hSNCBN5wK0Cd04gvv0I1WSpXyBSdZJ0yToTbx5IBFj4iAB/46NHzvacf78eceyOX7u3LkLFy4gEpjgK2Iotcei9Dj66NuvvxF9tiiKyC1fgLAjXnfJvpPJBEKhmzdv7t69e2xsjNThsoBIZ5CD+IekHVYUrRCb207v3LlDBNQYWGy6FcxNREcRseQAMfGJSX9/P7gojIplhzrAa7d9ItmP9u4VrfxtfjEIRVF/uXLFsi36p65q1Bg9gE2anehI8EO/lnGikEFqqvbk6VPbSR84eHDgxo1Uypp8Hhv8Z8i2nXhs6sOdO5uamu7fvy8fP3EC79AjARKdUsgbhqe8LELrUHVKWeImQUWpHUuFopOB/DRNhoAaGxv37NkDrdeuXVtcXFy/fv3q1asv9/ZgKfL83cmTMGAl7QcPHsAseQMH2RKgouG1xM/AhRZ9uVC0HdGDFhYWeJN24mtuboYZGIMKQJ49n8QeYuVTXWcSqaSmenbs2MFV+JbLZWgRojuL/5xEVkWbV0TLZs7E7fpg8eZnz8AnooRxCGDCG97lny9eSlopTiByrkomk5ouFCkqdemvylLcUeoHwLlfgWYilKppQu+l4fZkfglk/j8822XLurzmjAAAAABJRU5ErkJggg==",
												"Agregar Contactos"),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA0dJREFUeNrUVl1MUmEY9vBzDiAqEoE6ieVg+bMVZWSsQSZbOTfn5uaFrsyLrprdVF6RmuOi1k3ZupHm6obUvLAtYl6J4lUL5+bGHJsylZYDqdEskP/e1x3YEaHM1oXf9nDgfb/ve96f5/sORCqVKjjSg3A4HPuMer2eAw8hDT4gCfgO2AZEYU2KMfe3BJwcm5OlpaXy3t7eW5WVlZckEkmVUCgsttlsL30+38fx8XEHzAkASfwgGXBy9KCkv7//YX19/XWmsb29/S4+6+rqnoD/EawLHpagSC6XX87XfMjuHJbtXwhIDocjyEfA5XKxL2RjYyP2phggAMToHoUAibwETU1NbIyOxWJx8xEkEgmcU9Lc3Fzd0tJyB3pUHYlEtufn51/Pzs6+93g8n1EIGYJkMslcz4fmXhMIBEVZ9swoLy9X1dbWqru7ux9IpVJV2t7V1fWspqbmTF9f3334+S1fiTiwgRLlmy8DUJRIrVaf5fP5suw5JEkq4MFj2lgYaRqY2vr6+qednZ0I086E3+/3zs3NudlsNpnti0ajWD4SA8wQYBRpwIiYzeZ3Vqv1MdOeRjgcDhmNxns9PT0aiJaX7VepVBook3QPATMCOEyogK8jIyPmqampp7FYLMmILj45OWlaW1tzUhQVzZUdqI+SyWRk3hIhIHo8oVtA8hY2zWwUCAT8FovFBr7NgYGBYa/X62KuA3WlxsbGjKCkZZiT2tfk1tZWgr46UNcirVarBs1TaT9BEOg7Rmt/a2ho6Pbg4OCrioqKKvQ7nU4rBDABX4N7CJC9ra2Ngu/HQR2nGhoa9AqFQqdUKrWweUZNYrFYOjo6alldXbUvLy/PQAndLpdrpqysbJdgCQZ9yFj05bi7EKOmQHpKiOY5bHoFIiUOcgWEQqHt6enpN/F4nOro6LgJJdxcWFiYsNvtH9xu9wpmCQijrCQmk+kFKOAqs/t/Glg+UMz5xcXFjyKRSAzn5wRmbTAYbuh0OgMobgUE8QXTKYbOX8gly4NAo9Fc3NjY8DCkTkBfTnd2dg5j8EgQ4/F41GEJoDeFwWBwKYf95K424KMQIKPfXqy/fCNiU3/Sl5uQcU1gKj8AvoIjP4j//bfllwADAA3FOQiAHgGqAAAAAElFTkSuQmCC",
												"Mis Contactos"),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABcAAAATCAYAAAB7u5a2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAEkSURBVDhPrZSxjkZAFEavLXXovIFHQE/590qVBxCJVkSh4EWUap1KqVGqNBKliMLumtzNRmYMdv+TTO65gy+TmQnh8xs4IUkSNIBlWdAAtm0jNY5jUs/4wMrF8zyIoogM27Zx9ppb4bIsowFomkaqoiik8rgV/lceh+97X1UVNE2DM+dww8uyJIPFuq5o5zDDXdcFwzCwYzMMw+U7zPCu69CuyfMcjYa656/XC8ZxxO4eRVGAqqrY/UKt/Gnwju/7aEcO4fM8oz2j73u0I4dwURTR3gPzQJ+i6zraEepA67qGIAiwu8f+DQtq5aZpot0jTVM0Gua27Cv5+VkJgnA6wjDkLob7P9+ZpgmyLIO2bUGSJLAsCxzHwad8LsP/w1tuCxuAL7kZYZC329YrAAAAAElFTkSuQmCC",
												"Salir")})) };
		// -------------------------------------------------------------------------------

		// Contacts in the Contact Panel
		contactGroups = new ContactGroupBean[] {
				new ContactGroupBean(null, new ListModelArray<ContactBean>(
						new ContactBean[] {
								new ContactBean("images/avatars/afro.png",
										"Alred", "mobile"),
								new ContactBean("images/avatars/alien.png",
										"Bruce", "active"),
								new ContactBean("images/avatars/anciano.png",
										"Selina", ""),
								new ContactBean("images/avatars/artista.png",
										"Robin", "active"),
								new ContactBean(
										"images/avatars/astronauta.png",
										"Harvey", ""),
								new ContactBean("images/avatars/barbaman.png",
										"Tony", "mobile"),
								new ContactBean("images/avatars/bombero.png",
										"Nick", "mobile"),
								new ContactBean("images/avatars/boxeador.png",
										"Peter", "active"),
								new ContactBean("images/avatars/bruce_lee.png",
										"Clark", "mobile"), })),
				new ContactGroupBean("Other Online Contacts",
						new ListModelArray<ContactBean>(new ContactBean[] {
								new ContactBean(
										"images/avatars/caradebolsa.png",
										"Gwen", "active"),
								new ContactBean("images/avatars/chavo.png",
										"Natasha", "active"),
								new ContactBean(
										"images/avatars/cientifica.png",
										"Steve", "active"),
								new ContactBean(
										"images/avatars/cientifico_loco.png",
										"Mary", "active"),
								new ContactBean("images/avatars/comisario.png",
										"Clint", "active"),
								new ContactBean("images/avatars/cupido.png",
										"Phil", "active"),
								new ContactBean("images/avatars/diabla.png",
										"Pepper", "active"),
								new ContactBean("images/avatars/director.png",
										"Curt", "active"),
								new ContactBean("images/avatars/dreds.png",
										"Reed", "active"),
								new ContactBean("images/avatars/elsanto.png",
										"Sue", "active"),
								new ContactBean("images/avatars/elvis.png",
										"Johnny", "active"),
								new ContactBean("images/avatars/emo.png",
										"Ben", "active") })) };
	}

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Double ie = Servlets.getBrowser(request, "ie");
		if (ie != null && ie < 8.0) {
			Clients.showNotification("This demo does not support IE6/7", true);
		}

		Selectors.wireComponents(view, this, false);
	}

	@Command
	@NotifyChange("modalShow")
	public void hideModal() {
		modalShow = false;
	}

	@Command
	public void showModal() {
		// create a window programmatically and use it as a modal dialog.
		Window window = (Window) Executions.createComponents(
				"/publicacion.zul", null, null);
		window.doModal();
	}

	@Command("upload")
	public void upload(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
		UploadEvent upEvent = (UploadEvent) ctx.getTriggerEvent();

		System.out.println("uploading " + upEvent.getMedia().getName());
	}

	@Command
	@NotifyChange({ "currentPost", "modalShow", "likeStatus" })
	public void feedback(@BindingParam("instance") PostBean post,
			@BindingParam("ref") Component likeArea) {
		int mensaje = post.getCommentList().size();
		/*if (post.getCommentList().get(0).getId_pub().equals(null))
			System.out.println("prueba");	*/		
		currentPost = post;
		likeStatus = currentPost.getLikeList().contains(currentUser);
		currentPost.setLiked(likeStatus);
		modalShow = true;

		if (mobile) {
			feedbackPopup.open(feedbackPopup.getFellow("mainWindow"),
					"bottom_right");
		} else {
			Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			Clients.scrollIntoView(likeArea);

			feedbackPopup.open(likeArea, "after_start");
		}
	}

	// agregar comentario e insertarlo en la base desde el ws
	@Command
	@NotifyChange({ "currentPost", "currentComment" })
	public void addComment() {
		PostBean comment = new PostBean();
		comment.setAuthor(currentUser);
		comment.setContent(currentComment);
		comment.setTime(new Date());
		currentPost.getCommentList().add(comment);

		PostBean comentario = new PostBean();
		comentario.setContent(comment.getContent());
		comentario.setAuthor(currentUser);
		comentario.setId_pub(currentPost.getId_pub());
		ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/addComentario", comentario, request.getSession());
		currentComment = "";

	}
	
		
	// agregar y quitar likes en la base llamando al ws
	@Command
	@NotifyChange({ "currentPost", "likeStatus" })
	public void likePost(@BindingParam("instance") PostBean post) {
		PostBean publicacionEnvio;
		if (post != null)
			currentPost = post;
		List<AuthorBean> likeList = currentPost.getLikeList();
		likeStatus = likeList.contains(currentUser);

		if (likeStatus) {
			publicacionEnvio = new PostBean();
			publicacionEnvio.setAuthor(currentUser);
			publicacionEnvio.setId_pub(post.getId_pub());
			ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/eliminarLike",publicacionEnvio, request.getSession());
			likeList.remove(currentUser);
		} else {
			publicacionEnvio = new PostBean();
			publicacionEnvio.setAuthor(currentUser);
			publicacionEnvio.setId_pub(post.getId_pub());
			ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/guardarLike",publicacionEnvio, request.getSession());
			likeList.add(currentUser);
		}

		likeStatus = !likeStatus;

		currentPost.setLiked(likeStatus);
	}

	@Command
	@NotifyChange({ "menuOpen", "contactOpen" })
	public void toggleMenu() {
		menuOpen = !menuOpen;

		if ("landscape".equals(orient))
			contactOpen = !menuOpen;

		if (hideContact)
			contactOpen = false;
	}

	@Command
	@NotifyChange({ "menuOpen", "contactOpen" })
	public void contentSwipe(@BindingParam("dir") String direction) {
		if ("up".equals(direction) || "down".equals(direction))
			return;

		menuOpen = "right".equals(direction);

		if ("landscape".equals(orient))
			contactOpen = !menuOpen;

		if (hideContact)
			contactOpen = false;
	}

	@Command
	@NotifyChange({ "contactOpen", "menuOpen", "modalShow" })
	public void updateDeviceStatus(@BindingParam("orient") String orient,
			@BindingParam("width") int width) {

		Window mainWindow = ((Window) feedbackPopup.getFellow("mainWindow"));

		// Adjust width for desktop
		if (!mobile) {
			if (width > 1366) {
				mainWindow.setWidth("1366px");
				feedbackPopup.setWidth("700px");
			} else {
				mainWindow.setWidth("100%");
				feedbackPopup.setWidth("50%");
			}
		} else {
			feedbackPopup.setWidth("70%");
			feedbackPopup.setHeight("100%");

			// For mobile devices, responsive to orientation change
			if (!this.orient.equals(orient)) {
				this.orient = orient;

				// Android native browser does not resize after rotate
				Clients.resize(mainWindow);

				Clients.showNotification(orient,
						Clients.NOTIFICATION_TYPE_INFO, null, "middle_center",
						2000);

				if ("portrait".equals(orient)) {
					if (contactOpen)
						contactOpen = false;
				} else {
					if (!menuOpen && !contactOpen)
						contactOpen = true;
				}

				feedbackPopup.close();
				modalShow = false;
			}
		}

		if (width <= 800) {
			hideContact = true;
			contactOpen = false;
		} else {
			hideContact = false;
			if (!menuOpen)
				contactOpen = true;

			if (!mobile)
				menuOpen = true;
		}
	}

	@Command
	public void showMessage(
			@ContextParam(ContextType.COMPONENT) Component comp,
			@BindingParam("pos") String pos,
			@BindingParam("ref") Component view) {
		String msg = null;

		if (comp instanceof Listbox) {
			Listitem item = ((Listbox) comp).getSelectedItem();

			if (item instanceof Contact)
				msg = ((Contact) item).getName();
			else if (item instanceof MenuItem)
				msg = ((MenuItem) item).getTitle();

			((Listbox) comp).setSelectedItem(null);
			comp = item;
		} else if (comp instanceof Toolbarbutton) {
			msg = ((Toolbarbutton) comp).getLabel();
		} else
			return;

		
		
		if (msg == "Publicar") {
			/*Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			feedbackPopupPub.open(comp);*/
			Map<String, Object> params = new HashMap<String, Object>();

			// params.put("value", getValue());
			params.put("parent", comp);

			Window popup = (Window) Executions.createComponents("publicacion.zul",view, params);

			popup.doModal();
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}
		
		if (msg == "Mis Contactos") {
			/*Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			feedbackPopupPub.open(comp);*/
			Map<String, Object> params = new HashMap<String, Object>();

			// params.put("value", getValue());
			params.put("parent", comp);

			Window popup = (Window) Executions.createComponents("dynamic_tree.zul",view, params);

			popup.doModal();
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}
		
		if (msg == "Agregar Contactos") {
			/*Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			feedbackPopupPub.open(comp);*/
			Map<String, Object> params = new HashMap<String, Object>();

			// params.put("value", getValue());
			params.put("parent", comp);

			Window popup = (Window) Executions.createComponents("drag.zul",view, params);

			popup.doModal();
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}
		
		if (msg == currentUser.getName()) {
			/*Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			feedbackPopupPub.open(comp);*/
			Map<String, Object> params = new HashMap<String, Object>();

			// params.put("value", getValue());
			params.put("parent", comp);

			Window popup = (Window) Executions.createComponents("editImagen.zul",view, params);

			popup.doModal();
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}
		
		if (msg == "Actualizar Datos") {
			/*Clients.evalJavaScript("jq('.newsfeedPanel .z-center-body').eq(0).scrollTop(700);");
			feedbackPopupPub.open(comp);*/
			Map<String, Object> params = new HashMap<String, Object>();

			// params.put("value", getValue());
			params.put("parent", comp);

			Window popup = (Window) Executions.createComponents("editDatos.zul",view, params);

			popup.doModal();
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}
		
		if (msg == "Salir") {
			Session sess = Sessions.getCurrent();
			sess.removeAttribute("userCredential");
			Executions.sendRedirect("/login.zul");
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}

	}
	
	}
