/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import unbbayes.model.umpst.implementation.OrdinaryVariableModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.OrdinaryVariable;
import unbbayes.prs.mebn.entity.Type;

/**
 * Ordinary Variable Extension. It includes properties related to OrdinaryVariableModel
 * of UMP-ST
 * @author Diego Marques
 */
public class OrdinaryVariableExtension extends OrdinaryVariable {
	
	private OrdinaryVariableModel ordinaryVariableModel;

	/**
	 * @param name
	 * @param type
	 * @param mFrag
	 */
	public OrdinaryVariableExtension(String name, Type type, MFrag mFrag,
			OrdinaryVariableModel ovModel) {
		super(name, type, mFrag);
		
		setOrdinaryVariableModel(ovModel);
	}

	/**
	 * @return the ordinaryVariableModel
	 */
	public OrdinaryVariableModel getOrdinaryVariableModel() {
		return ordinaryVariableModel;
	}

	/**
	 * @param ordinaryVariableModel the ordinaryVariableModel to set
	 */
	public void setOrdinaryVariableModel(OrdinaryVariableModel ordinaryVariableModel) {
		this.ordinaryVariableModel = ordinaryVariableModel;
	}

}
