package unbbayes.model.umpst.implementation.node;

import java.awt.Color;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityNode;

public class UndefinedNode extends MultiEntityNode {
	
	private MFragExtension mfragExtension;
	private RelationshipModel relationshipPointer;
	
	public UndefinedNode(String name, MFragExtension mfragExtension) {
		super();		
		setName(name);
		setLabel(" ");
		setColor(new Color(176, 252, 131));
		
		this.mfragExtension = mfragExtension;
	}

	/**
	 * @return the mfragExtension
	 */
	public MFragExtension getMfragExtension() {
		return mfragExtension;
	}

	/**
	 * @param mfrag the mfragExtension to set
	 */
	public void setMfragExtension(MFragExtension mfragExtension) {
		this.mfragExtension = mfragExtension;
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
