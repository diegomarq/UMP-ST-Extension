

import java.io.IOException;
import java.util.Locale;


import unbbayes.util.Debug;

/**
 * This is just a stub in order to test this plugin
 * on UnBBayes
 * @author Shou Matsumoto
 *
 */
public class UnBBayesMainDelegator {

	/**
	 * Default empty constructor
	 */
	protected UnBBayesMainDelegator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * It just delegates to UnBBayes' main
	 * @param args
	 * @throws AclFormatException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// change default locale
		Locale.setDefault(new Locale("pt"));
		// enable debug mode 
		Debug.setDebug(true);
		// delegate to UnBBayes
		
	
		
		unbbayes.Main.main(args);
	}

}
