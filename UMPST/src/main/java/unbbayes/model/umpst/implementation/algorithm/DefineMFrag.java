package unbbayes.model.umpst.implementation.algorithm;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.MebnExtension;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * Create and make operations over MFrags.
 * @author Diego Marques
 */
public class DefineMFrag {
	
	private Map<String, GroupModel> mapGroup;
	private MebnExtension mebnExtension;
	private DefineMapping defineMapping;
	
	public DefineMFrag(Map<String, GroupModel> mapGroup, MebnExtension mebnExtension,
			DefineMapping defineMapping) {
		this.mapGroup = mapGroup;
		this.defineMapping = defineMapping;
		this.mebnExtension = mebnExtension;
		
		createMFrags();
	}
	
	/**
	 * Create all MFrags from set of groups.
	 */
	public void createMFrags() {
		Set<String> keys = mapGroup.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String key : sortedKeys) {
			GroupModel group = mapGroup.get(key);
			
			String id = group.getId();
			String name = group.getName();
			
			MFragExtension mfragExtension = new MFragExtension(name, mebnExtension);
//			MFragModel mfrag = new MFragModel(id, name);
			// Adds MFrags in MTheory			
			defineMapping.addGroupPointer(mfragExtension, group);
			defineMapping.addMFragExtension(mfragExtension);
		}
	} 
}