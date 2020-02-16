package me.asu.kill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessHandler
{

	/**
	 * @author coldanimal; ProcessHandler windowns version.
	 */
	public static boolean findRunningProcess(String processName)
	{
		String platform = System.getProperty("os.name");
		if (platform.contains("Windows")) {
			return findRunningWindowsProcess(processName);
		} else if (platform.contains("Linux")) {
			return findRunningLinuxProcess(processName);
		} else {
			throw new RuntimeException("Unknown platform " + platform);
		}
	}

	private static boolean findRunningLinuxProcess(String processName)
	{
		return false;
	}

	private static boolean findRunningWindowsProcess(String processName)
	{
		BufferedReader bufferedReader = null;
		Process        proc           = null;
		try {
			proc = Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq " + processName + "\"");
			bufferedReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String  line;
			boolean flag = false;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(processName)) {
					flag = true;
				}
			}
			return flag;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (Exception ex) {
				}
			}
			if (proc != null) {
				try {
					proc.destroy();
				} catch (Exception ex) {
				}
			}
		}
	}

	public static boolean killRunningProcess(String processName)
	{
		String platform = System.getProperty("os.name");
		if (platform.contains("Windows")) {
			return killRunningWindowsProcess(processName);
		} else if (platform.contains("Linux")) {
			return false;
		}
		throw new RuntimeException("Unkown platform " + platform);
	}

	private static boolean killRunningWindowsProcess(String processName)
	{
		try {

			//			int i = kill(processName);
			//			if (i == 0) {
			//				return true;
			//			}

			// try force
			int i = forceKill(processName);

			if (i == 0) {
				return true;
			}
			// try as admin
			i = killAsAdmin(processName);
			return i == 0;


		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private static int kill(String processName) throws IOException, InterruptedException
	{
		Process exec = Runtime.getRuntime().exec("taskkill /T /IM " + processName);
		return exec.waitFor();
	}

	private static int killAsAdmin(String processName) throws IOException, InterruptedException
	{
		Process exec;
		int     i;
		exec = Runtime.getRuntime().exec("nircmd.exe elevate taskkill  /F /T /IM " + processName);
		i = exec.waitFor();
		return i;
	}

	private static int forceKill(String processName) throws IOException, InterruptedException
	{
		Process exec;
		int     i;
		exec = Runtime.getRuntime().exec("taskkill  /F /T /IM " + processName);
		i = exec.waitFor();
		return i;
	}

	public static void main(String[] args)
	{
		//		if (ProcessHandler.findRunningProcess("notepad.exe")) {
		//			ProcessHandler.killRunningProcess("notepad.exe");
		//		}

		if (ProcessHandler.findRunningProcess("ECAgent.exe")) {
			boolean b = ProcessHandler.killRunningProcess("ECAgent.exe");
			if (b) {
				System.out.println("kill success.");
			} else {
				System.out.println("kill fail");
			}
		}

	}

}