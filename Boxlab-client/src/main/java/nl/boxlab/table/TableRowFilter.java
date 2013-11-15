package nl.boxlab.table;

public interface TableRowFilter<T>
{
	public boolean accept(T row);
}
