package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;
import java.util.List;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.ContextNode;
import unbbayes.prs.mebn.InputNode;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.ResidentNode;
import unbbayes.prs.mebn.entity.Type;

public class MFragExtension extends MFrag {
	private GroupModel groupPointer;
	private List<UndefinedNode> undefinedNodeList;
	private List<ResidentNodeExtension> residentNodeExtensionList;
	private List<InputNodeExtension> inputNodeExtensionList;
	private List<ContextNodeExtension> contextNodeExtensionList;
	
	private List<OrdinaryVariableExtension> ordinaryVariableExtensionList;

	public MFragExtension(String name, MebnExtension mebnExtension) {
		super(name, mebnExtension);
		
		undefinedNodeList = new ArrayList<UndefinedNode>();
		residentNodeExtensionList = new ArrayList<ResidentNodeExtension>();
		inputNodeExtensionList = new ArrayList<InputNodeExtension>();
		contextNodeExtensionList = new ArrayList<ContextNodeExtension>();
		
		ordinaryVariableExtensionList = new ArrayList<OrdinaryVariableExtension>();
	}	
	
	public void addOrdinaryVariableExtension(OrdinaryVariableExtension ovExtension) {
		ordinaryVariableExtensionList.add(ovExtension);
		super.addOrdinaryVariable((OrdinaryVariable)ovExtension);
	}
	
	public void removeOrdinaryVariableExtension(OrdinaryVariableExtension ovExtension) {
		ordinaryVariableExtensionList.remove(ovExtension);
		super.removeOrdinaryVariable(ovExtension);
	}
	
	public void addInputNodeExtension(InputNodeExtension inputNodeExtension) {
		inputNodeExtensionList.add(inputNodeExtension);
		super.addInputNode((InputNode)inputNodeExtension);
	}
	
	public void removeInputNodeExtension(InputNodeExtension inputNodeExtension) {
		inputNodeExtensionList.remove(inputNodeExtension);
		super.removeInputNode((InputNode)inputNodeExtension);
	}
	
	public void addContextNodeExtension(ContextNodeExtension contextNodeExtension) {
		contextNodeExtensionList.add(contextNodeExtension);
		super.addContextNode((ContextNode)contextNodeExtension);
	}
	
	public void removeContextNodeExtension(ContextNodeExtension contextNodeExtension) {
		contextNodeExtensionList.remove(contextNodeExtension);
		super.removeContextNode((ContextNode)contextNodeExtension);
	}
	
	public void addResidentNodeExtension(ResidentNodeExtension residentNodeExtension) {
		residentNodeExtensionList.add(residentNodeExtension);
		super.addResidentNode((ResidentNode)residentNodeExtension);
	}
	
	public void removeResindentNodeExtension(ResidentNodeExtension resindeNodeExtension) {
		residentNodeExtensionList.remove(resindeNodeExtension);
		super.removeResidentNode((ResidentNode)resindeNodeExtension);
	}
	
	public void addUndefinedNode(UndefinedNode node) {
		undefinedNodeList.add(node);
	}
	
	public void removeUndefinedNode(UndefinedNode node) {
		undefinedNodeList.remove(node);
	}	

	/**
	 * @return the groupPointer
	 */
	public GroupModel getGroupPointer() {
		return groupPointer;
	}

	/**
	 * @param groupPointer the groupPointer to set
	 */
	public void setGroupPointer(GroupModel groupPointer) {
		this.groupPointer = groupPointer;
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

	/**
	 * @return the inputNodeExtensionList
	 */
	public List<InputNodeExtension> getInputNodeExtensionList() {
		return inputNodeExtensionList;
	}

	/**
	 * @param inputNodeExtensionList the inputNodeExtensionList to set
	 */
	public void setInputNodeExtensionList(List<InputNodeExtension> inputNodeExtensionList) {
		this.inputNodeExtensionList = inputNodeExtensionList;
	}

	/**
	 * @return the contextNodeExtensionList
	 */
	public List<ContextNodeExtension> getContextNodeExtensionList() {
		return contextNodeExtensionList;
	}

	/**
	 * @param contextNodeExtensionList the contextNodeExtensionList to set
	 */
	public void setContextNodeExtensionList(List<ContextNodeExtension> contextNodeExtensionList) {
		this.contextNodeExtensionList = contextNodeExtensionList;
	}

	/**
	 * @return the ordinaryVariableExtensionList
	 */
	public List<OrdinaryVariableExtension> getOrdinaryVariableExtensionList() {
		return ordinaryVariableExtensionList;
	}

	/**
	 * @param ordinaryVariableExtensionList the ordinaryVariableExtensionList to set
	 */
	public void setOrdinaryVariableExtensionList(
			List<OrdinaryVariableExtension> ordinaryVariableExtensionList) {
		this.ordinaryVariableExtensionList = ordinaryVariableExtensionList;
	}

}
