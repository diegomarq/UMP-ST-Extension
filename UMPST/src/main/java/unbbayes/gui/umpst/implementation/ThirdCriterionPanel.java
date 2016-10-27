/**
 * 
 */
package unbbayes.gui.umpst.implementation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import unbbayes.controller.umpst.MappingController;
import unbbayes.model.umpst.exception.IncompatibleEventException;
import unbbayes.model.umpst.exception.IncompatibleQuantityException;
import unbbayes.model.umpst.implementation.CauseVariableModel;
import unbbayes.model.umpst.implementation.EventMappingType;
import unbbayes.model.umpst.implementation.node.MFragExtension;
import unbbayes.model.umpst.implementation.node.UndefinedNode;
import unbbayes.prs.mebn.MultiEntityBayesianNetwork;

/**
 * @author Diego Marques
 *
 */
public class ThirdCriterionPanel extends JPanel {
	
	private MappingController mappingController;
	private Map<MFragExtension, List<UndefinedNode>> mapMFragRelated;
	private List<UndefinedNode> userHypothesisList;
	private Vector<String> argList;
	
	private String inputType = "Input Type";
	private String residentType = "Resident Type";
	
	public ThirdCriterionPanel(MappingController mappingController, List<UndefinedNode> undefinedNodeList,
			MultiEntityBayesianNetwork mebn) {
		super();
		this.mappingController = mappingController;
		
		// Map all mfrag related to the undefined nodes
		setMapMFragRelated(new HashMap<MFragExtension, List<UndefinedNode>>());
		mapAllMFragRelatedTo(undefinedNodeList, 0);
		
		// Initialize node type map
		setUserHypothesisList(new ArrayList<UndefinedNode>());
		
		// Set the options to the user choose
		setArgList(new Vector<String>());			
		getArgList().add(null);
		getArgList().add(inputType);
		getArgList().add(residentType);
		
		try {
			createMappingPanel(undefinedNodeList, mebn);
		} catch (IncompatibleEventException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Keep the type and node related
	class ComboListener implements ItemListener {		
		UndefinedNode node;
		Vector<String> typeList;		
		
		public ComboListener(UndefinedNode _node, Vector<String> _typeList){
			node = _node;
			typeList = _typeList;
		}
		
		public void itemStateChanged(ItemEvent e) {
			JComboBox combo = (JComboBox)e.getSource(); 
			if(combo.getSelectedItem() != null){
				Object type = combo.getSelectedItem(); // The object is string argument
				
				if (type != null) {
					
					if(type.toString().equals(inputType)) {
						node.setMappingType(EventMappingType.INPUT);
						getUserHypothesisList().add(node);
					}
					else if(type.toString().equals(residentType)) {
						node.setMappingType(EventMappingType.RESIDENT);
						getUserHypothesisList().add(node);
					}
				}
			}
		}		
	}
	
	public JPanel createSelectionTypePanel(MFragExtension mfragRelated, List<UndefinedNode> undefinedNodeListRelated)
			throws IncompatibleEventException {
		
		JLabel mfragName = new JLabel(mfragRelated.getName());
		mfragName.setOpaque(true); 
		mfragName.setHorizontalAlignment(JLabel.CENTER); 
		mfragName.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
		
		JPanel nodePanel = new JPanel(new BorderLayout());		
		nodePanel.add(mfragName, BorderLayout.CENTER);
		
		JComboBox typeArgument[] = new JComboBox[undefinedNodeListRelated.size()+1];
		
		JToolBar nodeItem;
		JButton btnNodeNumber;
		JButton btnNodeType;
		
		//Build ComboBox for each undefinedNode
		for(int i = 0; i < undefinedNodeListRelated.size(); i++){
			
			nodeItem = new JToolBar();
			
			// Event options type
			typeArgument[i] = new JComboBox(getArgList());
			typeArgument[i].addItemListener(new ComboListener(undefinedNodeListRelated.get(i), argList)); 
			
			//Adding components to panel
			// Number of event
			btnNodeNumber = new JButton("" + (i+1));
			btnNodeNumber.setBackground(new Color(193, 207, 180));
			
			// Event name			
			Object event = undefinedNodeListRelated.get(i).getEventRelated();
			if(event instanceof CauseVariableModel) {				
				btnNodeType = new JButton(((CauseVariableModel)event).getRelationship()); 
				btnNodeType.setBackground(new Color(193, 210, 205)); 
			}
			else {
				throw new IncompatibleEventException("Invalid event. It suposes to be " + CauseVariableModel.class);
			}
			
			nodeItem.add(btnNodeNumber); 
			nodeItem.add(btnNodeType); 
			nodeItem.add(typeArgument[i]); 
			nodeItem.setFloatable(false);
			
			nodePanel.add(nodeItem);
			
		}
		return nodePanel;
	}
	
	public void createMappingPanel(final List<UndefinedNode> undefinedNodeList, MultiEntityBayesianNetwork mebn)
			throws IncompatibleEventException {
		
		final JFrame frame = new JFrame("Select event type");
		JPanel argPane = new JPanel(new BorderLayout());
		
		int numberOfMFrag = getMapMFragRelated().size();
		
		JPanel listPane =  new JPanel(new BorderLayout());
		listPane.setLayout(new GridLayout(2*numberOfMFrag + undefinedNodeList.size(), 1));
		
		Map<MFragExtension, List<UndefinedNode>> _mapMFragRelated = getMapMFragRelated();
		Set<MFragExtension> keyMap = _mapMFragRelated.keySet();
		
		for (MFragExtension mFragExtension : keyMap) {
			List<UndefinedNode> undefinedNodeListRelated = _mapMFragRelated.get(mFragExtension);			
			
			JPanel itemPanel = createSelectionTypePanel(mFragExtension, undefinedNodeListRelated);
			listPane.add(itemPanel);
			listPane.add(new JPanel());
		}

		argPane.add(listPane);		
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		
		JButton btnOk = new JButton("Confirm"); 
		btnOk.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				// Check if there are null arguments
				if (getUserHypothesisList().size() > 0) {
					mappingController.setHypothesisListCase(getUserHypothesisList());
					frame.dispose();
				}
				else {
					try {
						throw new IncompatibleQuantityException("Number of elements selected are not compatible to the nodes present");
					} catch (IncompatibleQuantityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		JToolBar tbArgX;
		tbArgX = new JToolBar();		
		tbArgX.setFloatable(false);
//		tbArgX.setLayout(new GridLayout());	
		tbArgX.add(new JPanel());
		tbArgX.add(btnOk);
		tbArgX.add(btnClose);
		
		argPane.add(tbArgX, BorderLayout.PAGE_END);
//		argPane.add(toolBar);
		
		frame.add(argPane);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//		frame.setLocationRelativeTo(buttonBackAtributes);
		frame.setSize(300,150);
		frame.setVisible(true);
	}
	
	/**
	 * Map All {@link MFragExtension} related to the {@link UndefinedNode}.
	 * @param undefinedNodeList
	 * @param numberOfMFrag
	 * @param index
	 * @return
	 */
	public void mapAllMFragRelatedTo(List<UndefinedNode> undefinedNodeList, int index) {
		
		if(undefinedNodeList.size() > 0){
			UndefinedNode undefinedNodeSearched = undefinedNodeList.get(index);
						
			// Keep and remove all undefined node related to the undefinedNode searched
			List<UndefinedNode> newUndefinedNodeList = keepUndefinedNodeEqualOf(undefinedNodeSearched, undefinedNodeList);			
			mapAllMFragRelatedTo(newUndefinedNodeList, index);
		}		
	}
	
	/**
	 * Keep {@link UndefinedNode} related to the {@link UndefinedNode} related to the {@link MFragExtension} and 
	 * remove their of the {@link UndefinedNode} list passed as parameter.
	 * @param undefinedNodeSearched
	 * @param undefinedNodeList
	 * @return
	 */
	public List<UndefinedNode> keepUndefinedNodeEqualOf(UndefinedNode undefinedNodeSearched,
			List<UndefinedNode> undefinedNodeList) {
		
		List<UndefinedNode> newUndefinedNodeList = undefinedNodeList;
		newUndefinedNodeList.remove(undefinedNodeSearched);
		
		// Get undefined node related to mfrag
		List<UndefinedNode> undefinedNodeListRelated = new ArrayList<UndefinedNode>();
		undefinedNodeListRelated.add(undefinedNodeSearched);
		
		// Remove and get all undefined node related to the undefined node searched
		for (int i = 0; i < undefinedNodeList.size(); i++) {
			UndefinedNode undefinedNodeCompared = undefinedNodeList.get(i);
			
			MFragExtension mfragCompared = undefinedNodeCompared.getMfragExtension();
			MFragExtension mfragSearched = undefinedNodeSearched.getMfragExtension();
			
			if(mfragSearched.equals(mfragCompared)) {
				newUndefinedNodeList.remove(undefinedNodeCompared);
				undefinedNodeListRelated.add(undefinedNodeCompared);
			}
		}
		
		// Put the mfragRelated and the undefinedNodeList Related in a map
		MFragExtension mfragRelated = undefinedNodeSearched.getMfragExtension();
		getMapMFragRelated().put(mfragRelated, undefinedNodeListRelated);
		
		return newUndefinedNodeList;
	}

	public Map<MFragExtension, List<UndefinedNode>> getMapMFragRelated() {
		return mapMFragRelated;
	}

	public void setMapMFragRelated(Map<MFragExtension, List<UndefinedNode>> mapMFragRelated) {
		this.mapMFragRelated = mapMFragRelated;
	}

	public Vector<String> getArgList() {
		return argList;
	}

	public void setArgList(Vector<String> argList) {
		this.argList = argList;
	}	

	public List<UndefinedNode> getUserHypothesisList() {
		return userHypothesisList;
	}

	public void setUserHypothesisList(List<UndefinedNode> userHypothesisList) {
		this.userHypothesisList = userHypothesisList;
	}	
}
