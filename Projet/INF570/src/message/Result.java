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
	
	/**
	 * constructeur pour une donnée de type Result. Ici on précise les metadata.
	 * @param fileIndex
	 * @param fileSize
	 * @param sharedFileName
	 * @param optionalResultData -metadata
	 */
	public Result(long fileIndex, long fileSize,String sharedFileName,short[] optionalResultData) {
		_optionalResultData = optionalResultData;
		this.fileIndex = fileIndex;
		this.fileSize = fileSize;
		this.sharedFileName = sharedFileName;
		
		this._fileIndex = Message.tabFromLong(fileIndex);
		this._fileSize = Message.tabFromLong(fileSize);
		this._sharedFileName = Message.tabFromString(sharedFileName);
	}
	
	/**
	 * constructeur pour une donnée de type Result sans metadata.
	 * @param fileIndex
	 * @param fileSize
	 * @param sharedFileName
	 */
	public Result(long fileIndex, long fileSize,String sharedFileName) {
		_optionalResultData = new short[0];
		this.fileIndex = fileIndex;
		this.fileSize = fileSize;
		this.sharedFileName = sharedFileName;
		
		this._fileIndex = Message.tabFromLong(fileIndex);
		this._fileSize = Message.tabFromLong(fileSize);
		this._sharedFileName = Message.tabFromString(sharedFileName);
	}
	

	protected Result(short[] fileIndex, short[] fileSize, short[] sharedFileName,short[] optionalResultData) {
		_fileIndex = fileIndex;
		_fileSize = fileSize;
		_sharedFileName = sharedFileName;
		_optionalResultData = optionalResultData;
		
		this.fileIndex = Message.longFromTab(_fileIndex);
		this.fileSize = Message.longFromTab(_fileSize);
		this.sharedFileName = Message.stringFromTab(_sharedFileName);
	}
	
	protected short[] toShortTab(){
		short[] res  = new short[8+2+_sharedFileName.length+_optionalResultData.length];
		for (int i = 0; i < 4; i++) {
			res[i] = _fileIndex[i];
		}
		for (int i = 0; i < 4; i++) {
			res[i+4] = _fileSize[i];
		}
		for (int i = 0; i < _sharedFileName.length; i++) {
			res[i+4+4] = _sharedFileName[i];
		}
		res[4+4+_sharedFileName.length] = 0;
		for (int i = 0; i < _optionalResultData.length; i++) {
			res[i+4+4+_sharedFileName.length+1] = _optionalResultData[i];
		}
		res[4+4+_sharedFileName.length+ _optionalResultData.length+1] = 0;
		return res;
	}
	
	protected int length(){
		return (8+2+_sharedFileName.length+_optionalResultData.length);
	}
	
	public String toString(){
		return "{index:["+getFileIndex()+"] size:["+getFileSize()+"kB] name:["+getSharedFileName()+"] optionalData:["+ Message.stringOfTab(getOptionalResultData())+"]}";
	}
}
