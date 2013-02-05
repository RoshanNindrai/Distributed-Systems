import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryProxy;
import java.io.*;
/**
 * Class ComputeServer provides a object to the Job Scheduler to be used in the computing jobs.
 * <P>
 * Usage: java Start ComputeServer <I>host</I> <I>port</I> <I>jsname</I><I>csname</I>
 * 
 * <BR><I>host</I> = Registry Server's host.
 * <BR><I>port</I> = Registry Server's port.
 * <BR><I>jsname</I> = Name of the JobScheduler.
 * <BR><I>csname</I> = Name of the ComputeServer.
 * 
 * @author Roshan Balaji
 */
public class ComputeServer extends Thread  implements Serializable,ComputeServerInterface {
	JobSchedulerInterface scheduler;
	public String csname;
	boolean Status;
	JobRef jobgot;
	Boolean pstate=false;
	/**
	 * Construct a new ComputeServer object.
	 * <P>
	 * The command line arguments are:
	 * <BR><TT>args[0]</TT> = Registry Server's host
	 * <BR><TT>args[1]</TT> = Registry Server's port
	 * <BR><TT>args[2]</TT> = name of the JobScheduler
	 * <BR><TT>args[3]</TT> = name of the ComputeServer
	 * 
	 * @param  args  Command line arguments.
	 * @throws NotBoundException 
	 *		Thrown during lookup when the remote object is not found
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) issue due to command line arguments being given
	 *     line arguments.
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 * @exception NoSuchObjectException
	 * 	   Thrown if the object is not to be found in Registry Server
	 */
	public ComputeServer(String args[]) throws NotBoundException, RemoteException {
		if (args.length != 4)
	    {
	    throw new IllegalArgumentException
	        ("Usage:ComputeServer: <host> <port> <jsname> <csame> <x>");
	    }
	String host = args[0];
	String jsname = args[2];
	csname = args[3];
	int port;
	// parsing the port from the string.
	try
	    {
	    port = Integer.parseInt (args[1]);
	    }
	catch (NumberFormatException exc)
	    {
	    throw new IllegalArgumentException
	        ("ComputeServer: Invalid port: \"" +
	         args[1] + "\"");
	    }
	RegistryProxy myProxy = null;
	try {
		// getting the registry proxy
		myProxy = new RegistryProxy(host,port);
		//the object is exported for listening.
		UnicastRemoteObject.exportObject(this,0);
	} catch (RemoteException e) {
		try {
			throw new RemoteException
			("ComputeServer: export object problem");
		} 
		catch(Exception ex){}
	}
	 try {
		 // looking up for the remote registry job scheduler object
		  scheduler=(JobSchedulerInterface)myProxy.lookup(jsname);
		  // the object is registered as an idle server ready to take jobs
		  scheduler.registerServer(this);
		
		 }
	 catch (NotBoundException e) {
			
				throw new NotBoundException
				("ComputeServer: The object is not present in the registry server ");
			
		} catch (RemoteException e){
			
			throw new RemoteException
			("ComputeServer: export object problem");
			
	}
	}
	/**
	 * performs the job that is sent from the JobScheduler this server, The server examines 
	 * the kind of the job and performs the respective job.
	 *
	 * @param 	jobgot JobRef Job sent by the JobSchedulers.
	 * 
	 * @return  Jobgot JobRef returned to after the job run function is run.
	 *
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 * @exception  InterruptedException
	 *     Thrown when a thread is interrupted when it is alive.
	 */
	 public JobRef doJob(JobRef jobgot) throws InterruptedException, RemoteException{
		 try{
	     	  
				  jobgot.run();
				  scheduler.registerServer(this);
				  return(jobgot);
			  }
		 catch(RemoteException e){
			 throw new RemoteException
		        ("ComputeServer: Invalid Registry Server Not found");

		 }
		catch(InterruptedException e){
			throw new InterruptedException
	        ("ComputeServer: Invalid Registry Server Not found");
		}
		
	 
	 }
	@Override
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
	public String getServerName() throws InterruptedException, RemoteException {
		
		return csname;
	}
		  
	 

}
