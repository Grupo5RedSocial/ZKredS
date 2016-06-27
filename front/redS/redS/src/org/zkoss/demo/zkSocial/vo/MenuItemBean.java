package org.zkoss.demo.zkSocial.vo;

public class MenuItemBean {
	private String icon;
	private String title;
	
	public MenuItemBean() {
		
	}
	
	public MenuItemBean(String icon, String title) {
		this.icon = icon;
		this.title = title;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

}
