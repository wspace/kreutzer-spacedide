package diagram;

import java.util.HashMap;

public class LabelRegistry {
	
	private HashMap<Integer, Integer> labelMap;
	
	public LabelRegistry() {
		labelMap = new HashMap<Integer, Integer>();
	}
	
	/**
	 * <p>Marks a label at this specific position.
	 * Label IDs can only used once.</p>
	 * @param labelID A unique label identifier
	 * @param pos The position to jump to
	 */
	public void markLabel(int labelID, int pos) {
		if (isMarked(labelID))
			throw new RuntimeException("The label " + labelID + " has already been marked!");
		labelMap.put(labelID, pos);
	}
	
	/**
	 * <p>Gives the position to jump to when this label is reached.</p>
	 * @param labelID The label identifier
	 * @return
	 */
	public int getMarkedPosition(int labelID) {
		if (!isMarked(labelID))
			throw new RuntimeException("The label " + labelID + " is not registered!");
		return labelMap.get(labelID);
	}
	
	/**
	 * <p>Returns true if this identifier is already listed.</p>
	 * @param labelID 
	 * @return The usage status of the given label ID
	 */
	public boolean isMarked(int labelID) {
		return labelMap.containsKey(labelID);
	}
	
	/**
	 * <p>Clears the label registry.</p>
	 */
	public void reset() {
		labelMap.clear();
	}

}
