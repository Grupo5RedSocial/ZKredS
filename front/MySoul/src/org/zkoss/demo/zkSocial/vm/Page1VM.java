package org.zkoss.demo.zkSocial.vm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;

public class Page1VM {
	
	@Wire Div divWorkspace;
	
	private Component component;
	
	@Init
	public void init() {
		
	}
	
	@AfterCompose
	public void doAfterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
	}
	
	public List<Person> getPersons() {
		List<Person> persons = new ArrayList<Person>();
		persons.add(new Person("Darth Vader"));
		persons.add(new Person("R2 D2"));
		persons.add(new Person("C3 PO"));
		persons.add(new Person("Obi Wan Kenobi"));
		persons.add(new Person("Luke Skywalker"));
		
		return persons;
	}
	
	@Command("edit-person")
	public void onEditPerson(@BindingParam("person") Person person) {
		component = divWorkspace.getLastChild();
		component.setVisible(false);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person", person);
		Executions.createComponents("page2.zul", divWorkspace, params);	
	}
	
	@GlobalCommand("close-person-editor")
	@NotifyChange("persons")
	public void onUpdatePersonsList(@BindingParam("person") Person person) {
		divWorkspace.getLastChild().detach();
		component.setVisible(true);
	}

}
