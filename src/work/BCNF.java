package work;

import core.Attribute;
import core.CoreLogger;
import core.FunctionalDependency;

import java.util.*;

public class BCNF
{
    private List<Relation> relationsDecomposees;
    private Relation baseRelation;
    private  CoreLogger logger ;
    
    public BCNF(Relation baseRelation , CoreLogger l )
    {
        this.relationsDecomposees = new ArrayList<>();
        this.baseRelation = baseRelation;
        logger =  l;
    }
    
    
    /**
     * Test si une Relation est en BCNF
     * @param relation
     * @return
     */
    public static boolean isBCNF(Relation relation)
    {
    	List<Attribute> attributes = relation.getAttributes();
    	if(attributes.size()==2) return true;
        for (Attribute attribute: relation.getResulTable()) {
            if (attribute.getCnt()!= null && !attribute.isSuperKey()) {
                return false;
            }
        }
        return true;
    }
    
    
    /**
     * Cette fonction lance la decomposistion en BCNF
     * de la Relation <baseRelation>.
     * commande: !decompose
     */
    public List<Relation> decompose()
    {
    	// logger.printMessage("--- START OF DECOMPOSITION ---");
    	decompose_bis(baseRelation);
    	return relationsDecomposees;
    }
    
    
    /**
     * Implementation de l'algorithme de
     * decomposition en BCNF sur une Relation passé en paramètre
     * @param relation
     */
    private void decompose_bis(Relation relation)
    {
        if (isBCNF(relation)) {
        	// logger.printMessage(relation.toString()+"is BCNF");
            relationsDecomposees.add(relation);
            return;
        }

        FunctionalDependency cnt = relation.getFirstCNT();
        if (cnt == null) return;
        
        Iterator<Attribute> iterator = cnt.getLeft().iterator();
        Attribute tmp;
        List<Attribute> attributesR1 = new ArrayList<>();
        List<Attribute> attributesR2 = new ArrayList<>();
        List<Attribute> attributes = relation.getAttributes();

        while (iterator.hasNext()) {
            tmp = iterator.next();
            
            attributesR2.addAll(Tools.seToList(Attribute.parse(tmp.getName())));
            for (char c: tmp.getCloture().toCharArray()) {
                if(attributes.contains(new Attribute(""+c)))
            	attributesR1.add(new Attribute(""+c));
            }
        }

        
        attributes.forEach(a -> {
        	if(!attributesR1.contains(a)) {
        		attributesR2.add(a);        		
        	}
        });


        List<FunctionalDependency> dependency = relation.getFunctionalDependencies();
        List<FunctionalDependency> functionalDependenciesR1 = DFilter(dependency,attributesR1);
        
        Relation relation1 = new Relation(relation.getRelationName()+"1",attributesR1, functionalDependenciesR1);

        List<FunctionalDependency> functionalDependenciesR2 = DFilter(dependency,attributesR2);
        Relation relation2 = new Relation(relation.getRelationName()+"2",attributesR2, functionalDependenciesR2);

        relation1.setCombinaisons(Tools.combine(relation1));
        relation2.setCombinaisons(Tools.combine(relation2));
        
        logger.printDecomposition(cnt , relation1 , relation2) ;
        
        relation1.computeAll();
        logger.printTable(relation1.getResulTable(),relation1.toString());
        
        relation2.computeAll();
        logger.printTable(relation2.getResulTable(),relation2.toString());

        decompose_bis(relation1);
        decompose_bis(relation2);
    }


    private List<FunctionalDependency> DFilter(List<FunctionalDependency> fd, List<Attribute> attr)
    {
        List<FunctionalDependency> result = new ArrayList<>();
        List<Attribute> container = new ArrayList<>();

        for(FunctionalDependency f : fd) {
            Iterator<Attribute> right  = f.getRight().iterator();
            Iterator<Attribute> left = f.getLeft().iterator();
            
            while(right.hasNext()) container.add(right.next());
            while (left.hasNext()) container.add(left.next());
            
            if (attr.containsAll(container)) result.add(f);
            
            container.clear();
        }
        
        return  result;
    }

}
