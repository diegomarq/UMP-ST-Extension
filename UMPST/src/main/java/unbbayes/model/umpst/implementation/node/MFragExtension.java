/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * @author diego
 *
 */
public class MFragExtension extends MFrag {
	
	private GroupModel groupRelated;

	/**
	 * @param name
	 * @param mebn
	 */
	public MFragExtension(String name, MultiEntityBayesianNetwork mebn) {
		super(name, mebn);
		
		// TODO Auto-generated constructor stub
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
