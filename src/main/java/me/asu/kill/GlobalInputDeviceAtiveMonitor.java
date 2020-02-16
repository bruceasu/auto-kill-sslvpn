package me.asu.kill;

import java.util.Observable;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

/**
 * Created by gw-meet1-0 on 2020/2/9.
 */
public class GlobalInputDeviceAtiveMonitor extends Observable
		implements NativeMouseInputListener, NativeKeyListener, NativeMouseWheelListener
{


	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent)
	{
		update();
	}

	private void update()
	{
		this.setChanged();
		this.notifyObservers(System.currentTimeMillis());
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
	{
		update();
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent)
	{
		update();
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent)
	{
		update();
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeMouseEvent)
	{
		update();
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent)
	{
		update();
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent)
	{
		update();
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent)
	{
		update();
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent)
	{
		update();
	}
}
