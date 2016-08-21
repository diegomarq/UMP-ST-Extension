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
	}
	
	public ResidentNode mapToResidentNode(UndefinedNode undefinedNode) {
		ResidentNode residentNode = new ResidentNode(undefinedNode.getName(), this);
		
		RelationshipModel relationship = undefinedNode.getRelationshipPointer();
		
		//TODO get the list of ordinary variables present in event and add their to resident
		//node list of arguments
		
//		for (int i = 0; i < relationship.get; i++) {
//			
//		}
//		residentNode.addArgument(ordinaryVariable, true);
		return residentNode;
	}
	
	public void removeUndefinedNode(UndefinedNode node) {
		getUndefinedNodeList().remove(node);
	}
	
	public void addUndefinedNode(UndefinedNode node) {
		getUndefinedNodeList().add(node);
	}
	
	public UndefinedNode mapToUndefinedNode(RelationshipModel relationship) {
		
		UndefinedNode node = new UndefinedNode(relationship.getName(), this);
		node.setRelationshipPointer(relationship);
		return node;
	}
	
//	public void mapContextNodeFormula(ContextNode node, NecessaryConditionVariableModel ncModel,
//			MEBNController mebnController) {
//		FormulaTreeController formulaControllerMebn = new FormulaTreeController(mebnController, node, null);
//		
//		NodeFormulaTreeUMP rootFormulaUMP = ncModel.getFormulaTree();
//		DefaultMutableTreeNode rootTreeView = new DefaultMutableTreeNode();
//		
//		for(NodeFormulaTreeUMP child: rootFormulaUMP.getChildrenUMP()){
//			DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(child); 
//			nodeTreeFather.add(treeNode);
//			buildChildren(child, treeNode); 
//		}
//		return nodeTreeFather;
//	}
	
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
		this.addContextNode(node);
		return node;
	}
	
	/**
	 * Maps an {@link OrdinaryVariableModel} created in UMP-ST implementation panel to
	 * an {@link OrdinaryVariable} of Mebn structure
	 * @param ordinaryVariableModel
	 * @return
	 */
	public OrdinaryVariable mapOrdinaryVariable(OrdinaryVariableModel
			ordinaryVariableModel) {
		
		String typeName = ordinaryVariableModel.getTypeEntity();
		Type type = MappingController.getType(this.getMultiEntityBayesianNetwork(), typeName);
		
		OrdinaryVariable ov = new OrdinaryVariable(
					ordinaryVariableModel.getVariable(), type, this);
		
		return ov;
	}
	
	/**
	 * Adds an {@link OrdinaryVariable} to the {@link MultiEntityBayesianNetwork}
	 * @param ordinaryVariableModel
	 */
	public void addOrdinaryVariable(OrdinaryVariableModel ordinaryVariableModel) {
		OrdinaryVariable ov = mapOrdinaryVariable(ordinaryVariableModel);
		super.addOrdinaryVariable(ov);
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

}
