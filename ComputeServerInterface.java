import java.rmi.Remote;
import java.rmi.RemoteException;
/**
 * Interface ComputeServerInterface specifies the Java RMI remote interface for a distributed
 * ComputeServer object in the Job Scheduling system.
 * 
 * @author Roshan Balaji
 */
public interface ComputeServerInterface extends Remote {
	/**
	 * performs the job that is sent from the JobScheduler this server, The server examines 
	 * the kind of the job and performs the respective job.
	 *
	 * @param 	jobgot Job Job sent by the JobSchedulers.
	 * 
	 * @return  Jobgot Job returned to after the job run function is run.
	 *
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 * @exception  InterruptedException
	 *     Thrown when a thread is interrupted when it is alive.
	 */
	public JobRef doJob(JobRef jobgot) 
			throws InterruptedException, RemoteException;
	/**
	 * The function provides the signature for the job that it handles.
	 *
	 * @param 	none.
	 * 
	 * @return  String that give the name of the server.
	 *
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 * @exception  InterruptedException
	 *     Thrown when a thread is interrupted when it is alive.
	 */
	 public String getServerName() 
			 throws InterruptedException, RemoteException;
}
