package org.insightech.er.editor.model.diagram_contents.element.connection;

import java.util.ArrayList;
import java.util.List;

import org.insightech.er.editor.model.AbstractModel;
import org.insightech.er.editor.model.diagram_contents.element.node.NodeElement;

public abstract class ConnectionElement extends AbstractModel {

	private static final long serialVersionUID = -5418951773059063716L;

	public static final String PROPERTY_CHANGE_CONNECTION = "connection";

	public static final String PROPERTY_CHANGE_BEND_POINT = "bendPoint";

	public static final String PROPERTY_CHANGE_CONNECTION_ATTRIBUTE = "connection_attribute";

	protected NodeElement source;

	protected NodeElement target;

	// �x���h�E�|�C���g�̈ʒu���̃��X�g
	private List<Bendpoint> bendPoints = new ArrayList<Bendpoint>();

	public NodeElement getSource() {
		return source;
	}

	public void setSource(NodeElement source) {
		if (this.source != null) {
			this.source.removeOutgoing(this);
		}

		this.source = source;

		if (this.source != null) {
			this.source.addOutgoing(this);
		}

		this.firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, source);
	}

	public void setSourceAndTarget(NodeElement source, NodeElement target) {
		this.source = source;
		this.target = target;
	}

	public void setTarget(NodeElement target) {
		if (this.target != null) {
			this.target.removeIncoming(this);
		}

		this.target = target;

		if (this.target != null) {
			this.target.addIncoming(this);
		}

		this.firePropertyChange(PROPERTY_CHANGE_CONNECTION, null, source);
	}

	public NodeElement getTarget() {
		return target;
	}

	public void delete() {
		source.removeOutgoing(this);
		target.removeIncoming(this);
	}

	public void connect() {
		if (this.source != null) {
			source.addOutgoing(this);
		}
		if (this.target != null) {
			target.addIncoming(this);
		}
	}

	public void addBendpoint(int index, Bendpoint point) {
		bendPoints.add(index, point);
		firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
	}

	public void setBendpoints(List<Bendpoint> points) {
		bendPoints = points;
		firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
	}

	public List<Bendpoint> getBendpoints() {
		return bendPoints;
	}

	public void removeBendpoint(int index) {
		bendPoints.remove(index);
		firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
	}

	public void replaceBendpoint(int index, Bendpoint point) {
		bendPoints.set(index, point);
		firePropertyChange(PROPERTY_CHANGE_BEND_POINT, null, null);
	}

	public void setParentMove() {
		firePropertyChange(PROPERTY_CHANGE_CONNECTION_ATTRIBUTE, null, null);
	}

	/**
	 * �ڑ��𕡐����܂��B �ڑ����Ɛڑ���̃m�[�h�͂Ƃ��ɁA�������Ɠ����ł��B
	 */
	@Override
	public ConnectionElement clone() {
		ConnectionElement clone = (ConnectionElement) super.clone();

		List<Bendpoint> cloneBendPoints = new ArrayList<Bendpoint>();
		for (Bendpoint bendPoint : bendPoints) {
			cloneBendPoints.add((Bendpoint) bendPoint.clone());
		}

		clone.bendPoints = cloneBendPoints;

		return clone;
	}
}
