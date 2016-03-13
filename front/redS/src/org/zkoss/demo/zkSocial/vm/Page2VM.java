package org.zkoss.demo.zkSocial.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.util.Clients;

public class Page2VM {

	private Person person;
	private int i = 0;
	
	@Init
	public void init(@ExecutionArgParam("person") Person person) {
		this.person = person;
	}
	
	@Command("save") 
	public void onSave() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("person", person);
		BindUtils.postGlobalCommand(null, null, "close-person-editor", params);
	}
	
	public List<String> getPositions() {
		List<String> positions = new ArrayList<String>();
		positions.add("Jedai");
		positions.add("Robot");
		positions.add("Sith");
		
		System.out.println("Page2VM -> getPositions() called " + ++i + " times");
		Clients.showNotification("The getter Page2VM.getPositions() called: " + i);
		return positions;
	}

	public Person getPerson() {
		return person;
	}
}
