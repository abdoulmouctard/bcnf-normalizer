package core;

import java.util.List;
import java.util.Set;

import work.Relation;

public class Log implements CoreLogger
{
    private static final int LENGTH = 20;
    final static String LINE_START = "$ " ;

    private void print(String message)
    {
        System.out.print(LINE_START + message);
    }

    private void println(String message)
    {
        System.out.println(LINE_START + message);
    }

    @Override
    public void printNewLine()
    {
        println("");
    }

    @Override
    public void printMessage(String message)
    {
        println(" [!]=> "+message);
    }

    @Override
    public void printRelation(Relation relation)
    {
        println("  [#]=> "+relation.toString());
    }
    
    @Override
    public void printAttributeAdded(Attribute a)
    {
        println("[ADDED] => Attribute " + a.getName());
    }

    @Override
    public void printAttributeAdded(Set<Attribute> a)
    {
        a.stream().forEach(this::printAttributeAdded);
    }

    @Override
    public void printFunctionDependencyAdded(FunctionalDependency fd)
    {
        println("[ADDED] => FD " + fd);

    }

    @Override
    public void printAll(Set<Attribute> attributes, Set<FunctionalDependency> functionalDependencies)
    {
        println("--- ATTRIBUTES ---");
        attributes.stream().map(a -> a.toString()).forEach(this::println);
        println("---");
        println("");
        println("--- FUNCTIONNALS DEPENDENCIES ---");
        functionalDependencies.stream().map(fd -> fd.toString()).forEach(this::println);
        println("---");
    }

    @Override
    public void printComputation(Set<Attribute> attributes)
    {
        println("--- COMPUTED ATTRIBUTES ---");
        attributes.stream().map(a -> a.toString()).forEach(this::println);
        println("---");
    }

    @Override
    public void help()
    {
        println("\t==== AVAILABLE COMMANDS ====");
        println(" !exit           | Exit the program.");
        println(" !help           | Display the help.");
        println(" !print          | Display the attributes and functional dependencies sets.");
        println(" !clear          | Clear the memory and drop any existent attribute ,relation or functional dependency.");
        println(" !compute        | Display the table that contain all attributes , closure , non-trivial complement, "
                + "key, and superKey.");
        println(" !fd:X->Y        | Create a functional dependency X->Y or compose many attributes");
        println(" !R:A;B;C        | Create a relation R(A,B,C).");
        println(" !superkey:Attr  | Check if this attribute is superkey.");
        println(" !key:Attr       | Check if this attribute is key.");
        println(" !isbcnf         | Check if the relation is in BCNF");
        println(" !decompose      | Decompose the current relation in to BCNF");

    }

    @Override
    public void error(String message)
    {
    	System.err.println(" [ERROR] => " + message);
    }

    @Override
    public void printTable(List<Attribute> dfnt ,String relation)
    {
    	if(!dfnt.isEmpty()) {	
	    	printHeaders("COMPUTED TABLE");
	    	println("\t"+relation);
	    	printNewLine();
	    	
	    	print("");
	    	for(int i=0; i<3*LENGTH+29; i++) {
	        	System.out.print("=");
	        }
	    	println("");
	    	
	    	println(
	                " " +reformat("ATTRIBUTES",0)+
	                " | "+reformat("CLOSURES",0)+
	                " | "+reformat( "CNT",0)+
	                " | "+ reformat( "SUPER KEYS",11)+
	                " | "+reformat("KEYS",5)
	        );
	    	
	        print("");
	        for(int i=0; i<3*LENGTH+29; i++) {
	        	System.out.print("=");
	        }
	        
	        System.out.println();
	        
	        for (Attribute att : dfnt) {
	            println(" "+reformat(att.toString(),0)+" | "+reformat(att.getCloture(),0)
	                    +" | "+reformat(printCNT(att.getCnt()),0)+" | "+ reformat(att.isSuperKey()?"X":"",11) +" | "+(att.isKey()?"X":""));
	        }
	        
	        print("");
	    	for(int i=0; i<3*LENGTH+29; i++) {
	        	System.out.print("=");
	        }
	    	println("");
	    	
    	}
    }

    private String printCNT(FunctionalDependency cnt)
    {
        if(cnt== null) {
            return " ---- ";
        }
        return cnt.toString();
    }

    @Override
    public void notifyLog(int i,String input)
    {
        if(i==1) println("FD(" + input + ") => ADDED");
        if(i==2) println("R(" + input + ") => ADDED");
    }

    private String reformat(String string, int l)
    {
    	string = string==null?"":string;
    	
    	int length;
    	int max;
    	
    	length = string.length();
    	
        if (l == 0) max = LENGTH;
        else max = l;
        
        for (int i = length; i < max; i++) string += " ";
        
        return string;
    }

	@Override
	public void printDecomposition(FunctionalDependency cnt, Relation relation1, Relation relation2)
	{
		println(" First CNT that viol BCNF is : "+ printCNT(cnt));
		println(" We can decompose it as following : ");
		println(" [1] => " + relation1.toString());
		println(" [2] => " + relation2.toString());
	}
	
	@Override
	public void printHeaders(String message)
	{
		printNewLine();
		printNewLine();
    	println( "\t ==== "+message.toUpperCase()+" ====");
	}
    
    
   
}
