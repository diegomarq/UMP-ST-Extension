package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

public class MFragExtension extends MFrag {
	private GroupModel groupPointer;

	public MFragExtension(String name, MultiEntityBayesianNetwork mebn) {
		super(name, mebn);		
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

}
