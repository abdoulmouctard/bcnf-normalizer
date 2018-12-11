package work;

import core.Attribute;
import core.Core;
import core.FunctionalDependency;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;



public class Relation
{
    private List<Attribute> attributes;
    private List<FunctionalDependency> functionalDependencies;
    private List<String> combinaisons ;
    private Set<Attribute> comb ;
    private List<Attribute> resulTable;
    private String relationName;
    
    public Relation(String name, List<Attribute> attributes, List<FunctionalDependency> functionalDependencies)
    {
    	relationName = name;
        this.attributes = attributes;
        comb = new TreeSet<>();
        this.functionalDependencies = functionalDependencies;
        resulTable = new ArrayList<Attribute>();
    }

    public void setCombinaisons(List<String> combinaisons)
    {
		this.combinaisons = combinaisons;
	}
    
    public List<Attribute> getResulTable() { return resulTable; }
    
    
    public String getRelationName()
    {
		return relationName;
	}
    
    
    public void setFunctionalDependencies(List<FunctionalDependency> functionalDependencies)
    {
        this.functionalDependencies = functionalDependencies;   
    }
    
    

    /**
     * Retourne la première dependance fonctionnelle qui
     * voile le BCNF dans la table.
     */
    FunctionalDependency getFirstCNT()
    {
        for (Attribute attribute: resulTable) {
            if (attribute.getCnt()!= null&& !attribute.isSuperKey()) {
                return attribute.getCnt();//calculateCNT(attribute);
            }
        }
        return null;
    }

    
    /**
     * Cette fonction calcul les CNT de l'Attribute passé en paramètre
     * @param attribute
     * @return
     */
    private FunctionalDependency calculateCNT(Attribute attribute)
    {
        char [] cloture = attribute.getCloture().toCharArray();
        String nameTmp = attribute.getName();
        Set<Attribute> left = new TreeSet<>();
        Set<Attribute> right = new TreeSet<>();
        left.add(attribute);
        Attribute tmp;

        if (nameTmp.equals(attribute.getCloture())) return null;
        else {
        	int index;
	        for (char c : cloture) {
	            if(!nameTmp.contains(c+"") && 
	            		attributes.contains(new Attribute(c+""))) {
	                index = indexOfAttribute(c+"",attributes.iterator());
	                tmp = attributes.get(index);
	                right.add(tmp);
	                
	            }
	        }
	        
	        if (right.size() == 0) return null;

        	return new FunctionalDependency(left,right);
        }
    }
    
    /**
     * Determine la position de l'attribu associé à
     * la chaine de caractère attribute dans la list des attribus
     * attributes passée en parametre aussi.
     * @param attribue
     * @param attributes
     * @return
     */
    public static <T> int indexOfAttribute(String attribue ,Iterator<? extends Attribute> attributes)
    {
    	int cpt = 0;
    	while(attributes.hasNext()) {
    		if(Tools.areEquals(attribue,attributes.next().getName())) return cpt;
    		cpt++;
    	}
    	
        return -1;
    }

    public List<FunctionalDependency> getFunctionalDependencies()
    {
        return functionalDependencies;
    }

    public List<Attribute> getAttributes() { return attributes; }
    public Set<Attribute> getCombinaisons() { return comb; }
    
    
    /**
     * Methode qui calcul toutes les combinaisons possibles
     * des attribus et qui determine pour chaque attribu sa cloture,
     * son CNT, s'il est super clé et en fin s'il est clé.
     */
    public void computeAll()
    {
    	if(!functionalDependencies.isEmpty()) {
    	
	    	List<String> superKeyList = new ArrayList<>();
	    	boolean isSuperKey;
	    	
	    	for (String attrName : combinaisons) {    	  
				  Attribute attribute = new Attribute(attrName);
				  Core.applyAndLog(Attribute::parse, this.comb::addAll, attrName);
				  
				  attribute.setCloture(getComputedAttributes());
				  attribute.setCnt(calculateCNT(attribute));
				  
				  isSuperKey = testSuperKey(attribute.getCloture());
				  
				  attribute.setSuperKey(isSuperKey);
				
				  if(isSuperKey) superKeyList.add(attribute.toString());
				  resulTable.add(attribute);
				  comb.clear();
	        }
	        testKey(superKeyList);
	        setAttributesListValues();
    	}
    }
    	
    /**
     * Met à jour l'ensemble des attribus de la relation
     * depuis les resultats obtenu sur la table.
     */
    private void setAttributesListValues()
    {
		int index;
    	for (Attribute attr : attributes) {
			index = indexOfAttribute(attr.getName(),resulTable.iterator());
			Attribute tmp = resulTable.get(index);
			attr.setCloture(tmp.getCloture());
			attr.setCnt(tmp.getCnt());
			attr.setKey(tmp.isKey());
			attr.setSuperKey(tmp.isSuperKey());
		}
	}
    
    
    
    
    
    /**
     * Methode qui permet de calculer la cloture
     * des attribus
     * @return
     */
    Set<Attribute> getComputedAttributes()
    {
		Set<Attribute> computedAttributesSet = new TreeSet<>();
		
		
		computedAttributesSet.addAll(this.comb);
		
		Predicate<? super FunctionalDependency> p = (
				fd -> computedAttributesSet.containsAll(fd.getLeft()) 
				 && !computedAttributesSet.containsAll(fd.getRight()));
		
		while(this.functionalDependencies.stream().anyMatch(p)) {
			
			this.functionalDependencies.stream()
				.filter(p)
				.forEach(fd -> computedAttributesSet.addAll(fd.getRight()));
		}
		
		return computedAttributesSet;
	}
    
    
    @Override
    public String toString()
    {
    	int size;
    	StringBuilder builder = new StringBuilder();
    	
        builder.append(relationName);
        builder.append("=(");
        size = attributes.size();
        for (int i = 0; i < size; i++) {
        	builder.append(attributes.get(i).getName());
        	if (i < size - 1) {
            	builder.append(" ; ");	
			}
		}
        builder.append(") ");
        
        
        builder.append(" | ");
        
        
        builder.append("FD[");
        builder.append(relationName);
        builder.append("]={"); 
        size = functionalDependencies.size();
        for (int i = 0; i < size; i++) {
        	builder.append(functionalDependencies.get(i).toString());
        	if (i < size - 1) {
            	builder.append(" ; ");	
			}
		}
        builder.append("}");

        return builder.toString();
    }
   
    /**
     * Cette methode prend la table de tous les attribus
     * qui sont des super clés et determine ceux qui sont
     * des clés.
     * @param tabSuper
     */
    private void testKey(List<String> tabSuper)
    {
    	if(!tabSuper.isEmpty()) {
	        int minSize =tabSuper.get(0).length();
	        
	        for (String att: tabSuper) {
	            if(att.length()<minSize) minSize = att.length();
	        }
	        
	        int index;
	        for (String sup : tabSuper) {
	            if(sup.length()==minSize) {
	            	index = indexOfAttribute(sup,resulTable.iterator());
	            	resulTable.get(index).setKey(true);
	            }
	        }
        }
    }

    /**
     * Cette methode determine si l'attribut associé
     * à la cloture passée en paramètre est bien une super clé.
     * @param closure
     * @return
     */
    private boolean testSuperKey(String closure)
    {
        int cmp = 0;
        char[] clo = closure.toCharArray();
        for(char c : clo) {
            if(attributes.contains(new Attribute(c+""))) {
                cmp ++;
            }
        }
        if(cmp == attributes.size()) {
            return true;
        }
        return false;
    }
}