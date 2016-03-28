package org.zkoss.demo.zkSocial.tree;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
import org.zkoss.demo.zkSocial.data.Contact;
import org.zkoss.demo.zkSocial.data.ContactList;
import org.zkoss.demo.zkSocial.registro.RandomStringGenerator;
import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.ContactBlock;
import org.ws.util.entidad.Usuario;

public class DemoComposer extends SelectorComposer<Component> {
	private static final long serialVersionUID = 3814570327995355261L;
	
	@Wire
	private Window demoWindow;
	@Wire
	private Tree tree;
	@Wire
	private Menupopup editPopup;
	private AuthorBean currentUser;

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}

	private AdvancedTreeModel contactTreeModel;
	     
	    @Listen("onClick = #closeBtn")
	    public void showModal(Event e) {
	    	demoWindow.detach();
	    }
	    
	    private RandomStringGenerator rsg = new RandomStringGenerator(6);
		private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
		
		private Session sess = Sessions.getCurrent();
		private Usuario userLogin;
	    @Listen("onClick = #bloquear")
	    public void menuAction(Event event){
	    	userLogin = (Usuario) sess.getAttribute("userCredential");
			currentUser = userLogin.getUsuarioLogin();
	    	Treecell c= (Treecell)tree.getSelectedItem().getTreerow().getChildren().get(0);
	    	Treeitem i = tree.getSelectedItem();
	    	ContactTreeNode ctn = i.getValue();
	    	Contact ct = ctn.getData();
	    	ContactBlock contact = new ContactBlock();
	    	contact.setId(currentUser.getId_persona());
	    	contact.setName(ct.getName());
	    	
	    	//System.out.println(tree.getSelectedItem().getTreerow().getLabel());
	    	System.out.println(ct.getName());
	    	ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/bloquearAmigo",contact, request.getSession());
	    	i.setDisabled(true);
	    	Messagebox.show("Se bloqueará este usuario de su lista de contactos", "Informativo", Messagebox.OK, Messagebox.INFORMATION);
	    }
	
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);		
		contactTreeModel = new AdvancedTreeModel(new ContactList().getRoot());
		tree.setItemRenderer(new ContactTreeRenderer());
		tree.setModel(contactTreeModel);
		tree.setContext(editPopup);
	}

	/**
	 * The structure of tree
	 * 
	 * <pre>
	 * &lt;treeitem>
	 *   &lt;treerow>
	 *     &lt;treecell>...&lt;/treecell>
	 *   &lt;/treerow>
	 *   &lt;treechildren>
	 *     &lt;treeitem>...&lt;/treeitem>
	 *   &lt;/treechildren>
	 * &lt;/treeitem>
	 * </pre>
	 */
	private final class ContactTreeRenderer implements TreeitemRenderer<ContactTreeNode> {
		@Override
		public void render(final Treeitem treeItem, ContactTreeNode treeNode, int index) throws Exception {
			ContactTreeNode ctn = treeNode;
			Contact contact = (Contact) ctn.getData();
			Treerow dataRow = new Treerow();
			dataRow.setParent(treeItem);
			treeItem.setValue(ctn);
			treeItem.setOpen(ctn.isOpen());

			if (!isCategory(contact)) { // Contact Row
				Hlayout hl = new Hlayout();
				hl.appendChild(new Image(contact.getProfilepic()));
				hl.appendChild(new Label(contact.getName()));
				hl.setSclass("h-inline-block");
				Treecell treeCell = new Treecell();
				treeCell.appendChild(hl);
				dataRow.setDraggable("true");
				dataRow.appendChild(treeCell);
				dataRow.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
					@Override
					public void onEvent(Event event) throws Exception {
						ContactTreeNode clickedNodeValue = (ContactTreeNode) ((Treeitem) event.getTarget().getParent())
								.getValue();
						Window w = new Window("ZK IM - " + ((Contact) clickedNodeValue.getData()).getName(), "normal",
								true);
						w.setPosition("parent");
						w.setParent(demoWindow);
						HashMap<String, String> dataArgs = new HashMap<String, String>();
						dataArgs.put("name", clickedNodeValue.getData().getName());
						Executions.createComponents("/widgets/tree/dynamic_tree/dialog.zul", w, dataArgs);
						w.doOverlapped();
					}
				});
			} else { // Category Row
				dataRow.appendChild(new Treecell(contact.getCategory()));
			}
			// Both category row and contact row can be item dropped
			dataRow.setDroppable("true");
			dataRow.addEventListener(Events.ON_DROP, new EventListener<Event>() {
				@SuppressWarnings("unchecked")
				@Override
				public void onEvent(Event event) throws Exception {
					// The dragged target is a TreeRow belongs to an
					// Treechildren of TreeItem.
					Treeitem draggedItem = (Treeitem) ((DropEvent) event).getDragged().getParent();
					ContactTreeNode draggedValue = (ContactTreeNode) draggedItem.getValue();
					Treeitem parentItem = treeItem.getParentItem();
					contactTreeModel.remove(draggedValue);
					if (isCategory((Contact) ((ContactTreeNode) treeItem.getValue()).getData())) {
						contactTreeModel.add((ContactTreeNode) treeItem.getValue(),
								new DefaultTreeNode[] { draggedValue });
					} else {
						int index = parentItem.getTreechildren().getChildren().indexOf(treeItem);
						if(parentItem.getValue() instanceof ContactTreeNode) {
							contactTreeModel.insert((ContactTreeNode)parentItem.getValue(), index, index,
									new DefaultTreeNode[] { draggedValue });
						}
						
					}
				}
			});

		}

		private boolean isCategory(Contact contact) {
			return contact.getName() == null;
		}
	}
}