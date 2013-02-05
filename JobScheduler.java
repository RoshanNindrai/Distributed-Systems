import java.math.BigInteger;
import java.rmi.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.rit.ds.Lease;
import edu.rit.ds.RemoteEventGenerator;
import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.*;
import edu.rit.ds.registry.AlreadyBoundException;

import java.rmi.server.*;
/**
 * Class JobScheduler provides a Java RMI distributed Scheduler object in the Job Scheduling system.
 * <P>
 * Usage: java Start JobScheduler <I>host</I> <I>port</I> <I>name</I>
 * 
 * <BR><I>host</I> = Registry Server's host
 * <BR><I>port</I> = Registry Server's port
 * <BR><I>name</I> = Name of the JobScheduler
 * 
 * @authors Alan Kaminsky, Roshan Balaji
 */
public class JobScheduler extends Thread implements JobSchedulerInterface{
	static RegistryProxy myProxy;
boolean status;
BigInteger output;
static boolean flag=false;
JobEvent event;
public static int job_id=0;
 JobRef jobobject;
public static Integer done_job_id=0;
static Queue<JobRef> jobqueue=new ConcurrentLinkedQueue<JobRef>();
List<String> registry_object= new ArrayList<String>();
static Queue<JobRef> interruptedjobqueue=new ConcurrentLinkedQueue<JobRef>();
static RemoteEventGenerator<JobEvent> eventGenerator = new RemoteEventGenerator<JobEvent>();	
static  Queue<ComputeServerInterface>serverlist=new LinkedList<ComputeServerInterface>();
/**
 * Construct a new JobScheduler object.
 * <P>
 * The command line arguments are:
 * <BR><TT>args[0]</TT> = Registry Server's host
 * <BR><TT>args[1]</TT> = Registry Server's port
 * <BR><TT>args[2]</TT> = name of the JobScheduler
 * 
 * @param  args  Command line arguments.
 *
 * @exception  IllegalArgumentException
 *     (unchecked exception) issue due to command line arguments being given
 *     line arguments.
 * @exception  RemoteException
 *     Thrown if a remote error occurred.
 * @throws AlreadyBoundException
 * 	   Thrown when the object is already found in the registry server 
 * @exception NoSuchObjectException
 * 	   Thrown if the object is not to be found in Registry Server
 */
public JobScheduler(String args[]) throws RemoteException, AlreadyBoundException
{
super();
// Verify command line arguments.
if (args.length != 3)
    {
    throw new IllegalArgumentException
        ("JobScheduler: <host> <port> <JSName>");
    }
String host = args[0];
String name = args[2];
int port;
try
    {
    port = Integer.parseInt (args[1]);
    }
catch (NumberFormatException exc)
    {
    throw new IllegalArgumentException
        ("JobScheduler: Invalid port: \"" +
         args[1] + "\"");
    }
// Get a proxy for the Registry Server.
try{
myProxy = new RegistryProxy (host, port);
}
catch(RemoteException e)
{
	 throw new RemoteException
     ("JobScheduler: Invalid Registry Server Not found");
}
// Export.
	
	UnicastRemoteObject.exportObject (this, 0);

// Rebind into the Registry Server.
	
try
    {
	
    myProxy.bind (name, this);
	
    }
catch (RemoteException exc)
    {
    try
        {
        UnicastRemoteObject.unexportObject (this, true);
        throw new RemoteException
        ("JobScheduler: Invalid Registry Server Not found");
        }
    catch (NoSuchObjectException exc2)
        {
        }
    throw exc;
    }
catch (AlreadyBoundException exc)
{
try
	{
	UnicastRemoteObject.unexportObject (this, true);
	throw new AlreadyBoundException
    ("JobScheduler: The object is already Bounded");
	}
catch (NoSuchObjectException exc2)
	{
	}
throw new IllegalArgumentException
	("JobScheduler(): <JSname> = \""+name+"\" already exists");
}
}
/**
 * Construct a new JobScheduler Thread object.
 *
 * @param  none.
 *
 * @exception  none.
 */
public JobScheduler(){
	JobRef jobobject;
}
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
public JobRef scheduleJob
		(JobRef _Job ) 
	throws RemoteException, InterruptedException {
	// reporting that the job is scheduled.
	eventGenerator.reportEvent (new JobEvent (_Job));
	// slowing down the tread.
	slowDown(500L);
	// creating a job scheduler thread to handle a job,
	JobScheduler jobScheduler_thread=new JobScheduler();
	// job scheduler job is assigned
	jobScheduler_thread.jobobject=_Job;
	// checking if there is any free servers for the job 
	//to be started.
	while(isserveravailable()||flag){slowDown(500L);}
	// the joscheduler thread is started.
	jobScheduler_thread.start();
	// thread wait till it reutnr from run
	jobScheduler_thread.join();
	// the return function is called by which the client gets it job back 
	// after computation.
	return jobScheduler_thread.sendResult();
}
/**
 * This function overrides java.lang.thread.run function,
 * and it is started when the respective thread start function is called.
 *
 *@param none.
 * 
 *@return  none.
 *
 *@exception  InterruptedException
 *     Thrown when a thread is interrupted when it is alive.
 */
public void run(){
	try {
		// getjob is called by which the job is made ready for the server to 
		// run it.
		this.jobobject=getJob(this.jobobject);
		} 
	catch (Exception e) 
		{
		} 
}
/**
 * Adds the event listener to the Scheduler. The JobScheduler report event 
 * when a job is scheduled, when the job is started in a server, and when 
 * the job is completed by the compute servers.
 *
 * @param  listener  Remote event listener.
 * 
 * @author Alan Kaminsky
 */
public Lease addListener
	(RemoteEventListener<JobEvent> listener)
	throws RemoteException
		{
	return eventGenerator.addListener (listener);
		}
/**
 * This function constitutes the heart of the program, The job
 * is given its server signature and the job is forwarded 
 * to the compute server to run its respective jobs. Event notification are sent
 * when the job starts in a server and when it get finished in the server.
 * 
 *@param _Job JobRef the job forwarded from the Scheduler thread.
 * 
 *@return  the JobRef after compute server computes it.
 *
 *@exception  InterruptedException
 *     Thrown when a thread is interrupted when it is alive.
 * @exception  RemoteException
 *     Thrown when a remote error occurs.
 *@exception  Unmarshallexception
 *     Thrown when the compute server goes down.    
 */
public JobRef getJob
	(final JobRef _job) 
	throws RemoteException, InterruptedException{
	try	{
			// Getting a idle server for computation.
			ComputeServerInterface server=serverlist.remove();
			// Job start event is reported.
			eventGenerator.reportEvent (new JobEvent (0,server.getServerName(),_job));
			// The compute job is performed at the compute server end.
			JobRef get=server.doJob(_job);
			// The job finished event is reported.
			eventGenerator.reportEvent (new JobEvent (1,server.getServerName(),get));
			// The result is returned.
			return get;
		}
	catch(Exception e){
		// the flag is used to make the other jobs wait till the interrupted job is done.
			flag=true;
			//job added to the interrupted queue.
			interruptedjobqueue.add(_job);
			// waiting for idle servers.
			while(isserveravailable()){slowDown(500L);}
			// the get job is called to compute for the interrupted job.
			JobRef Get=getJob(interruptedjobqueue.remove());
			// the flag is released.
			flag=false;
			return Get;
		}
}
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
public void registerServer
	(final ComputeServerInterface _server) 
		throws RemoteException 
		{
	try{
			serverlist.add(_server);
	   }
	catch(Exception e)
		{
		}
	}
/**
 * This function is used by JobScheduler thread to find is there is any idle server 
 * available.
 * 
 *@param none.
 * 
 *@return  true when there is no server, false when there is a server.
 *
 */
public boolean isserveravailable(){
	if(serverlist.isEmpty()){
			return true;
		}
	else{
			return false;
		}
}
/**
 * This function is called when the tread completes the task.
 * 
 *@param none.
 * 
 *@return   Job JobRef returns the job to the client after execution.
 *
 */
public JobRef sendResult() 
	throws RemoteException 
		{
	return this.jobobject;
		}
/**
 * This function is called to slow down a tread.
 * 
 *@param none.
 * 
 *@return  none.
 *
 *@exception  InterruptedException
 *     Thrown when a thread is interrupted when it is alive.
 *     
 *     @author Alan Kaminsky
 *
 */
private void slowDown(final long _sleep)
{
try
	{
	Thread.sleep (_sleep);
	}
catch (InterruptedException exc)
	{
	}
}


}
