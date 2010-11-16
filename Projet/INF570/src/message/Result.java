package message;

public class Result {
/**
 * Cette structure formate les résultats d'une recherche.
 * 
 */

	private short[] _fileIndex;
	private short[] _fileSize;
	private short[] _sharedFileName;
	private short[] _optionalResultData;
	
	
	private long fileIndex;
	private long fileSize;
	private String sharedFileName;
	
	
	public short[] getOptionalResultData() {
		return _optionalResultData;
	}
	public long getFileIndex() {
		return fileIndex;
	}
	public long getFileSize() {
		return fileSize;
	}
	public String getSharedFileName() {
		return sharedFileName;
	}
	
	
	public Result(long fileIndex, long fileSize,
			String sharedFileName,short[] optionalResultData) {
		super();
		_optionalResultData = optionalResultData;
		this.fileIndex = fileIndex;
		this.fileSize = fileSize;
		this.sharedFileName = sharedFileName;
		
		//TODO champs bruts
		
	}
	
	protected Result(short[] fileIndex, short[] fileSize, short[] sharedFileName,
			short[] optionalResultData) {
		super();
		_fileIndex = fileIndex;
		_fileSize = fileSize;
		_sharedFileName = sharedFileName;
		_optionalResultData = optionalResultData;
		
		
		//TODO champs sémantiques
	}
	
	
	
	
}
