package org.zkoss.demo.zkSocial.tree;

import java.util.List;

import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.demo.zkSocial.data.Contact;

public class ContactTreeNode extends DefaultTreeNode<Contact> {
	private static final long serialVersionUID = -7012663776755277499L;
	
	private boolean open = true;

	public ContactTreeNode(Contact data, DefaultTreeNode<Contact>[] children) {
		super(data, children);
	}

	public ContactTreeNode(Contact data, DefaultTreeNode<Contact>[] children, boolean open) {
		super(data, children);
		setOpen(open);
	}

	public ContactTreeNode(Contact data) {
		super(data);

	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
