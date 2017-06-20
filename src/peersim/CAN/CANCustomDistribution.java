import peersim.core.CommonState;
import peersim.config.Configuration;
import peersim.core.Network;
import java.util.Random;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;

public class CANCustomDistribution implements Control {

/*----------------------------------------------------------------------
 * Attributs
 * ---------------------------------------------------------------------
 */
    private static final String PAR_PROT = "protocol";

    private int protocolID;
    private static Random rnd;

/*----------------------------------------------------------------------
 * Constructeur
 * ---------------------------------------------------------------------
 */ 

	public CANCustomDistribution(String prefix) {
		protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
		rnd = new Random();
    }

/*----------------------------------------------------------------------
 * Méthodes
 * ---------------------------------------------------------------------
 */
 
	public boolean execute() {
		long tmp;
		for (int i = 0; i < Network.size(); ++i) {
			tmp = (long)(rnd.nextDouble()*10000);
			if(tmp<0) tmp=-tmp;
			((CANProtocol)(Network.get(i).getProtocol(protocolID))).setNodeID(tmp);
		}
		return false;
	}
    
} 
