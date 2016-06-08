package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;

import org.eclipse.osgi.framework.debug.Debug;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.entity.RelationshipModel;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.model.umpst.project.UMPSTProject;

/**
 * Create MEBN Extension structures, MFrag Extension according to UMP-ST's groups and
 * Undefined Nodes related to all relationship models in UMP-ST.
 * @author Diego Marques
 */
public class DefineMapping {
	
	private UMPSTProject umpstProject;
	private MappingController mappingController;
	
	private DefineMebn defineMebnExtension;
	private DefineMFrag defineMFragExtension;
	
	public DefineMapping(UMPSTProject umpstProject, MappingController mappingController) {
		this.umpstProject = umpstProject;
		this.mappingController = mappingController;
		
		createMebnExtension();
		Debug.println("MTheoryExtension created: " + getDefineMebnExtension().getMebnExtension().getName());
		createMFragsExtension();
		Debug.println("MFrags created: " + getDefineMebnExtension().getMFragExtensionList().size());
		createUndefinedNodes();
		// put code here to map attributes as RVs
	}
	
	/**
	 * TODO create a new method to map attributes as UndefinedNodes
	 */
	
	public void createUndefinedNodes() {
		List<MFragExtension> listMFragExtension = defineMebnExtension.getMFragExtensionList();
		for (MFragExtension mfragExtension : listMFragExtension) {
			
			GroupModel group = mfragExtension.getGroupPointer();
			List<RelationshipModel> relationshipList = group.getBacktrackingRelationship();
			for(RelationshipModel relationship : relationshipList) {
				
				String name = relationship.getName();
				UndefinedNode node = new UndefinedNode(name, mfragExtension);
				node.setRelationshipPointer(relationship);
				mfragExtension.addUndefinedNode(node);
			}
		}
	}
	
	public void createMebnExtension() {
		setDefineMebnExtension(new DefineMebn(umpstProject));
	}
	
	public void createMFragsExtension() {
		defineMFragExtension = new DefineMFrag(
				umpstProject.getMapGroups(),
				getDefineMebnExtension().getMebnExtension(), this);
	}
	
	public void addGroupPointer(MFragExtension mfragExtension, GroupModel groupPointer) {
		mfragExtension.setGroupPointer(groupPointer);
	}
	
	public void addMFragExtension(MFragExtension domainMFragExtension) {
		getDefineMebnExtension().addMFragExtension(domainMFragExtension);
	}
	
	public void removeMFrag(MFragExtension domainMFrag) {
		getDefineMebnExtension().removeMFragExtension(domainMFrag);
	}

	/**
	 * @return the defineMebnExtension
	 */
	public DefineMebn getDefineMebnExtension() {
		return defineMebnExtension;
	}

	/**
	 * @param defineMebnExtension the defineMebnExtension to set
	 */
	public void setDefineMebnExtension(DefineMebn defineMebnExtension) {
		this.defineMebnExtension = defineMebnExtension;
	}
}
