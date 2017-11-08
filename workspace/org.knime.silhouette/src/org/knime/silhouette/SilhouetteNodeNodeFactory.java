package org.knime.silhouette;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "MyExampleNode" Node.
 * This is an example node provided by KNIME.com.
 *
 * @author KNIME.com
 */
public class SilhouetteNodeNodeFactory 
        extends NodeFactory<SilhouetteNodeNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SilhouetteNodeNodeModel createNodeModel() {
        return new SilhouetteNodeNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<SilhouetteNodeNodeModel> createNodeView(final int viewIndex,
            final SilhouetteNodeNodeModel nodeModel) {
        return new SilhouetteNodeNodeView(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new SilhouetteNodeNodeDialog();
    }

}

