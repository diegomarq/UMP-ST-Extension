package unbbayes.model.umpst.implementation.node;

import java.awt.Color;

import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityNode;

public class UndefinedNode {
	
	private MFragExtension mfragExtension;
	private Object eventRelated;
	
	/**
	 * Node that was not defined in the criteria of selection.
	 * @param event
	 * @param mfragExtension
	 */
	public UndefinedNode(Object event, MFragExtension mfragExtension) {
		setEventRelated(event);		
		setMfragExtension(mfragExtension);
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
	 * The event can be a {@link CauseVariableModel}
	 * @return object
	 */
	public Object getEventRelated() {
		return eventRelated;
	}
	
	/**
	 * {@link CauseVariableModel} related
	 * @param eventRelated
	 */
	public void setEventRelated(Object eventRelated) {
		this.eventRelated = eventRelated;
	}	
}
