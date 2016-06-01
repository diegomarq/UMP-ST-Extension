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

	private ArrayList<MFragExtension> domainMFragList;

	/**
	 * @param name
	 */
	public MebnExtension(String name) {
		super(name);		
		
		domainMFragList = new ArrayList<MFragExtension>();
	}
	
	

}
