package unbbayes.model.umpst.implementation.algorithm;

import java.util.List;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.group.GroupModel;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.project.UMPSTProject;
import unbbayes.prs.mebn.MFrag;

public class DefineMapping {
	
	private UMPSTProject umpstProject;
	private MappingController mappingController;
	
	private DefineMebn defineMebn;
	private DefineMFrag defineMFrag;
	
	public DefineMapping(UMPSTProject umpstProject, MappingController mappingController) {
		this.umpstProject = umpstProject;
		this.mappingController = mappingController;
		
		createMebn();
		createMFrags();
		createUndefinedNodes();
	}
	
	public void createUndefinedNodes() {
		List<MFrag> listMFrag = defineMebn.getMFragList();
		for (MFrag mfrag : listMFrag) {
			
		}
	}
	
	public void createMebn() {
		defineMebn = new DefineMebn(umpstProject);
	}
	
	public void createMFrags() {
		defineMFrag = new DefineMFrag(
				umpstProject.getMapGroups(),
				defineMebn.getMebn(), this);
	}
	
	public void addGroupPointer(MFragExtension mfrag, GroupModel groupPointer) {
		mfrag.setGroupPointer(groupPointer);
	}
	
	public void addMFrag(MFrag domainMFrag) {
		defineMebn.addMFrag(domainMFrag);
	}
	
	public void removeMFrag(MFrag domainMFrag) {
		defineMebn.removeMFrag(domainMFrag);
	}
}
