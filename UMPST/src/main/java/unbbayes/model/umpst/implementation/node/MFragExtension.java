package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;
import java.util.List;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.ResidentNode;

public class MFragExtension extends MFrag {
	private GroupModel groupPointer;
	private List<UndefinedNode> undefinedNodeList;
	private List<ResidentNodeExtension> residentNodeExtensionList;
	private List<InputNodeExtension> inputNodeExtensionList;
	private List<ContextNodeExtension> contextNodeExtensionList;

	public MFragExtension(String name, MebnExtension mebnExtension) {
		super(name, mebnExtension);
		
		undefinedNodeList = new ArrayList<UndefinedNode>();
		residentNodeExtensionList = new ArrayList<ResidentNodeExtension>();
		inputNodeExtensionList = new ArrayList<InputNodeExtension>();
		contextNodeExtensionList = new ArrayList<ContextNodeExtension>();
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

}
