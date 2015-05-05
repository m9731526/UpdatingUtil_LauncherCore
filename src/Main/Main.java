/*
 
 */

package Main;

import java.io.*;
import java.net.*;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class Main{
	static float[] verCmp = { 0, 0 };
	static String patchNoS;

	private static void init() throws MalformedURLException {
		Downloader.versionUrl = new URL(
				"https://dl.dropboxusercontent.com/u/132679455/Client/Version");
		Downloader.verFN = "Version";
		Downloader.FileListUrl = new URL(
				"https://dl.dropboxusercontent.com/u/132679455/Client/FileList");
		Downloader.PatchLogUrl = new URL(
				"https://dl.dropboxusercontent.com/u/132679455/Client/patchlog.txt");
		Downloader.dropboxUrlStr = "https://dl.dropboxusercontent.com/u/132679455/Client";

	}
	
	public static void userSet() throws IOException{		
		
		if(new File("Launch.vbe").exists())	return;
		
		String Pname = JOptionPane.showInputDialog("請輸入你的角色名稱：", "英文或數字");
		
		int Ram;
		switch(System.getProperty("sun.arch.data.model")){
		case "32":
			Ram = 1024;	break;
		case "64":
			Ram = 2048; break;
		default:
			Ram = 1024; break;
		}
		
		if(Pname != null && Pname.length() > 0){
			 while(!Pname.matches("\\w+")){
					Pname = JOptionPane.showInputDialog("請輸入你的角色名稱：", "英文或數字");
			 }
		FileWriter fw = new FileWriter("Launch.vbe");
		fw.write(String.format("set a=wscript.createobject(\"wscript.shell\")\na.run(\"MCLauncherBN.exe 1.7.10-Forge10.13.2.1307-1.7.10*%s*%d*-Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true\")", Pname, Ram));
		fw.close();
		Runtime.getRuntime().exec("wscript.exe Launch.vbe");
		}
		
	}

	public static void main(String[] args) throws IOException {
		init();
		new Frameset();
		try {
			if(checkconnection() == 0) System.exit(1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			versioncheck();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userSet();
		
		return;
	}

	public static void versioncheck() throws IOException, InterruptedException {
		File lver = new File("Version");
		if (lver.exists() == true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					Downloader.versionUrl.openStream()));
			String str = null;
			String[] tstr = new String[5];

			if ((str = br.readLine()) != null) {
				str = str.replace(".", " ");
				tstr = str.split("\\s");
			}

			String Vstr = tstr[0];
			String MVersion = tstr[1], SVersion = tstr[2];
			Frameset.Text_message.append("\n更新代號：" + Vstr + "\n線上版本："
					+ MVersion + "." + SVersion);
			verCmp[0] = Float.parseFloat((MVersion + SVersion));

			br.close();

			FileReader localVer = new FileReader("Version");
			br = new BufferedReader(localVer);

			if ((str = br.readLine()) != null) {
				str = str.replace(".", " ");
				tstr = str.split("\\s");
			}

			Vstr = tstr[0];
			MVersion = tstr[1];
			SVersion = tstr[2];
			patchNoS = tstr[3];
			Frameset.Text_message.append("\n安裝版本：" + MVersion + "." + SVersion);
			verCmp[1] = Float.parseFloat((MVersion + SVersion));

			br.close();

			if (verCmp[0] > verCmp[1]) {
				Frameset.Text_message.append("\n檢測到更新檔案，執行更新。");
				// Frameset.Btn_start.setEnabled(true);
				Thread.sleep(1000);
				Downloader.updatingQueue();
				return;
			} else if (verCmp[0] == verCmp[1]) {
				Frameset.Text_message.append("\n本地版本不需要進行更新，即將關閉本程式。");
				Thread.sleep(5000);
			} else if (verCmp[0] < verCmp[1]) {
				Frameset.Text_message.append("\n版本資訊錯誤，或者是線上版本已重設，請清空資料夾後重開更新程式。");
				FileManager.delete("Version");
				Thread.sleep(1000);
				Downloader.fullupdate();
				return;
			}
		} else if (lver.exists() == false) {
			Frameset.Text_message.append("\n未發現版本資訊，判定為遊戲未安裝，即將自動安裝。");
			Thread.sleep(1000);
			Downloader.fullupdate();
			return;
		}

	}

	public static int checkconnection() throws IOException,
			InterruptedException {
		Process p1 = java.lang.Runtime.getRuntime().exec(
				"ping -n 1 www.dropbox.com");
		int returnVal = p1.waitFor();
		boolean reachable = (returnVal == 0);
		if (reachable == false) {
			Frameset.Text_message.setText("無法存取Dropbox伺服器。");
			Frameset.Text_message.append("\n請確認網路連線，程式即將自動關閉。");
			Thread.sleep(5000);

			return 0;

		} else if (reachable == true) {
			Frameset.Text_message.setText("已連上Dropbox伺服器。");
			return 1;
		} else {
			Frameset.Text_message.setText("連線狀況未知。");
			Frameset.Text_message.append("\n請確認網路連線，程式即將自動關閉。");
			Thread.sleep(5000);
			return 0;
		}
	}

}
