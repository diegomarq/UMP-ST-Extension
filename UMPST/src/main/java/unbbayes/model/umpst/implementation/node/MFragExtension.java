/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import java.util.List;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.entity.Type;

/**
 * Class modified to add group model
 * @author Diego Marques
 *
 */
public class MFragExtension extends MFrag {
	
	private GroupModel groupRelated;

	/**
	 * @param name
	 * @param mebn
	 */
	public MFragExtension(String name, MultiEntityBayesianNetwork mebn,
			GroupModel group) {
		super(name, mebn);
		
		this.setGroupRelated(group);
	}
	
	public OrdinaryVariable mapOrdinaryVariable(OrdinaryVariableModel
			ordinaryVariableModel) {
		Type type = this.getMultiEntityBayesianNetwork().
				getTypeContainer().getType(ordinaryVariableModel.getTypeEntity());
		
		OrdinaryVariable ov = new OrdinaryVariable(
				ordinaryVariableModel.getVariable(), type, this);
		
		return ov;
	}
	
	public void addOrdinaryVariable(OrdinaryVariableModel ordinaryVariableModel) {
		OrdinaryVariable ov = mapOrdinaryVariable(ordinaryVariableModel);
		this.addOrdinaryVariable(ov);
		
		System.out.println(ov.getName()+ " -- MFragExtension");
	}
	
	public void addAllOrdinaryVariables(RuleModel rule) {
		List<OrdinaryVariableModel> ovModelList = rule.getOrdinaryVariableList();
		for (int i = 0; i < ovModelList.size(); i++) {
			OrdinaryVariableModel ovModel = ovModelList.get(i);
			this.addOrdinaryVariable(ovModel);
		}
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

}
