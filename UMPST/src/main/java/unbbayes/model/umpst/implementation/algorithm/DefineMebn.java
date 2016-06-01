package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;

import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.prs.mebn.MFrag;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * Mebn is a MultiEntityBayesianNetwork structure 
 * @author Diego Marques
 */
public class DefineMebn {

	private UMPSTProject umpstProject;
	private MultiEntityBayesianNetwork mebn;	
	
	public DefineMebn(UMPSTProject umpstProject) {
		this.umpstProject = umpstProject;		
		
		mebn = new MultiEntityBayesianNetwork(umpstProject.getModelName());
		
	}
	
	public List<MFrag> getMFragList() {
		return mebn.getDomainMFragList();
	}
	
	public void addMFrag(MFrag domainMFrag) {
		mebn.addDomainMFrag(domainMFrag);
	}
	
	public void removeMFrag(MFrag domainMFrag) {
		mebn.removeDomainMFrag(domainMFrag);
	}

	/**
	 * @return the mebn
	 */
	public MultiEntityBayesianNetwork getMebn() {
		return mebn;
	}

	/**
	 * @param mebn the mebn to set
	 */
	public void setMebn(MultiEntityBayesianNetwork mebn) {
		this.mebn = mebn;
	}
}
