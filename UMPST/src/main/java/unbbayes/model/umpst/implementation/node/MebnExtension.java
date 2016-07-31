/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;

import unbbayes.model.umpst.rule.RuleModel;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * Extension class to deal with UMP-ST mapping to MEBN/PR-OWL 2 
 * @author Diego Marques
 */
public class MebnExtension extends MultiEntityBayesianNetwork {

	private ArrayList<MFragExtension> mFragExtensionList;

	/**
	 * @param name
	 */
	public MebnExtension(String name) {
		super(name);
		
		setMFragExtensionList(new ArrayList<MFragExtension>());
	}
	
	public void addMFragExtension(MFragExtension mFragExtension) {
		mFragExtensionList.add(mFragExtension);
		super.addDomainMFrag((MFrag)mFragExtension);
	}
	
	public void removeMFragExtension(MFragExtension mFragExtension) {
		mFragExtension.delete();
		mFragExtensionList.remove(mFragExtension);
		super.removeDomainMFrag((MFrag)mFragExtension);
	}

	/**
	 * @return the mFragExtensionList
	 */
	public ArrayList<MFragExtension> getMFragExtensionList() {
		return mFragExtensionList;
	}

	/**
	 * @param mFragExtensionList the mFragExtensionList to set
	 */
	public void setMFragExtensionList(ArrayList<MFragExtension> mFragExtensionList) {
		this.mFragExtensionList = mFragExtensionList;		
	}
	
	

}
