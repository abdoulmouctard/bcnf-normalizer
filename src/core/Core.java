package core;

import work.BCNF;
import work.Relation;
import work.Tools;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.PatternSyntaxException;



public class Core implements Runnable
{

    private Relation relation;
    private List<Relation> decomposedRelations;
    private Set<FunctionalDependency> functionalDependencies;
    private boolean stop = false;
    private final Scanner scanner;
    private final static CoreLogger logger = new Log();
    private final static String PATTERN_NO_ARGS = "^!\\w+$";
    private final static String PATTERN_ARGS = "^!\\w+:.+$";
    private final static String PATTERN_SPLIT = "[!:]";

    
    public Core(Scanner scanner)
    {
        this.functionalDependencies = new TreeSet<>();
        this.scanner = scanner;
        logger.help();
    }


    public Core() { this(new Scanner(System.in)); }
    
    /**
     * Arret du programme
     * @param stop
     * @return
     */
    private void stopThread()
    {
    	this.stop = true;
    }

    /**
     * Le Main processus
     */
    public void run()
    {
        while(!stop) {
            logger.printNewLine();
            parseInput(requestInput());
        }
    }

    /**
     * Recupère les saisies de l'utilisateur
     * sur l'entré standard
     * @return
     */
    public String requestInput()
    {
        try {return scanner.next(); }
        catch (NoSuchElementException e) { stopThread(); return "";}
    }
    
    /**
     * Methode qui permet d'associer
     * chaque saisie valide de l'utilisateur
     * à une action
     * @param input
     * @return
     */
    public boolean parseInput(String input)
    {
        String [] words = input.split(PATTERN_SPLIT, 3);
        
        if(input.matches(PATTERN_ARGS)) {
            switch(words[1]) {
                case "R":
                	try{
                		relation = Tools.createRelation(words[2]);
                		logger.notifyLog(2,words[2]);
                	}catch (Exception e) {logger.printMessage(e.getMessage());}    
                    break;
                case "fd":
                    applyAndLog(FunctionalDependency::parse, this.functionalDependencies::add, words[2]);
                    logger.notifyLog(1,words[2]);
                    relation.setFunctionalDependencies(Tools.seToList(functionalDependencies));
                    break;
                case "superkey":
                	attributeIsSuperKey(words[2]);
                    break;
                case "key":
                	attributeIsKey(words[2]);
                    break;
                default:
                    logger.error("Unknown command. Type !help to show available commands");
                    return false;
            }
        }
        
        if(input.matches(PATTERN_NO_ARGS)) {
            switch(words[1]) {
                case "compute" :
                	compute();
                	break;
                case "quit": case "exit":
                	stopThread();
                	break;
                case "print":
                	printAll();
                	break;
                case "isbcnf":
                	currentRelationIsBcnf();
                	break;
                case "decompose":
                	decompose();
                	break;
                case "clear":
                	clear();
                	break;
                case "help":
                	help();
                	break;
                default:
                	logger.error("Unknown command. Type !help to show available commands");
                    return false;
            }
        }
        return true;
    }
    
    
    private boolean canProceed()
    {
    	boolean can;
    	if(relation== null||functionalDependencies.isEmpty()) { 
    		logger.error("R or E is empty!!");
    		can = false;
    	} else if (relation.getResulTable().isEmpty()) {
    		logger.error("Compute the table before by typing <<!compute>>");
    		can = false;
    	} else {
    		can = true;
    	}
    	return can;
    }
    
    /**
     * Methode qui decompose la Relation
     * courante en DNCF, et l'affiche
     * sur la sortie Standard
     */
    private void decompose()
    {
    	if(canProceed()) {
    		logger.printNewLine();
    		logger.printHeaders("DECOMPOSITION");
    		logger.printNewLine();
    		
    		decomposedRelations = (new BCNF(relation,logger)).decompose();
    		logger.printNewLine();
    		logger.printMessage("FINALLY, WE HAVE "+decomposedRelations.size()+" RELATION(S) :");
    		
    		decomposedRelations.forEach(r -> logger.printRelation(r));
    	}
    }
    
	/**
	 * Methode qui lance le test et Log
	 * si une Relation est en BCNF ou NON
	 */
    private void currentRelationIsBcnf()
	{
		if(canProceed()) {
			if(!BCNF.isBCNF(relation)) {
				logger.printMessage("THIS RELATION IS NOT IN BCNF");
				logger.printMessage("ENTER <<!decompose>> TO DECOMPOSE IT IN BCNF");
			} else {
				logger.printMessage("THIS RELATION IS IN BCNF");
			}
		}
	}

    
	private void compute()
	{
        if(relation == null ||functionalDependencies.isEmpty()){
            logger.error("R or E is empty!!");
        } else {
        	relation.setCombinaisons(Tools.combine(relation));
            relation.computeAll();
            logger.printTable(relation.getResulTable(),relation.toString());
        }
    }
	

	private void attributeIsSuperKey(String strOfAttribute)
	{
		KeyTester(strOfAttribute, true);
	}
	
	private void attributeIsKey(String strOfAttribute)
	{
		KeyTester(strOfAttribute, false);
	}
	
	
	
	/**
	 * Factorise les deux tests de Clé et SuperClé qui se ressemblent 
	 * @param strOfAttribute
	 * @param superkey
	 */
	private void KeyTester(String strOfAttribute, boolean superkey)
	{
		if(canProceed()) {
			
			int index;
			List<Attribute>combinaison;
			
			if(decomposedRelations != null && !decomposedRelations.isEmpty()) {
				
				for (Relation relation : decomposedRelations) {
					relation.computeAll();
					combinaison = relation.getResulTable();
					index = Relation.indexOfAttribute(strOfAttribute, combinaison.iterator());
					
					if (index>=0) {
						if (superkey && combinaison.get(index).isSuperKey()) {
							logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS A SUPER KEY IN : <<"+relation.toString()+" >>");
		            		return;
						}else if(!superkey && combinaison.get(index).isKey()) {
							logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS A KEY IN : <<"+relation.toString()+" >>");
		            		return;
						}
					}
				}

				if (superkey) { logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS NOT A SUPER  KEY");}
				else { logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS NOT A KEY");}
				return;
				
			}else {
				combinaison =  relation.getResulTable();
				index=Relation.indexOfAttribute(strOfAttribute,combinaison.iterator());
				
				if (index>=0) {
					if (superkey && combinaison.get(index).isSuperKey()) {
						logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS A SUPER KEY IN : <<"+relation.toString()+" >>");
	            		return;
					}else if(!superkey && combinaison.get(index).isKey()) {
						logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS A KEY IN : <<"+relation.toString()+" >>");
	            		return;
					}
					
					if (superkey) { logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS NOT A SUPER  KEY");
					}else { logger.printMessage("THE ATTRIBUTE <<"+strOfAttribute+">> IS NOT A KEY");}
					
					return;

				}
			}
			
            logger.error("UNKNOWN ATTRIBUTE <<"+strOfAttribute+">>. DECOMPOSE THE RELATION WITH <<!decompose>>");
        }
	}	
	

 
    /**
     * Reinitaialise les différents champs
     * de la classe
     */
    private void clear()
    {
      functionalDependencies.clear();
      relation = null; //new Relation(null,null, null);
      decomposedRelations=null;
    }

    
    public static <E> void applyAndLog(Function<String, E> parse, Consumer<E> add, String input)
    {
        try {
            E inputParsed = parse.apply(input);
            add.accept(inputParsed);
        } catch (PatternSyntaxException e) { logger.error(e.getDescription());}
    }

    /**
     * Affiche la table de decomposition d'une Relation
     */
    private void printAll()
    {
    	if (decomposedRelations != null && !decomposedRelations.isEmpty()) {
			decomposedRelations.forEach(r-> {
				if (r.getFunctionalDependencies()!=null && !r.getFunctionalDependencies().isEmpty()) {
					logger.printTable(r.getAttributes(), r.toString());					
				}
			});
			return;
		}
    	
    	if(relation == null || relation.getResulTable().isEmpty())
    		logger.error("Execute <<!compute>> before");
    	else
    		logger.printTable(relation.getResulTable(),relation.toString());    		
    }

    /**
     * Log sur la sortie Standard
     * le panneau des commandes disponible
     * dans le programme
     */
    private void help()
    {
        logger.help();
    }
}