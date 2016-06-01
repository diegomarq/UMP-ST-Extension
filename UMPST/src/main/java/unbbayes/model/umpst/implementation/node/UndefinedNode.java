package unbbayes.model.umpst.implementation.node;

import java.awt.Color;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityNode;

public class UndefinedNode extends MultiEntityNode {
	
	private MFrag mfrag;
	private RelationshipModel relationshipPointer;
	
	public UndefinedNode(String name, MFrag mfrag) {
		super();		
		setName(name);
		setLabel(" ");
		setColor(new Color(176, 252, 131));
		
		this.mfrag = mfrag;
	}

	/**
	 * @return the mfrag
	 */
	public MFrag getMfrag() {
		return mfrag;
	}

	/**
	 * @param mfrag the mfrag to set
	 */
	public void setMfrag(MFrag mfrag) {
		this.mfrag = mfrag;
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
