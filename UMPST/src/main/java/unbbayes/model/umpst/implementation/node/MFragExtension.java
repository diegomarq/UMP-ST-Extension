/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.prs.mebn.ContextNode;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;

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
	private List<ResidentNodeExtension> residentNodeExtensionList;
	private List<InputNodeExtension> inputNodeExtensionList;
	private List<OrdinaryVariableModel> ordinaryVariablevModelList;
	

	/**
	 * @param name
	 * @param mebn
	 */
	public MFragExtension(String name, MultiEntityBayesianNetwork mebn,
			GroupModel group) {
		super(name, mebn);
		
		this.mebn = mebn;
		this.setGroupRelated(group);
		setResidentNodeExtensionList(new ArrayList<ResidentNodeExtension>());
		setInputNodeExtensionList(new ArrayList<InputNodeExtension>());
		setOrdinaryVariablevModelList(new ArrayList<OrdinaryVariableModel>());
	}
	
	/**
	 * Returns the index of {@link OrdinaryVariable} related to {@link OrdinaryVariableModel}
	 * @param ovModel
	 * @param mfragExtension
	 * @return
	 */
	public int getOrdinaryVariableIndexOf(OrdinaryVariableModel ovModel) {
		
		int i = -1;
		for (int j = 0; j < this.getOrdinaryVariableList().size(); j++) {
			
			OrdinaryVariable ov = this.getOrdinaryVariableList().get(j);
			if ((ovModel.getVariable().equals(ov.getName()) &&
					(ovModel.getTypeEntity().equals(ov.getValueType().toString())))) {
				return j;
			}
		}
		return i;
	}
	
	/**
	 * Check if there is the {@link OrdinaryVariableModel} in the {@link MFragExtension}.
	 * @param ovModel
	 * @return
	 */
	public boolean existsAsOrdinaryVariableModel(OrdinaryVariableModel ovModel) {
		for (int i = 0; i < this.getOrdinaryVariablevModelList().size(); i++) {
			OrdinaryVariableModel ovModelCompared = this.getOrdinaryVariablevModelList().get(i);
			if(ovModelCompared.equals(ovModel)) {
				return true;
			}
		}
		return false;
	}
	
	public void addInputNodeExtension(InputNodeExtension inputNode) {
		getInputNodeExtensionList().add(inputNode);
		super.addInputNode(inputNode);
	}
	
	public void removeInputNodeExtension(InputNodeExtension inputNode) {
		getInputNodeExtensionList().remove(inputNode);
		super.removeInputNode(inputNode);
	}
	
	public void addResidentNodeExtension(ResidentNodeExtension residentNode) {
		getResidentNodeExtensionList().add(residentNode);
		super.addResidentNode(residentNode);
	}
	
	public void removeResidentNodeExtension(ResidentNodeExtension residentNode) {
		getResidentNodeExtensionList().remove(residentNode);
		super.removeResidentNode(residentNode);
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
	
	public void addOrdinaryVariableModel(OrdinaryVariableModel ovModel) {
		getOrdinaryVariablevModelList().add(ovModel);
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
	 * @return the ordinaryVariablevModelList
	 */
	public List<OrdinaryVariableModel> getOrdinaryVariablevModelList() {
		return ordinaryVariablevModelList;
	}

	/**
	 * @param ordinaryVariablevModelList the ordinaryVariablevModelList to set
	 */
	public void setOrdinaryVariablevModelList(
			List<OrdinaryVariableModel> ordinaryVariablevModelList) {
		this.ordinaryVariablevModelList = ordinaryVariablevModelList;
	}

}
