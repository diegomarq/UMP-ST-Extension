package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;

import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.MebnExtension;
import unbbayes.model.umpst.project.UMPSTProject;

/**
 * Mebn is a MultiEntityBayesianNetwork structure 
 * @author Diego Marques
 */
public class DefineMebn {

	private UMPSTProject umpstProject;
	private MebnExtension mebnExtension;
	
	public DefineMebn(UMPSTProject umpstProject) {
		this.umpstProject = umpstProject;		
	
		mebnExtension = new MebnExtension(umpstProject.getModelName());
	}
	
	public List<MFragExtension> getMFragExtensionList() {
		return mebnExtension.getDomainMFragExtensionList();
	}
	
	public void addMFragExtension(MFragExtension domainMFrag) {
		mebnExtension.addDomainMFragExtension(domainMFrag);
		mebnExtension.addDomainMFrag(domainMFrag);
	}
	
	public void removeMFragExtension(MFragExtension domainMFrag) {
		mebnExtension.removeDomainMFragExtension(domainMFrag);
	}

	/**
	 * @return the mebnExtension
	 */
	public MebnExtension getMebnExtension() {
		return mebnExtension;
	}

	/**
	 * @param mebn the mebnExtension to set
	 */
	public void setMebnExtension(MebnExtension mebnExtension) {
		this.mebnExtension = mebnExtension;
	}
}
