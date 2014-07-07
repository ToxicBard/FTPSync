import java.io.IOException;
import java.net.SocketException;
import org.apache.commons.net.ftp.*;
import java.io.File;

public class FTPSync {
	
	private static FTPClient mFTPClient;
	private static final String mBaseLocalDirectory = "/media/patrick/2TB_Media/Media Library/Music";
	private static final String mBaseRemoteDirectory = "sdcard/ParagonNTFS/Ouya/Media/Music";

	/**
	 * @param args
	 * @throws IOException 
	 */
	//Source Control Test
	public static void main(String[] args) throws IOException {
		String hostAddress = "192.168.1.149";
		int hostPort = 2124;
		
		String userName = "ouya";
		String userPass = "ouya";
		
		mFTPClient = new FTPClient();
	
		mFTPClient.connect(hostAddress, hostPort);
		mFTPClient.login(userName, userPass);
		
		traverseRemoteDirectory(mBaseRemoteDirectory);
		
	}
	
	/* Recursive directory traversal.  Given a particular directory path this
	 * function goes through each FTPFile.  If the FTPFile is a folder, then we
	 * pass that directory path in for another iteration of traverseDirectory.
	*/
	private static void traverseRemoteDirectory(String remoteDirectoryPath) throws IOException{
		FTPFile[] directoryFolders;
		String ftpFilePath;

		directoryFolders = mFTPClient.listFiles(remoteDirectoryPath);
		
		for(FTPFile f : directoryFolders){
			ftpFilePath = remoteDirectoryPath + "/" + f.getName();
			
			if(f.isDirectory() == true){
				traverseRemoteDirectory(ftpFilePath);
			}
			/*
			 * For now this just displays the filename.  In the
			 * future it should compare the file to the mirrored
			 * filepath on the compared local folder, and delete
			 * the remote file if the local mirror file doesn't exist
			 * or is a different file.
			 */
			else if(f.isFile() == true){
				System.out.println(remoteFileMatchesLocally(ftpFilePath));
			}
		}
		
		//TODO Delete folder if it's empty at this point
	}
	
	private static boolean remoteFileMatchesLocally(String remoteFilePath) {
		String relativeFilePath = remoteFilePath.substring(mBaseRemoteDirectory.length());
		String localFilePath = mBaseLocalDirectory + relativeFilePath;
		File localCheckFile = new File(localFilePath);
		boolean localFileExists = localCheckFile.exists();
		
		//TODO Add comparison on modified date / size / etc
		if(localFileExists == true){
			return true;
		}
		
		return false;
	}

}
