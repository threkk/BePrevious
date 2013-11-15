package nl.boxlab.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Nils Dijk
 */
public class MethodColumnDefinition extends ColumnDefinition
{

	private final TableViewDefinition view;
	private final Method getter;
	private final Method setter;

	public MethodColumnDefinition(TableViewDefinition view, Column info, Method getter,
			Method setter)
	{
		super(info);

		this.view = view;
		this.getter = getter;
		this.setter = setter;
	}

	/*
	 * (non-Javadoc) @see
	 * nl.thanod.jtableview.TableViewController.ColumnDefinition#getValue
	 * (java.lang.Object)
	 */
	@Override
	public Object getValue(Object o)
	{
		try {
			return this.getter.invoke(view, o);
		} catch (IllegalArgumentException ball) {
			ball.printStackTrace();
		} catch (IllegalAccessException ball) {
			ball.printStackTrace();
		} catch (InvocationTargetException ball) {
			ball.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc) @see
	 * nl.thanod.jtableview.TableViewController.ColumnDefinition#getValueClass
	 * ()
	 */
	@Override
	public Class<?> getValueClass()
	{
		return this.getter.getReturnType();
	}

	/*
	 * (non-Javadoc) @see
	 * nl.thanod.jtableview.TableViewController.ColumnDefinition#isEditable ()
	 */
	@Override
	public boolean isEditable()
	{
		return this.setter != null;
	}

	/*
	 * (non-Javadoc) @see
	 * nl.thanod.jtableview.TableViewController.ColumnDefinition#setValue
	 * (java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setValue(Object dest, Object value)
	{
		try {
			this.setter.invoke(view, dest, value);
		} catch (IllegalArgumentException ball) {
			ball.printStackTrace();
		} catch (IllegalAccessException ball) {
			ball.printStackTrace();
		} catch (InvocationTargetException ball) {
			ball.printStackTrace();
		}
	}
}
