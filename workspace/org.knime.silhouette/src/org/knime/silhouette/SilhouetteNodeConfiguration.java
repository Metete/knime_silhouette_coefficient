package org.knime.silhouette;

import org.knime.core.data.DoubleValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

public class SilhouetteNodeConfiguration {

	static final String CLUSTER_COL_NAME = "ClusterColName";
	static final String DATA_COLUMNS = "DataColumns";

	/**
	 * member variable for cluster column.
	 */
	private SettingsModelString m_clusterCol = createClusterColModel();

	/**
	 * member variable for data columns.
	 */
	private SettingsModelColumnFilter2 m_dataCols = createDataColumnsModel();

	public SettingsModelString get_clusterCol() {
		return m_clusterCol;
	}

	public SettingsModelColumnFilter2 get_dataCols() {
		return m_dataCols;
	}

	/**
	 * Creates a SettingsModelString object for the cluster column model.
	 * 
	 * @return A SettingsStringModel for cluster column model.
	 */
	public static SettingsModelString createClusterColModel() {
		return new SettingsModelString(CLUSTER_COL_NAME, null);
	}

	/**
	 * Creates a SettingsModelColumnFilter2 object with the allowed type of
	 * DoubleValue.
	 * 
	 * @return A Json File SettingsStringModel
	 */
	public static SettingsModelColumnFilter2 createDataColumnsModel() {
		return new SettingsModelColumnFilter2(DATA_COLUMNS, DoubleValue.class);
	}

	/**
	 * Wrapper function for saveSettings
	 * 
	 * @param settings
	 *            settings
	 */
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		// save user settings to the config object.
		// file name will be string
		m_clusterCol.saveSettingsTo(settings);
		m_dataCols.saveSettingsTo(settings);

	}

	/**
	 * Wrapper function for loadSettings
	 * 
	 * @param settings
	 *            settings
	 * @throws InvalidSettingsException
	 */
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		// load (valid) settings from the config object.
		// It can be safely assumed that the settings are valided by the
		// method below.

		m_clusterCol.loadSettingsFrom(settings);
		m_dataCols.loadSettingsFrom(settings);

	}
}
