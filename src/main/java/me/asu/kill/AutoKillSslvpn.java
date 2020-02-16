package me.asu.kill;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;


public class AutoKillSslvpn
{

	private static final GlobalInputDeviceAtiveMonitor inputMonitor;

	static {
		inputMonitor = new GlobalInputDeviceAtiveMonitor();

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			ex.printStackTrace();
		}

		GlobalScreen.addNativeKeyListener(inputMonitor);
		GlobalScreen.addNativeMouseWheelListener(inputMonitor);
		GlobalScreen.addNativeMouseListener(inputMonitor);
		GlobalScreen.addNativeMouseMotionListener(inputMonitor);

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				try {
					if (inputMonitor != null) {
						GlobalScreen.registerNativeHook();
					}
				} catch (NativeHookException ex) {
					System.err.println("There was a problem registering the native hook.");
					ex.printStackTrace();
				}
			}
		});
	}


	public static void main(String[] args)
	{
		OptParser parser = new OptParser();
		parser.parse(args);
		long noHumanTimeoutInSec = parser.getNoHumanTimeoutInSec();
		long msgTimeoutInSec     = parser.getMsgTimeoutInSec();

		AlertDialog                alertDialog = new AlertDialog(new CancelKill(), new ToKill(),
		                                                         msgTimeoutInSec);
		LastHumanActiveTimeUpdater updater     = new LastHumanActiveTimeUpdater();

		inputMonitor.addObserver(updater);

		initScheduledExecutorService(updater, alertDialog, noHumanTimeoutInSec);

		System.out.println("The sentinel is ready.");
	}

	private static void initScheduledExecutorService(final LastHumanActiveTimeUpdater updater,
	                                                 final AlertDialog alertDialog,
	                                                 final long noHumanTimeoutInSec)
	{
		final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(
				10);
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				scheduledExecutorService.shutdownNow();
			}
		});
		scheduledExecutorService.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{

				boolean isTimeout = updater.isTimout(noHumanTimeoutInSec);

				if (isTimeout) {
					boolean found = ProcessHandler.findRunningProcess("SangforServiceClient.exe");
					//					boolean found = ProcessHandler.findRunningProcess("notepad.exe");
					if (found) {
						alertDialog.show();
					}
				}
			}
		}, 0, 5, TimeUnit.SECONDS);
	}


	static class ToKill implements Runnable
	{

		@Override
		public void run()
		{
			System.out.println("Going to kill program(s)...");
			//			killProgram("notepad.exe");
			killProgram("SangforServiceClient.exe");
			killProgram("SangforCSClient.exe");
			killProgram("SangforPromote.exe");
			killProgram("ECAgent.exe");
			//System.exit(0);
		}

		private static void killProgram(String processName)
		{
			if (ProcessHandler.findRunningProcess(processName)) {
				System.out.printf("Found %s, ", processName);
				boolean b = ProcessHandler.killRunningProcess(processName);
				if (b) {
					System.out.printf("kill %s success.%n", processName);
				} else {
					System.out.printf("kill %s fail.%n", processName);
				}
			}
		}
	}

	static class CancelKill implements Runnable
	{

		@Override
		public void run()
		{
			System.err.println("User cancel to kill the program(s).");
		}
	}

	static class LastHumanActiveTimeUpdater implements Observer
	{

		private volatile long lastHumanActiveTime = System.currentTimeMillis();

		@Override
		public void update(Observable o, Object arg)
		{
			//			LOG.info("update active time: " + arg);
			if (arg instanceof Long) {
				lastHumanActiveTime = (Long) arg;
			}
		}

		public long getLastHumanActiveTime()
		{
			return lastHumanActiveTime;
		}

		public boolean isTimout(long noHumanTimeoutInSec)
		{
			return System.currentTimeMillis() - noHumanTimeoutInSec * 1000 > lastHumanActiveTime;
		}
	}

	private static class OptParser
	{

		private long msgTimeoutInSec     = 30;
		private long noHumanTimeoutInSec = 600;

		public static String help()
		{
			StringBuilder buffer = new StringBuilder();
			buffer.append(AutoKillSslvpn.class.getSimpleName())
			      .append(" [options]\n")
			      .append("\toptions:\n")
			      .append("\t\t--msg-timeout-in-seconds\n")
			      .append("\t\t\t<SECONDS> messagebox wait SECONDS to close and execute\n")
			      .append("\t\t\t          kill action, default is 15 seconds.\n")
			      .append("\t\t--no-human-active-timeout-in-seconds\n")
			      .append("\t\t\t<SECONDS> when human has no active action, like type \n")
			      .append("\t\t\t          and click after SECONDS, will call show \n")
			      .append("\t\t\t          messageboxm, default is 600 sencodes.\n")
			      .append("\t\t--help, -h  print this message. \n");

			return buffer.toString();
		}

		public OptParser parse(String[] args)
		{
			if (args == null) {
				return this;
			}
			for (int i = 0; i < args.length; i++) {
				if ("--msg-timeout-in-seconds".equals(args[i])) {
					if (i + 1 >= args.length) {
						System.err.println(help());
						throw new RuntimeException("Argument error.");
					} else {
						i++;
						msgTimeoutInSec = Long.parseLong(args[i]);
					}
				} else if ("--no-human-active-timeout-in-seconds".equals(args[i])) {
					if (i + 1 >= args.length) {
						System.err.println(help());
						throw new RuntimeException("Argument error.");
					} else {
						i++;
						noHumanTimeoutInSec = Long.parseLong(args[i]);
					}
				} else if ("--help".equals(args[i]) || "-h".equals(args[i])) {
					System.err.println(help());
					System.exit(1);
				}

			}

			return this;
		}

		public long getMsgTimeoutInSec()
		{
			return msgTimeoutInSec;
		}

		public long getNoHumanTimeoutInSec()
		{
			return noHumanTimeoutInSec;
		}
	}
}



