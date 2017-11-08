package org.knime.silhouette;

import org.knime.core.data.NominalValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter2;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnNameSelection;
import org.knime.core.node.defaultnodesettings.DialogComponentFileChooser;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

/**
 * <code>NodeDialog</code> for the "PFA" Node. This is an example node provided
 * by KNIME.com.
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author Mete Can Akar
 */
public class SilhouetteNodeNodeDialog extends DefaultNodeSettingsPane {

	/**
	 * New pane for configuring MyExampleNode node dialog. This is just a
	 * suggestion to demonstrate possible default dialog components.
	 */

	private SettingsModelString clusterColStringModel;
	private SettingsModelColumnFilter2 dataColumnsFilter2SettingsModel;

	protected SilhouetteNodeNodeDialog() {
		super();

		clusterColStringModel = SilhouetteNodeConfiguration.createClusterColModel();
		dataColumnsFilter2SettingsModel = SilhouetteNodeConfiguration.createDataColumnsModel();

		addDialogComponent(
				new DialogComponentColumnNameSelection(clusterColStringModel, "Please choose the cluster column", 0, 
						NominalValue.class));

		// input port index 0 because we only have 1 input port.
		addDialogComponent(new DialogComponentColumnFilter2(dataColumnsFilter2SettingsModel, 0));

		// addDialogComponent(new DialogComponentNumber(
		// new SettingsModelIntegerBounded(
		// MyExampleNodeNodeModel.CFGKEY_COUNT,
		// MyExampleNodeNodeModel.DEFAULT_COUNT,
		// Integer.MIN_VALUE, Integer.MAX_VALUE),
		// "Counter:", /*step*/ 1, /*componentwidth*/ 5));

	}

}
