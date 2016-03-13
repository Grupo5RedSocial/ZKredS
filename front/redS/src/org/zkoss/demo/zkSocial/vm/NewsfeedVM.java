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
							"Publicación",
							"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAANFJREFUeNpiZEhrYSADTAPis0A8lwkqADLlPx78CkmzHBD7AfFMIDZjgQoWEbDxHZTuBWIDIHYD4nwgvgAzgBNKswPxLxyG9CJZBKJTQAwmNEW4NHchaX4ExPUwCSYiAgwUPqVQ9msgdgbijUC8DiTAQkAzyGsVUPZ3IPYB4jtAbAzEKsQYIA31FgjHAfEpdAX4DGCGOt0WiG8C8RdsivAZ8BeI0wkFEBMDhYDqBggRoUcUWxj8hQbaWxIs/4Xsgj5coYwngEF6GBjJzM5wABBgALS1KzDd2YvyAAAAAElFTkSuQmCC" },
					{
							"Amigos",
							"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA0dJREFUeNrUVl1MUmEY9vBzDiAqEoE6ieVg+bMVZWSsQSZbOTfn5uaFrsyLrprdVF6RmuOi1k3ZupHm6obUvLAtYl6J4lUL5+bGHJsylZYDqdEskP/e1x3YEaHM1oXf9nDgfb/ve96f5/sORCqVKjjSg3A4HPuMer2eAw8hDT4gCfgO2AZEYU2KMfe3BJwcm5OlpaXy3t7eW5WVlZckEkmVUCgsttlsL30+38fx8XEHzAkASfwgGXBy9KCkv7//YX19/XWmsb29/S4+6+rqnoD/EawLHpagSC6XX87XfMjuHJbtXwhIDocjyEfA5XKxL2RjYyP2phggAMToHoUAibwETU1NbIyOxWJx8xEkEgmcU9Lc3Fzd0tJyB3pUHYlEtufn51/Pzs6+93g8n1EIGYJkMslcz4fmXhMIBEVZ9swoLy9X1dbWqru7ux9IpVJV2t7V1fWspqbmTF9f3334+S1fiTiwgRLlmy8DUJRIrVaf5fP5suw5JEkq4MFj2lgYaRqY2vr6+qednZ0I086E3+/3zs3NudlsNpnti0ajWD4SA8wQYBRpwIiYzeZ3Vqv1MdOeRjgcDhmNxns9PT0aiJaX7VepVBook3QPATMCOEyogK8jIyPmqampp7FYLMmILj45OWlaW1tzUhQVzZUdqI+SyWRk3hIhIHo8oVtA8hY2zWwUCAT8FovFBr7NgYGBYa/X62KuA3WlxsbGjKCkZZiT2tfk1tZWgr46UNcirVarBs1TaT9BEOg7Rmt/a2ho6Pbg4OCrioqKKvQ7nU4rBDABX4N7CJC9ra2Ngu/HQR2nGhoa9AqFQqdUKrWweUZNYrFYOjo6alldXbUvLy/PQAndLpdrpqysbJdgCQZ9yFj05bi7EKOmQHpKiOY5bHoFIiUOcgWEQqHt6enpN/F4nOro6LgJJdxcWFiYsNvtH9xu9wpmCQijrCQmk+kFKOAqs/t/Glg+UMz5xcXFjyKRSAzn5wRmbTAYbuh0OgMobgUE8QXTKYbOX8gly4NAo9Fc3NjY8DCkTkBfTnd2dg5j8EgQ4/F41GEJoDeFwWBwKYf95K424KMQIKPfXqy/fCNiU3/Sl5uQcU1gKj8AvoIjP4j//bfllwADAA3FOQiAHgGqAAAAAElFTkSuQmCC",},
					{
							"Agregar Amigos",
							"data:image/png;base64,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"},
					{
							"Logout",
							"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABcAAAATCAYAAAB7u5a2AAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAADsIAAA7CARUoSoAAAAEkSURBVDhPrZSxjkZAFEavLXXovIFHQE/590qVBxCJVkSh4EWUap1KqVGqNBKliMLumtzNRmYMdv+TTO65gy+TmQnh8xs4IUkSNIBlWdAAtm0jNY5jUs/4wMrF8zyIoogM27Zx9ppb4bIsowFomkaqoiik8rgV/lceh+97X1UVNE2DM+dww8uyJIPFuq5o5zDDXdcFwzCwYzMMw+U7zPCu69CuyfMcjYa656/XC8ZxxO4eRVGAqqrY/UKt/Gnwju/7aEcO4fM8oz2j73u0I4dwURTR3gPzQJ+i6zraEepA67qGIAiwu8f+DQtq5aZpot0jTVM0Gua27Cv5+VkJgnA6wjDkLob7P9+ZpgmyLIO2bUGSJLAsCxzHwad8LsP/w1tuCxuAL7kZYZC329YrAAAAAElFTkSuQmCC"} });
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
			Executions.sendRedirect("/login_effect.zul");
		try	{
		currentUser = userLogin.getUsuarioLogin();
		}catch(Exception e ){ 
			Executions.sendRedirect("/login_effect.zul");
	    }
		
		// -------------------------------------------------------------------------------
		// For generating categorized Menuitems in the Sidebar

		menuGroups = new MenuGroupBean[] {
				new MenuGroupBean(
						"FAVORITES",
						new ListModelArray<MenuItemBean>(
								new MenuItemBean[] {
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAutJREFUeNq0VU1IG0EUdmOi6yYmIT9EKaU1JFg0hxIJVEQNJPbixdKUFjw0OQg59OClQqmHgBJKPbSn2KSC0LMX8ZCLgSLUetCDYq6VnkoJarL9cdfort+TbFmSVDYGH3wsM7PzvXnvfTOvpeWGjdnY2GiKYGRk5Mp1fZ0NrfiYAQfA0SEa9CkDInAIHOtlWa7+oXN6ejo2Pj4+19bWxl0nqvPz8/Lm5ubi7OzsHFOJwgTQyf8AXSsrK58dDsedZlJXLpeFsbGx+0RuTSaTL51O5710Ov16e3tbZhiGpciOjo6Os9nsR0EQvmNOrpzOGgwGJ71ebz/mxbW1teVSqbSv0+kkkNpAGnW73R69Xs/id5Yc2Px+/wtKBzZ+gYN1Iifs7e3llpaWMvjnh+pwphOYx+N5x/P871QqlcPcN0AC7N3d3YGenh6PIiJy0A6yViI8PT01UqoUBzAeKOVyub8KeygUkgwGwy9aRxrtq6ury/DHX7IxTCvmXOq6XqpIIVQWqsZyHSH8mzMajSaCaqFWpmpy9fjs7IzyaAqHwyeqPRxyz9F6sVgszcAODg7yFXlaE4nEzNDQ0KimCHp7e0cnJiaecRzHKxuQRnZgYOBR5V8d6nd3cHCQeGSkysSybEdNiiRJuoTaAY1dLteteDz+pp4Mab0TNjU19aremtYayIVC4SdJUNMVlmXGarU6IALDlREo46+w+fn5t5gqaLxfllgs9jwSiTzVFEFfX58PNZyE/Ioaby/r8/mC/5VptYqQYvPw8PCTRp8JzfcAN5Xf3d1dRwSHWoghaw4RPMRlc15VA0YZ5/P5/YWFhU+N1CAajbZD2pGafkDkoihSH7ArzwX0/iCTyXxoREUWi8VRnSJZSUkgEHhss9n6zWazncYg1iHcriZqIJEDEdUX8by24yW8Taj3pjRqyAb1FkFHrW1ra+s9Jk7UxW4GeE6EnZ2dReJWOpqlkvuOa/Tgej1ZqPTkYstN24UAAwCE6hRrSCrxTwAAAABJRU5ErkJggg==",
												"Actualizar Datos", 8),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAu5JREFUeNq0VVFoUlEY9k6didvUajUXJBujgh70NQhb9OSL4mYNWQ972cMGwh6rhw0yo4eBD/ZgCYPFGgzU2DTGHqLYQzFh1ghiMgeDpjTnnBs6U6f2/eMaardgagc+7j33nvP95//O95/D4/3nxiwvL9dFoNFo/h2AhQQ4B7QCglPw54EkEANSQKF6AJFJDAbDnYGBgWednZ3XTptBNBrd8nq9lunpaTe6B1wZKOfm5pY6Ojqu1ipTMpmMm0ymm4eHh+voFsv/NZEs7e3tPcVikVcrJBLJWbVafR1cl4Eu4DzQXJKIzzAMnwbW046OjhRut/t9Op3OQbbVycnJp5FIZIMyYOpZfQm5XK5VLpd3YR+vIBvT+Pi4nYxz4ph6V89yNJXzKJXKG3jIGhagmkcoFIrocRKgUCg0hJyDh2lYBiKRqJmLhwIUGhFALBYL/xYgjQLZb2lpkddKDmnye2hcUpNN436//0UerVaLrqysvIb/96q/k/qUwQGKwra2tvZRpVJpstnspd7eXiNcICxfSTAY/Lq5uRkiO5atPIN5qwiwPjIycrc8A8oKj2NmYWGBp9PpGLa0zwDdDofDhYLpLg0OBAJvnU6ne2Ji4iGfz29FEOa3TRimiA0WtbW1VUi8vb39eXR0VCegVObn5ymfDEGv1/9AqX9TKBTdtArcFy9tNpvDbrc/IKeQlFKp9EJ1hsSDeTtYwDH2NDQ1NfWI5BdwbEwqHo9/ymQyt5Hdk5mZmVcko9lsHmPvC7FWq701ODhoxSEnLU1KJBLx4eHh+3jdYu+GBPBTwGGtdCgUehOLxfwg/4L+vsfjybOTon19fU2Li4vhVCq1MzQ09Fwmk12kSbu7u3RUB4HvFS7i2Pljn8+3AfIPeN/DCVnhLvQp5QSkW7JYLPfC4fA6fUeAAB2qf5QyJtTk/f7+ftpoMeqnx2q1PoaTnLOzs+9IlooALperrgo2Go10qImBLMlbfaMJGnCIZlhwtl8CDAA8PQ/NwxQAIgAAAABJRU5ErkJggg==",
												"Messages", 0),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJJREFUeNqcVl9IU2EU35xtc7P9Y6z1JBUISSvpLUQkAiEECXG0t6CB+mRpQY+NNinSnA/2MJtC6ktCU0ZRCVo4EElQwbUHna621FrTtTZtztz6fXIv3G73zusO/GDfvec7v9853/nOnTiXy4mIicViEY9JACWgAkqBY0AWSAO/KGSAHNfmYhG/ianAJ1tbW82VlZXX9Hr9KY1Go89kMrubm5tr4XD44+DgYN/i4uIn+G0Bf/4LwpMBWWhqa2svW63Wx0aj8QyfCkI2NTXlttvtT7Bco7I5lEBVX19f19LS0qdQKErph1AdXVpamkUWJ8rLyy9KJJIi+t38/Ly3ra3tNn5GmJlwEUgNBoPJ5XK9JeWgH0aj0TUQ3ojH48tYKpqbm60Wi+UuU9XExMRTh8PxAD9/0GdSxJG1Bkra1Wq1npDTmJubG0fwAN6HgSAEvEgmkymmT1VV1U2TyXSWagQRFwFJQ1dRUVHH3EigUqkMeCenuqpYq9WqZDKZnOkjlUpLGhoaLFS3cRJIqqurTyuVSjWbAF10xWw2X4fPeZ1Od8lmszlwBsVsPzTEBVJCvjaVlJWVGelzYRrUyZuamh42NjbegYBSWj3b5HK5lhmXTbC/vr4e5dpIG0pzcPB8Pul0OsHsInaJ9icnJ5djsdhGNpsVFYJgMOhDnBQfAZEVw818xa6tEOzs7KRGRkZeIsY2HwGx5PDw8ABaMHlU9bhsnkgkspqvRMT24BScnp4eOIp63PINp9NJxkWcOfi4CMjLnz09PS7c3i9CgkN9bmxszJ5KpT4TgcxgRTzNQlL86vV6O/ZhhxH4/f53Ho/nNfNwDyMgtg1VbxYWFrz56p5IJLb6+/s74P+d+k4IJiDO0a6uLhtKFeJSTrIbHR29v7Ky4mePaSEEB+MedV0dGhq6hwuUZhPMzMw8R2k81FctVwjBQal8Pt8HjGInM3goFJrt7Ox8RI3mLN9mIQREWdztdj8LBALjJDjG9rfu7u5b1MdlL/9uSpEAk2HInevt7X1fU1NzFevj1HjPa2IB/yr+GZZACbAL/Oare6EZFGR/BRgALZ+XGZ2aoKsAAAAASUVORK5CYII=",
												"Nearby", 5),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAaFJREFUeNpiPHToEAMlwNbWFq88CxrfCYi3AzEbsRYcPnwYmdsBxGuA+A7Q4o8gAab///8zIGF1IGZDEyMFhwDxOiC+AQwZsCOZ0Bx0EIizgPgymSF2Coi3ArEEEHOCgwhkMxSsAGIVIP4CxBFAfJWBCgDZgtdQC0C0EAOVALIFuUBsCcQJQLwOKnYNiI8QaVYaIQtAwABNISxOcAInJycwvW/fPqwWoKeivzhSRzUQn8GG9+7dqwUyCJs+bD64AMTLgNgbiPmRxFuhGAO4uLgwwCzAGkT//v1DDkNjIL4JxBOB+CSSugRo/GCAXbt2dbi5ud1HMgdnHGRC4+AdEK9EU7cbT954itcHSBKG0OQJsqQLKiYK9RU+oLtjxw6ikikIeADxUiR+CBSTnw9whR0tMhpNAHI+UATim0CsDcQFFJSoOPPBMWgpCAIatAgiTyA2A1UWQOxKCwtAuVgLmookaBXJnFDDt8IyEIlgMRBH4SvsVgLxU2iEkxOxsUAciK+wA9VmQdDaLY2CkLkJNQujVQGrV5WINSk4OBivPECAAQBTpZHKNpK1HgAAAABJRU5ErkJggg==",
												"Notes", 3),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAa9JREFUeNrUlt9HQ2EYx8/WERExuoqldDtKdLXjpESUZmd3I0oZpf6AbqLrbkbpIjGii8QuEidz5BzrakRX00VERERXEdHW9+HZHK/HOus06eHjvD+25/s853nfZ4vU63WtkxbVOmx6uVwO5cAwjNYC/JwFJyDWroAS4D1IQbTaFOAakHMPXIZ8IyvgAEyqAhR5BRzy+g6YA6dgt5VH0zSbY8/zaJLAcwbPJ+xVdeEU0eY2j8c5s0qQ8H2+RkDBdd1BSeDzm3nDhsAbeBEEyAZAr16r1dQvXoF9kOSa3AjOu0ERPACrsSj40qKkKrAJxsCWby3mG++BUZB2HGfJn4FKlFQDEAd3IAsWQc63ly+VSvFGBip6gFbRBy5APzgGH8J+AUxLvqQaaOyMitfFThO8TvMe4fNTtm2viTdZEEhyxMt0zMFCwEuWEAWUtCjyM067+CvNTsmAetL5D31d8+0XM6DmlAa3IYKl1zkBHiWBFMhzawhj1E3XQVYVoI15Og3cR8JYDmyAVzrSapGfwRH3mTBGPWrVsqx3VYBqMdyOp0wm87e/yZF//6/iS4ABAJTXDst1neyvAAAAAElFTkSuQmCC",
												"Gallery", 0),
										new MenuItemBean(
												"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA0dJREFUeNrUVl1MUmEY9vBzDiAqEoE6ieVg+bMVZWSsQSZbOTfn5uaFrsyLrprdVF6RmuOi1k3ZupHm6obUvLAtYl6J4lUL5+bGHJsylZYDqdEskP/e1x3YEaHM1oXf9nDgfb/ve96f5/sORCqVKjjSg3A4HPuMer2eAw8hDT4gCfgO2AZEYU2KMfe3BJwcm5OlpaXy3t7eW5WVlZckEkmVUCgsttlsL30+38fx8XEHzAkASfwgGXBy9KCkv7//YX19/XWmsb29/S4+6+rqnoD/EawLHpagSC6XX87XfMjuHJbtXwhIDocjyEfA5XKxL2RjYyP2phggAMToHoUAibwETU1NbIyOxWJx8xEkEgmcU9Lc3Fzd0tJyB3pUHYlEtufn51/Pzs6+93g8n1EIGYJkMslcz4fmXhMIBEVZ9swoLy9X1dbWqru7ux9IpVJV2t7V1fWspqbmTF9f3334+S1fiTiwgRLlmy8DUJRIrVaf5fP5suw5JEkq4MFj2lgYaRqY2vr6+qednZ0I086E3+/3zs3NudlsNpnti0ajWD4SA8wQYBRpwIiYzeZ3Vqv1MdOeRjgcDhmNxns9PT0aiJaX7VepVBook3QPATMCOEyogK8jIyPmqampp7FYLMmILj45OWlaW1tzUhQVzZUdqI+SyWRk3hIhIHo8oVtA8hY2zWwUCAT8FovFBr7NgYGBYa/X62KuA3WlxsbGjKCkZZiT2tfk1tZWgr46UNcirVarBs1TaT9BEOg7Rmt/a2ho6Pbg4OCrioqKKvQ7nU4rBDABX4N7CJC9ra2Ngu/HQR2nGhoa9AqFQqdUKrWweUZNYrFYOjo6alldXbUvLy/PQAndLpdrpqysbJdgCQZ9yFj05bi7EKOmQHpKiOY5bHoFIiUOcgWEQqHt6enpN/F4nOro6LgJJdxcWFiYsNvtH9xu9wpmCQijrCQmk+kFKOAqs/t/Glg+UMz5xcXFjyKRSAzn5wRmbTAYbuh0OgMobgUE8QXTKYbOX8gly4NAo9Fc3NjY8DCkTkBfTnd2dg5j8EgQ4/F41GEJoDeFwWBwKYf95K424KMQIKPfXqy/fCNiU3/Sl5uQcU1gKj8AvoIjP4j//bfllwADAA3FOQiAHgGqAAAAAElFTkSuQmCC",
												"Amigos", 15) })) };
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

		
		
		if (msg == "Publicación") {
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
		
		if (msg == "Amigos") {
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
		
		if (msg == "Agregar Amigos") {
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
		
		if (msg == "Logout") {
			Session sess = Sessions.getCurrent();
			sess.removeAttribute("userCredential");
			Executions.sendRedirect("/login_effect.zul");
		} else {
			Clients.showNotification(msg, Clients.NOTIFICATION_TYPE_INFO, comp,	pos, 2000);
		}

	}
	
	
	private MenuItemBean Noticias;
	@NotifyChange("Noticias")
	public void lanzarTimer(){
		TimerTask timerTask = new TimerTask() 
	    { 
	        public void run()  
	        { 
	        	String messages = ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/getNewsFeed", FakeData.lastPublicacion, request.getSession());
	        	Noticias.setCount(11);
	        } 
	    }; 
	    Timer timer = new Timer();  
	    timer.scheduleAtFixedRate(timerTask, 0, 20000);
	}
}
