import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface JobRef specifies the Java RMI remote interface for a distributed
 * Job object in the Job Scheduling system. This Interface is implemented by all Job giving Client.
 * 
 * @author Roshan Balaji
 */

public interface JobRef extends java.io.Serializable{
	/**
	 * This function is triggered by the ComputeSever to run the job.
	 * The function perform prime operation/ Gcd opearation or a new operation 
	 *
	 * @param none
	 * 
	 *@return  JobRef a jobref object on whose input the run function is manipulated on.
	 *
	 *@exception  InterruptedException
	 *     Thrown when a thread is interrupted when it is alive.
	 */
	public JobRef run() throws InterruptedException, RemoteException;
	/**
	 * This function is triggered by the JobScheduler to get
	 * the name of the job to forward it to the Logger
	 *
	 * @param none
	 * 
	 *@return  String JobName
	 *
	 */
	public String GetName() throws RemoteException;

}
