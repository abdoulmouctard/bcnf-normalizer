package work;

import core.Attribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * Cette class renferme l'ensemble 
 * des methodes intermediaire, 
 * qui sont partagées dans le programme
 *
 */
public class Tools
{
    private Tools() {}
    
    /**
     * Tranforme l'entree de l'Utilisateur en une Relation
     * @param input 
     * @return
     * @throws Exception
     */
    public static Relation createRelation(String input) throws Exception
    {
        String[] array = input.split(";");
        List<Attribute> attributes = new ArrayList<>();

        for (String str: array) {
            if (str.length() != 1 || ! Character.isLetter(str.charAt(0))) {
                throw new Exception("You need to enter a letters and separate them by ';'");
            }
            attributes.add(new Attribute(str));
        }
        return new Relation("R",attributes,null);
    }
    
    /**
     * Fonction qui part d'une relation, et retourne toutes 
     * les combinaisons possibles des attribus de la relation.
     * @param relation
     * @return
     */
    public static List<String> combine(Relation relation)
    {
        List<String> letters = new ArrayList<>();
        List<String> combination = new ArrayList<>(letters);
        
        relation.getAttributes().forEach(attr-> letters.add(attr.getName()));
        combination.addAll(letters);

        for (int i = 0; i < letters.size(); i++) {
            combination.addAll(combine_bis(letters, i));
        }
        

        StringBuilder allAttributes = new StringBuilder();
        letters.forEach(allAttributes::append);
        combination.add(allAttributes.toString());

        combination = trim(combination);
        
        return combination;
    }

    /**
     * Permet d'assurer l'intégrité des combinaisons, en:
     * supprimant les doublons, les attribus dupliqué, ...
     * @param combination
     * @return
     */
    private static List<String> trim(List<String> combination)
    {
    	List<String> list = new ArrayList<>();
    	combination.forEach(c->list.add(removeDuplicatedChars(c)));
    	
    	List<String> result = new ArrayList<>();
    	
    	boolean matched = false;
    	
    	for (String string : list) {
			if (!result.contains(string)) {
				matched = false;
				for(String str_res: result){
					if(areEquals(str_res, string)) {
						matched = true;
						break;
					}
				}
				if (!matched) result.add(string);
			}
		}
    	
        return result;
    }
    
    /**
     * Utilisé par la fonction combine, cette fonction 
     * fait vagement le travail de combinaisons des elements
     * @param elements
     * @param length
     * @return
     */
    private static List<String> combine_bis(List<String> elements, int length)
    {
        if (elements.size() == 1 || length <= 1) { return elements; }
        List<String> sub = combine_bis(elements,length-1);
        List<String> all = new ArrayList<>();

        StringBuilder tmp;
        for (String element : elements) {
            for (String aSub : sub) {
                tmp = new StringBuilder(element);
                for (char c : aSub.toCharArray()) {
                    if (tmp.toString().indexOf(c) <= 0) {
                        tmp.append(c);
                    }
                }
                all.add(tmp.toString());
            }
        }
        return all;
    }


    /**
     * Transforme un Set<T> en List<T>
     * @param sets
     * @return
     */
    public static <T> List<T> seToList(Set<T> sets)
    {
        Iterator<T> iterator = sets.iterator();
        List<T> list = new ArrayList<>();

        while (iterator.hasNext())
        {
            list.add(iterator.next());
        }
        return  list;
    }
    
    /**
     * Fonction utilisée dans le nettoyage des attribus
     * @param string
     * @return
     */
    private static String removeDuplicatedChars(String string)
    {
    	StringBuilder builder = new StringBuilder();
    	
    	for (char c : string.toCharArray()) {
			if (!builder.toString().contains(""+c)) {
				builder.append(c);
			}
		}
    	
    	return builder.toString();
	}
    
    /**
     * Fonction permetatnt de comparer les Strings de deux Attribus
     * @param attr1
     * @param attr2
     * @return
     */
    static boolean areEquals(String attr1, String attr2)
    {	
    	if (attr1.equals(attr2)) return true;
		
		if( attr1.length() != attr2.length()) return false;
		
		int matched = 0;
		char[] attr1_chars = attr1.toCharArray();
		char[] attr2_chars = attr2.toCharArray();
		
		for (int i = 0; i < attr1_chars.length; i++) {
			for (int j = 0; j < attr2_chars.length; j++) {	
				if(attr1_chars[i] == attr2_chars[j]) {
					matched++;
				}						
			}
		}
		
		return matched == attr1.length();
	}  
    
    

}