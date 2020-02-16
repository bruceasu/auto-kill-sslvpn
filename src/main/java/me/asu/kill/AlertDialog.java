package me.asu.kill;

import static javax.swing.JOptionPane.CLOSED_OPTION;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Created by gw-meet1-0 on 2020/2/6.
 */
public class AlertDialog
{

	final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
	JOptionPane op;
	JDialog     dialog;
	Object[] options = {"中止"};
	String   message = "臣尝闻主公遣使欲刺 SSLVPN，卦不祥，请止之。";

	Runnable stopCallback;
	Runnable timeoutCallback;
	long     timeoutInSec;

	public AlertDialog(Runnable stopCallback, Runnable timeoutCallback, long timeoutInSec)
	{
		this.stopCallback = stopCallback;
		this.timeoutCallback = timeoutCallback;
		if (timeoutInSec < 1) {
			this.timeoutInSec = 30;
		} else {
			this.timeoutInSec = timeoutInSec;
		}

		init();
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				System.out.println("shutdown executors");
				scheduledExecutorService.shutdownNow();
			}

		});
	}

	private void init()
	{

		op = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_OPTION, null,
		                     options, options[0]);
		op.setInitialValue(options[0]);
		op.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());
		dialog = op.createDialog("十万火急军报");
		op.selectInitialValue();
		dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		//		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setAlwaysOnTop(true);
		//		dialog.setUndecorated(true);
		//		dialog.setOpacity(0.9f);
	}

	public void show()
	{

		ScheduledFuture f = scheduledExecutorService.schedule(new Runnable()
		{
			@Override
			public void run()
			{

				op.setValue(-1);
				//				ComponentEvent e = new ComponentEvent(dialog,
				//				                                      ComponentEvent.COMPONENT_HIDDEN);
				//				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(e);
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						dialog.setVisible(false);
					}
				});
			}
		}, 15, TimeUnit.SECONDS);
		dialog.setVisible(true);
		int selectedValue = getValue();
		f.cancel(true);
		System.err.println("selectedValue = " + selectedValue);

		if (selectedValue == 0 && stopCallback != null) {
			stopCallback.run();
		} else if (timeoutCallback != null) {
			timeoutCallback.run();
		}
	}

	private int getValue()
	{
		Object selectedValue = op.getValue();
		if (selectedValue == null) {
			return CLOSED_OPTION;
		}
		if (options == null) {
			if (selectedValue instanceof Integer) {
				return ((Integer) selectedValue).intValue();
			}
			return CLOSED_OPTION;
		}
		for (int counter = 0, maxCounter = options.length; counter < maxCounter; counter++) {
			if (options[counter].equals(selectedValue)) {
				return counter;
			}
		}
		return CLOSED_OPTION;
	}

}
