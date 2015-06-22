package baron.jakub.model;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.SwingUtilities;

public class MouseCapturer implements MouseListener, MouseMotionListener,
		MouseWheelListener {

	private boolean active;
	private float[] angles;
	private float increment = 0.0001f;
	private float[] positions;
	private int[] pressedAngles;
	private int[] pressedPositions;
	private float zoom;

	public MouseCapturer() {
		pressedAngles = new int[] { 0, 0 };
		angles = new float[] { 0f, 0f, 0f };
		positions = new float[] { -0f, -0f, -0f };
		pressedPositions = new int[] { 0, 0 };
		zoom = 0.003f;
		active = true;

	}

	public float[] getAngles() {
		return angles;
	}

	public float[] getPositions() {
		return positions;
	}

	public float getZoom() {
		return zoom;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			updateAngles(e);
		} else if (SwingUtilities.isRightMouseButton(e)) {
			updatePositions(e);
		} else if (SwingUtilities.isMiddleMouseButton(e)) {

		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			pressedAngles[0] = e.getX();
			pressedAngles[1] = e.getY();
		} else if (SwingUtilities.isRightMouseButton(e)) {
			pressedPositions[0] = e.getX();
			pressedPositions[1] = e.getY();
		} else if (SwingUtilities.isMiddleMouseButton(e)) {

		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		zoom += ((float) notches * increment);
		if (active && zoom < increment)
			zoom = increment;
		// if (notches < 0) {
		// message = "Mouse wheel moved UP " + -notches + " notch(es)";
		// } else {
		// message = "Mouse wheel moved DOWN " + notches + " notch(es)";
		// }
		// System.out.println(message);

	}

	public void setActive(boolean b){
		active = b;
	}

	public void setBasicZoom(float z) {
		this.zoom = z;
	}

	public void setZoomIncreasing(float inc) {
		this.increment = inc;
	}

	private void updateAngles(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		angles[0] += (pressedAngles[0] - x) / 5f;
		angles[1] += (pressedAngles[1] - y) / 5f;
		pressedAngles[0] = x;
		pressedAngles[1] = y;
	}
	private void updatePositions(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		positions[0] -= (pressedPositions[0] - x) / 300f;
		positions[1] += (pressedPositions[1] - y) / 300f;
		pressedPositions[0] = x;
		pressedPositions[1] = y;

	}

}
