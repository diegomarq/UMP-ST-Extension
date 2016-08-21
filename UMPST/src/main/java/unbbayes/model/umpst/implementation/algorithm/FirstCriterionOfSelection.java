package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.osgi.framework.debug.Debug;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;
import unbbayes.prs.mebn.ResidentNode;

/**
 * Separate resident nodes according the relationship presence in a groupList.
 * If relationshipModel is in just one group then it is a resident node.
 * @author Diego Marques
 */
public class FirstCriterionOfSelection {
	
	private UMPSTProject umpstProject;
	private MappingController mappingController;

	public FirstCriterionOfSelection(UMPSTProject umpstProject, MappingController mappingController,
			MultiEntityBayesianNetwork mebn) {
		
		this.umpstProject = umpstProject;
		this.mappingController = mappingController;
		
		
//		nodeResidentList = new ArrayList<NodeResidentModel>();
		
//		mapMFragExtension = mappingController.getMapMFragExtension();
		
		firstSelection(mebn);
		Debug.println(this.getClass() + "Fist Criterion of Selection done.");
	}
	
	/**
	 * Search nodes declared once in MTheory and maps them to resident.
	 */
	public void firstSelection(MultiEntityBayesianNetwork mebn) {
		
		Map<String, MFragExtension> mapMFragExtension = mappingController.getMapMFragExtension();
		Set<String> keys = mapMFragExtension.keySet();
		TreeSet<String> sortedKeys = new TreeSet<String>(keys);
		
		for (String groupId : sortedKeys) {	
			MFragExtension mfragExtension = mapMFragExtension.get(groupId);
			List<UndefinedNode> undefinedNodeList = mfragExtension.getUndefinedNodeList();
			
			for (int i = 0; i < undefinedNodeList.size(); i++) {
				
				UndefinedNode undefinedNode = undefinedNodeList.get(i);				
				RelationshipModel relationship = undefinedNode.getRelationshipPointer();
				if (relationship.getFowardtrackingGroups().size() == 1) {
					
					String name = undefinedNode.getName();
					ResidentNode residentNode = new ResidentNode(name, mfragExtension);
					residentNode.setDescription(residentNode.getName());
					mfragExtension.addResidentNode(residentNode);
					
					mfragExtension.removeUndefinedNode(undefinedNode);
//					mFragExtension.addResidentNodeExtension(residentNode);
				}
			}			
		}
	}
}
