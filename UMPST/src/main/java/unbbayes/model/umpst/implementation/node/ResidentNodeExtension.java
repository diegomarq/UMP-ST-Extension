/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.ResidentNode;

/**
 * Extension of ResidentNode considering UMP-ST related properties.
 * @author Diego Marques
 */
public class ResidentNodeExtension extends ResidentNode {
	
	private RelationshipModel relationshipPointer;

	/**
	 * @param name
	 * @param mFrag
	 */
	public ResidentNodeExtension(String name, MFrag mFrag) {
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
