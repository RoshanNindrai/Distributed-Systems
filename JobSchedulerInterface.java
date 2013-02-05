

import java.rmi.Remote;
import java.rmi.RemoteException;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventListener;
/**
 * Interface JobSchedulerInterface specifies the Java RMI remote interface for a distributed
 * Job Scheduler object in the Job Scheduling system.
 * 
 * @author Roshan Balaji
 */
public interface JobSchedulerInterface extends Remote{
	/**
	 * Creates a Thread for every job that is being sent to the Scheduler
	 * each Thread is assigned with a job object, which is forwarded
	 * to a server for it to run and the function returns the client with its
	 * respective job object.
	 *
	 * @param _Job Job sent by the clients.
	 * 
	 *@return  Job Job returned to the respective client after being computed.
	 *
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 * @exception  InterruptedException
	 *     Thrown when a thread is interrupted when it is alive.
	 */
	public  JobRef scheduleJob(JobRef _Job) 
			throws RemoteException, InterruptedException ;
	/**
	 * Adds the event listener to the Scheduler. The JobScheduler report event 
	 * when a job is scheduled, when the job is started in a server, and when 
	 * the job is completed by the compute servers.
	 *
	 * @param  listener  Remote event listener.
	 */
	public Lease addListener(RemoteEventListener<JobEvent> listener)
			throws RemoteException;
	/**
	 * This function is called by the compute server
	 * to register itself as idle .
	 *@param _server ComputeServerInterface this denotes a idle server.
	 * 
	 *@return  none.
	 *
	 * @exception  RemoteException
	 *     Thrown when a remote error occurs.
	 *
	 */
    public void registerServer(ComputeServerInterface _server) 
    		throws RemoteException;
  }
