/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.implementation.NecessaryConditionVariableModel;
import unbbayes.prs.mebn.ContextNode;
import unbbayes.prs.mebn.MFrag;

/**
 * Extension of ContextNode considering UMP-ST related properties.
 * @author Diego Marques
 */
public class ContextNodeExtension extends ContextNode {
	
	private NecessaryConditionVariableModel ncConditionPointer;

	/**
	 * @param name
	 * @param mFrag
	 */
	public ContextNodeExtension(MFragExtension mFragExtension, 
			NecessaryConditionVariableModel ncConditionPointer, String name) {
			
		super(name, mFragExtension);
		this.ncConditionPointer = ncConditionPointer;
	}

	/**
	 * @return the ncConditionPointer
	 */
	public NecessaryConditionVariableModel getNcConditionPointer() {
		return ncConditionPointer;
	}

	/**
	 * @param ncConditionPointer the ncConditionPointer to set
	 */
	public void setNcConditionPointer(NecessaryConditionVariableModel ncConditionPointer) {
		this.ncConditionPointer = ncConditionPointer;
	}

}
