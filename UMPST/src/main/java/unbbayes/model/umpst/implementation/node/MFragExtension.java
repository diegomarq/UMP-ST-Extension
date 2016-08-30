/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.prs.mebn.ContextNode;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.entity.Type;

/**
 * Class modified to add group model
 * @author Diego Marques
 *
 */
public class MFragExtension extends MFrag {
	
	private GroupModel groupRelated;
	private static ResourceBundle resource = unbbayes.util.ResourceController.newInstance().getBundle(
			unbbayes.controller.mebn.resources.Resources.class.getName());
	private MultiEntityBayesianNetwork mebn;
	private List<UndefinedNode> undefinedNodeList;
	private List<ResidentNodeExtension> residentNodeExtensionList;

	/**
	 * @param name
	 * @param mebn
	 */
	public MFragExtension(String name, MultiEntityBayesianNetwork mebn,
			GroupModel group) {
		super(name, mebn);
		
		this.mebn = mebn;
		this.setGroupRelated(group);
		setUndefinedNodeList(new ArrayList<UndefinedNode>());
		setResidentNodeExtensionList(new ArrayList<ResidentNodeExtension>());
	}
	
	public void addResidentNodeExtension(ResidentNodeExtension residentNode) {
		getResidentNodeExtensionList().add(residentNode);
		super.addResidentNode(residentNode);
	}
	
	public void removeResidentNodeExtension(ResidentNodeExtension residentNode) {
		getResidentNodeExtensionList().remove(residentNode);
		super.removeResidentNode(residentNode);
	}
	
	public void removeUndefinedNode(UndefinedNode node) {
		getUndefinedNodeList().remove(node);
	}
	
	public void addUndefinedNode(UndefinedNode node) {
		getUndefinedNodeList().add(node);
	}
	
	/**
	 * Create {@link ContextNode} from {@link NecessaryConditionVariableModel} of 
	 * UMP-ST implementation panel
	 * @param ncModel
	 * @return
	 */
	public ContextNode addContextNode(NecessaryConditionVariableModel ncModel) {
		
		String name = null;
		while (name == null){
			name = resource.getString("contextNodeName") + mebn.getContextNodeNum(); 
			if(mebn.getNamesUsed().contains(name)){
				name = null;
				mebn.plusContextNodeNul();
			}
		}
		
		ContextNode node = new ContextNode(name, this);
		mebn.getNamesUsed().add(name);
		super.addContextNode(node);
		return node;
	}
	
	/**
	 * Adds an {@link OrdinaryVariable} to the {@link MultiEntityBayesianNetwork}
	 * @param ordinaryVariableModel
	 */
	public void addOrdinaryVariable(OrdinaryVariable ordinaryVariable) {
		super.addOrdinaryVariable(ordinaryVariable);
	}

	/**
	 * @return the group
	 */
	public GroupModel getGroupRelated() {
		return groupRelated;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroupRelated(GroupModel groupRelated) {
		this.groupRelated = groupRelated;
	}

	/**
	 * @return the undefinedNodeList
	 */
	public List<UndefinedNode> getUndefinedNodeList() {
		return undefinedNodeList;
	}

	/**
	 * @param undefinedNodeList the undefinedNodeList to set
	 */
	public void setUndefinedNodeList(List<UndefinedNode> undefinedNodeList) {
		this.undefinedNodeList = undefinedNodeList;
	}

	/**
	 * @return the residentNodeExtensionList
	 */
	public List<ResidentNodeExtension> getResidentNodeExtensionList() {
		return residentNodeExtensionList;
	}

	/**
	 * @param residentNodeExtensionList the residentNodeExtensionList to set
	 */
	public void setResidentNodeExtensionList(
			List<ResidentNodeExtension> residentNodeExtensionList) {
		this.residentNodeExtensionList = residentNodeExtensionList;
	}

}
