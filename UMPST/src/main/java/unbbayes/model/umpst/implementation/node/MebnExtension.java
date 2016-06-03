/**
 * 
 */
package unbbayes.model.umpst.implementation.node;

import java.util.ArrayList;
import java.util.List;

import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * Extension class to deal with UMP-ST mapping to MEBN/PR-OWL 2 
 * @author Diego Marques
 */
public class MebnExtension extends MultiEntityBayesianNetwork {

	private ArrayList<MFragExtension> domainMFragExtensionList;

	/**
	 * @param name
	 */
	public MebnExtension(String name) {
		super(name);
		
		setDomainMFragExtensionList(new ArrayList<MFragExtension>());
	}
	
	public void addDomainMFragExtension(MFragExtension domainMFragExtension) {
		domainMFragExtensionList.add(domainMFragExtension);
		super.addDomainMFrag((MFrag)domainMFragExtension);
	}
	
	public void removeDomainMFragExtension(MFragExtension domainMFragExtension) {
		domainMFragExtension.delete();
		domainMFragExtensionList.remove(domainMFragExtension);
		super.removeDomainMFrag((MFrag)domainMFragExtension);
	}

	/**
	 * @return the domainMFragExtensionList
	 */
	public ArrayList<MFragExtension> getDomainMFragExtensionList() {
		return domainMFragExtensionList;
	}

	/**
	 * @param domainMFragExtensionList the domainMFragExtensionList to set
	 */
	public void setDomainMFragExtensionList(ArrayList<MFragExtension> domainMFragExtensionList) {
		this.domainMFragExtensionList = domainMFragExtensionList;		
	}
	
	

}
