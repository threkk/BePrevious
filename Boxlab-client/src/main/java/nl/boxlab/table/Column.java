package nl.boxlab.table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.swing.table.TableCellRenderer;

/**
 * @author nilsdijk
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column
{

	/**
	 * The key is used to store settings for columns when persisting the view to
	 * later restore it and to fetch localized titles for the column
	 * 
	 * @return
	 */
	String key();

	/**
	 * The maximum width of the column, when negative it is assumed there is no
	 * maximum width for the column
	 * 
	 * @return
	 */
	int maxWidth() default -1;

	/**
	 * The minimal width of the column, when negative it is assumed there is no
	 * minimal width of the column
	 * 
	 * @return
	 */
	int minWidth() default 50;

	/**
	 * The default order of the columns
	 * 
	 * @return
	 */
	int order() default Integer.MAX_VALUE;

	/**
	 * The default width of the column
	 * 
	 * @return
	 */
	int preferredWidth() default -1;

	/**
	 * The renderer used to display the value in the column
	 * 
	 * @return
	 */
	Class<? extends TableCellRenderer> renderer() default TableCellRenderer.class;

	/**
	 * Default title of the column when no localized title has been provided for
	 * the key
	 * 
	 * @return
	 */
	String title();

	/**
	 * Used to indicate the column is visible by default. Columns can be made
	 * visible and this is persisted when the settings of the view are persisted
	 * 
	 * @return
	 */
	boolean visible() default true;
}
