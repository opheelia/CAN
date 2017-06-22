import peersim.core.CommonState;
import peersim.config.Configuration;
import peersim.core.Network;
import java.util.Random;
import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;
import peersim.transport.*;
import java.io.*;

public class CANZoneDistribution implements Control {

/*----------------------------------------------------------------------
 * Attributes
 * ---------------------------------------------------------------------
 */
    private static final String PAR_PROT = "protocol";

    private int protocolID;

/*----------------------------------------------------------------------
 * Builder
 * ---------------------------------------------------------------------
 */ 

	public CANZoneDistribution(String prefix) {
		protocolID = Configuration.getPid(prefix + "." + PAR_PROT);
    }
    
/*----------------------------------------------------------------------
 * Methods
 * ---------------------------------------------------------------------
 */
 
	public boolean execute() {
		CANInterval[] fullZone = CANProtocol.getFullZone();
		((CANProtocol)((Network.get(0)).getProtocol(protocolID))).setNodeZone(fullZone[0],fullZone[1]);
		System.out.println("CANZoneDistribution : first node has full zone");
		return false;
	}
    
}
