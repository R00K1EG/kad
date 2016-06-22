package app;

import net.IIEPackage;

public class AppPackageProcess {

	private IIEPackage app = null;
	
	public AppPackageProcess(){
		
	}

	/**
	 * must bu done;
	 * @return
	 */
	public int process(){
		return 0;
	}
	
	
	public IIEPackage getApp() {
		return app;
	}

	public synchronized void setApp(IIEPackage app) {
		this.app = app;
		this.process();
	}
	
	
	
}

