/**
 * 
 */
package de.cognicrypt.codegenerator.taskintegrator.models;

import java.util.ArrayList;

import de.cognicrypt.codegenerator.Constants;
import de.cognicrypt.codegenerator.Constants.FeatureType;

/**
 * @author rajiv
 *
 */
public class ClaferFeature {
	private FeatureType featureType;
	private String featureName;
	private String featureInheritance;
	private ArrayList<FeatureProperty> featureProperties; // <String name, String value>. The names in this collection cannot be repeated.
	private ArrayList<ClaferConstraint> featureConstraints; // each constraint will be generated as a "valid" string in the constraint generator pop up.
	/**
	 * @param featureType
	 * @param featureName
	 * @param featureInheritance
	 * 
	 * 
	 */
	public ClaferFeature(FeatureType featureType, String featureName, String featureInheritance) {
		super();
		this.setFeatureType(featureType);
		this.setFeatureName(featureName);
		this.setFeatureInheritance(featureInheritance);
		this.featureProperties = new ArrayList<FeatureProperty>();
		this.featureConstraints = new ArrayList<ClaferConstraint>();
	}
	/**
	 * @return the featureType
	 */
	public FeatureType getFeatureType() {
		return featureType;
	}
	/**
	 * @param featureType the featureType to set
	 */
	public void setFeatureType(FeatureType featureType) {
		this.featureType = featureType;
	}
	/**
	 * @return the featureName
	 */
	public String getFeatureName() {
		return featureName;
	}
	/**
	 * @param featureName the featureName to set
	 */
	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	public String getFeatureInheritance() {
		return featureInheritance;
	}

	public void setFeatureInheritance(String featureInheritance) {
		this.featureInheritance = featureInheritance;
	}

	/**
	 * @return the properties
	 */
	public ArrayList<FeatureProperty> getfeatureProperties() {
		return featureProperties;
	}

	public void setFeatureProperties(ArrayList<FeatureProperty> featureProperties) {
		this.featureProperties = featureProperties;
	}
	
	public boolean hasProperties() {
		return !featureProperties.isEmpty();
	}
	
	public boolean hasProperty(String needle) {
		for (FeatureProperty featureProperty : getfeatureProperties()) {
			if (featureProperty.getPropertyName() == needle) {
				return true;
			}
		}
		
		return false;
	}

	public ArrayList<ClaferConstraint> getFeatureConstraints() {
		return featureConstraints;
	}

	public void setFeatureConstraints(ArrayList<ClaferConstraint> featureConstraints) {
		this.featureConstraints = featureConstraints;
	}
	
	public boolean hasConstraints() {
		return !featureConstraints.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder strRepresentation = new StringBuilder();
		
		if (featureType == Constants.FeatureType.ABSTRACT) {
			strRepresentation.append("abstract ");
		}
		
		strRepresentation.append(getFeatureName());
		
		if (!getFeatureInheritance().isEmpty()) {
			strRepresentation.append(": ");
			strRepresentation.append(getFeatureInheritance());
		}
		
		for (FeatureProperty featureProperty : getfeatureProperties()) {
			strRepresentation.append("\n\t");
			strRepresentation.append(featureProperty.toString());			
		}
		
		for (ClaferConstraint featureConstraint : getFeatureConstraints()) {
			strRepresentation.append("\n\t");
			strRepresentation.append(featureConstraint.toString());
		}
		
		strRepresentation.append("\n");
		
		return strRepresentation.toString();
	}

	/**
	 * Implement features that this Clafer uses for inheritance or as property types if missing in featureList and add them to the list
	 * 
	 * @param featureList
	 *        haystack list for missing features and to be updated with new features
	 */
	public void implementMissingFeatures(ArrayList<ClaferFeature> featureList) {
		// find missing inherited feature
		if (!getFeatureInheritance().isEmpty()) {
			boolean parentFound = false;
			for (ClaferFeature cfrFeature : featureList) {
				if (cfrFeature.getFeatureName().equals(getFeatureInheritance())) {
					parentFound = true;
					break;
				}
			}

			// implement missing inherited feature
			if (!parentFound) {
				ClaferFeature parentFeature = new ClaferFeature(Constants.FeatureType.ABSTRACT, getFeatureInheritance(), "");
				featureList.add(parentFeature);
			}
		}

		// find missing property types
		for (FeatureProperty fp : getfeatureProperties()) {
			boolean propertyTypeFound = false;
			for (ClaferFeature cfrFeature : featureList) {
				if (cfrFeature.getFeatureName().equals(fp.getPropertyType())) {
					propertyTypeFound = true;
					break;
				}
			}

			// implement missing property types			
			if (!propertyTypeFound) {
				ClaferFeature propertyTypeFeature = new ClaferFeature(Constants.FeatureType.CONCRETE, fp.getPropertyType(), "");
				featureList.add(propertyTypeFeature);
			}
		}
	}

	public void removeUnusedFeatures(ArrayList<ClaferFeature> featureList) {
		for (ClaferFeature cfrFeature : featureList) {
			// check usage of cfrFeature			
			boolean used = false;
			if (cfrFeature.hasProperties()) {
				used = true;
			}
			if (cfrFeature.hasConstraints()) {
				used = true;
			}
			
			// if abstract and somebody inherits -> used
			for (ClaferFeature refFeature : featureList) {
				if (refFeature.getFeatureInheritance() == cfrFeature.getFeatureName()) {
					// usage found: refFeature inherits from cfrFeature
					used = true;
				}
			}

			for (ClaferFeature refFeature : featureList) {
				for (FeatureProperty featureProp : refFeature.getfeatureProperties()) {
					if (featureProp.getPropertyType() == cfrFeature.getFeatureName()) {
						// usage found: featureProp is of type cfrFeature
						used = true;
					}
				}
			}

			if (!used) {
				System.out.println(cfrFeature.getFeatureName() + " unused");
			}
		}
	}

	public void printModel(ArrayList<ClaferFeature> claferModel) {
		// TODO This should not be an class method
		StringBuilder sb = new StringBuilder();
		for (ClaferFeature cfrFeature : claferModel) {
			sb.append(cfrFeature.toString());
		}
		
		System.out.println(sb);
	}
	
}
