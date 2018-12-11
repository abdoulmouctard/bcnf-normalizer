package core;

import java.util.List;
import java.util.Set;

import work.Relation;

/**
 * core.CoreLogger is used to display messages and results.
 *
 */
public interface CoreLogger {

    public void printMessage(String message);
    public void printRelation(Relation relation);
    public void printAttributeAdded(Attribute a);
    public void printAttributeAdded(Set<Attribute> a);
    public void printFunctionDependencyAdded(FunctionalDependency fd);
    public void printAll(Set<Attribute> attributes, Set<FunctionalDependency> functionalDependencies);
    public void printComputation(Set<Attribute> attributes);
    public void printNewLine();
    public void help();
    public void error(String message);
    public void printTable(List<Attribute> dfnt,String relation);
    public void notifyLog(int i,String s);
	public void printDecomposition(FunctionalDependency cnt, Relation relation1, Relation relation2);
	public void printHeaders(String message);

}
