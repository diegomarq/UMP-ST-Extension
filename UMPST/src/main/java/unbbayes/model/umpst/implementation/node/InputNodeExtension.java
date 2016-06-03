/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.prs.mebn.InputNode;
import unbbayes.prs.mebn.MFrag;

/**
 * Extension of InputNode considering UMP-ST related properties.
 * @author Diego Marques
 */
public class InputNodeExtension extends InputNode {
	
	private RelationshipModel relationshipPointer;

	/**
	 * @param name
	 * @param mFrag
	 */
	public InputNodeExtension(String name, MFrag mFrag) {
		super(name, mFrag);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the relationshipPointer
	 */
	public RelationshipModel getRelationshipPointer() {
		return relationshipPointer;
	}

	/**
	 * @param relationshipPointer the relationshipPointer to set
	 */
	public void setRelationshipPointer(RelationshipModel relationshipPointer) {
		this.relationshipPointer = relationshipPointer;
	}

}
