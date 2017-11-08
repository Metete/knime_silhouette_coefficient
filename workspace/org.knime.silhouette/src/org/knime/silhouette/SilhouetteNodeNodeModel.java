package org.knime.silhouette;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.NominalValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.StringValue;
import org.knime.core.data.append.AppendedColumnRow;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnFilter2;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.filter.NameFilterConfiguration.FilterResult;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * This is the model implementation of MyExampleNode. This is an example node
 * provided by KNIME.com.
 *
 * @author KNIME.com
 */
public class SilhouetteNodeNodeModel extends NodeModel {

	// the logger instance
	private static final NodeLogger logger = NodeLogger.getLogger(SilhouetteNodeNodeModel.class);

	private long m_rowSizeOfDocument = 0;

	// create an instance of configuration
	SilhouetteNodeConfiguration silhouetteNodeConfiguration = new SilhouetteNodeConfiguration();

	/**
	 * member variable for cluster column.
	 */
	private final SettingsModelString m_clusterCol = silhouetteNodeConfiguration.get_clusterCol();

	/**
	 * member variable for data columns.
	 */
	private SettingsModelColumnFilter2 m_dataCols = silhouetteNodeConfiguration.get_dataCols();

	/**
	 * Constructor for the node model.
	 */
	protected SilhouetteNodeNodeModel() {

		// 1 input 1 output.
		super(1, 1);
	}

	private String getClusterValue(DataRow row, int index) {

		// TODO METE LOOK FOR KNIME DATA TYPES
		return ((StringValue) row.getCell(index)).getStringValue();
	}

	private double[] getDataValues(DataRow row, int[] colIndices) {
		double[] data = new double[colIndices.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = ((DoubleValue) row.getCell(colIndices[i])).getDoubleValue();
		}
		return data;
	}

	/**
	 * Calculates the euclideanDistance
	 * 
	 * @param myRow
	 * @param dataRow2
	 * @return
	 */
	private double euclideanDistance(double[] myRow, double[] dataRow2) {
		// calculate the euclidean distance between double arrays
		double result = 0;
		for (int i = 0; i < myRow.length; i++) {

			double firstVal = myRow[i];
			double secondVal = dataRow2[i];
			result += Math.pow((firstVal - secondVal), 2);
		}
		return Math.sqrt(result);
	}

	/**
	 * Adds silhouette to the table
	 * 
	 * @param output
	 *            table to be updated
	 * @param silhouette
	 *            value to be added
	 */
	private void addSilhouetteRow(BufferedDataContainer output, DataRow row, double silhouette) {
		output.addRowToTable(new AppendedColumnRow(row, new DoubleCell(silhouette)));
	}

	private DataTableSpec createOutputSpec(DataTableSpec inputSpec) {
		DataTableSpec appendedSpec = new DataTableSpec(
				new DataColumnSpecCreator("silhouetteCoefficient", DoubleCell.TYPE).createSpec());
		return new DataTableSpec(inputSpec, appendedSpec);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
			throws Exception {

		// TODO do something here
		logger.info("Node Model Stub... this is not yet implemented !");

		// member var.
		FilterResult res = m_dataCols.applyTo(inData[0].getDataTableSpec());
		int[] usedColumns = new int[res.getIncludes().length];

		for (int i = 0; i < usedColumns.length; i++) {
			// Finds the columns with the specified name in the TableSpec and
			// adds their indexes to the usedColumns array
			usedColumns[i] = inData[0].getDataTableSpec().findColumnIndex(res.getIncludes()[i]);
		}

		// set row size of the document
		m_rowSizeOfDocument = inData[0].size();
		int clusterColIndex = inData[0].getDataTableSpec().findColumnIndex(m_clusterCol.getStringValue());

		HashMap<String, BufferedDataContainer> clusters = new HashMap<>();

		// her bir row için
		for (DataRow dataRow : inData[0]) {

			// clusterColIndex is where cluster column is
			String cluster = getClusterValue(dataRow, clusterColIndex);
			// if the Key cluster absents(yoksa) in clusters HashMap then call
			// the exec.createDataContainer(inData[0].getSpec())) mapping
			// function (CREATE A NEW BUFFEREDDATACONTAINER)
			// if the Key cluster presents(varsa) in clusters HashMap then don't
			// call the exec.createDataContainer(inData[0].getSpec())) mapping
			// function but assign the already existing value for the key
			// cluster
			BufferedDataContainer dc = clusters.computeIfAbsent(cluster,
					(key) -> exec.createDataContainer(inData[0].getSpec()));

			// Then add to that BufferedDataContainer according to its key the
			// dataRow.
			dc.addRowToTable(dataRow);
		}

		ArrayList<BufferedDataTable> lstBufferedDataTables = new ArrayList<>();

		for (BufferedDataContainer bufferedDataContainer : clusters.values()) {
			bufferedDataContainer.close();
			lstBufferedDataTables.add((BufferedDataTable) bufferedDataContainer.getTable());
		}

		BufferedDataContainer output = exec.createDataContainer(createOutputSpec(inData[0].getDataTableSpec()));

		// for every cluster
		for (BufferedDataTable bufferedDataTable : lstBufferedDataTables) {

			// currently used list of buffered data tables and will be modified
			// in the future.
			// ArrayList<BufferedDataTable> lstCurrentBufferedDataTables =
			// lstBufferedDataTables;

			// for every row within the same(inner) cluster
			for (DataRow myRow : bufferedDataTable) {
				double[] myRowValues = getDataValues(myRow, usedColumns);
				double innerResult = 0;

				// In the same(inner) cluster(table)
				for (DataRow innerRow : bufferedDataTable) {
					double[] innerRowValues = getDataValues(innerRow, usedColumns);
					innerResult += euclideanDistance(myRowValues, innerRowValues);
				}
				// Calculate the avg dissimilarity within the same cluster.
				double innerAVG = innerResult / bufferedDataTable.size();
				// remove the same(inner) table(cluster) from the
				// lstCurrentBufferedDataTables
				// lstCurrentBufferedDataTables.remove(lstBufferedDataTables.indexOf(bufferedDataTable));

				double minOuterAVG = Double.POSITIVE_INFINITY;

				// iterate through outer tables(clusters)
				for (BufferedDataTable currentTable : lstBufferedDataTables) {
					if (currentTable == bufferedDataTable) {
						continue;
					}
					double outerRowResult = 0;
					for (DataRow outerRow : currentTable) {
						double[] outerRowValues = getDataValues(outerRow, usedColumns);
						outerRowResult += euclideanDistance(myRowValues, outerRowValues);
					}
					double outerTableAVG = outerRowResult / currentTable.size();
					if (outerTableAVG < minOuterAVG) {
						minOuterAVG = outerTableAVG;
					}
				}
				// Calculate silhouette
				double silhouette = (minOuterAVG - innerAVG) / Double.max(minOuterAVG, innerAVG);
				addSilhouetteRow(output, myRow, silhouette);
			}
		}

		// exec.createDataContainer(spec) yukarıda çağır dedi.

		// Close every data container with close()
		// Get the table using getTable()

		output.close();
		return new BufferedDataTable[] { (BufferedDataTable) output.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset() {
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {

		// TODO: check if user settings are available, fit to the incoming
		// table structure, and the incoming types are feasible for the node
		// to execute. If the node can execute in its current state return
		// the spec of its output data table(s) (if you can, otherwise an array
		// with null elements), or throw an exception with a useful user message

		if (m_clusterCol.getStringValue() == null) {
			String matchingCol = null;
			for (String col : inSpecs[0].getColumnNames()) {
				if (inSpecs[0].getColumnSpec(col).getType().isCompatible(NominalValue.class)) {
					matchingCol = col;
					break;
				}
			}
			if (matchingCol != null) {
				m_clusterCol.setStringValue(matchingCol);
				setWarningMessage("No cluster column selected, using column " + matchingCol);
			} else {
				throw new InvalidSettingsException("No string column found for the cluster column.");
			}
		}
		return new DataTableSpec[] { createOutputSpec(inSpecs[0]) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings) {

		silhouetteNodeConfiguration.saveSettingsTo(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {

		silhouetteNodeConfiguration.loadValidatedSettingsFrom(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {

		// TODO check if the settings could be applied to our model
		// e.g. if the count is in a certain range (which is ensured by the
		// SettingsModel).
		// Do not actually set any values of any member variables.

		m_clusterCol.validateSettings(settings);
		m_dataCols.validateSettings(settings);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO load internal data.
		// Everything handed to output ports is loaded automatically (data
		// returned by the execute method, models loaded in loadModelContent,
		// and user settings set through loadSettingsFrom - is all taken care
		// of). Load here only the other internals that need to be restored
		// (e.g. data used by the views).

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir, final ExecutionMonitor exec)
			throws IOException, CanceledExecutionException {

		// TODO save internal models.
		// Everything written to output ports is saved automatically (data
		// returned by the execute method, models saved in the saveModelContent,
		// and user settings saved through saveSettingsTo - is all taken care
		// of). Save here only the other internals that need to be preserved
		// (e.g. data used by the views).

	}

}
