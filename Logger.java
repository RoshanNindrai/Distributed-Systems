import edu.rit.ds.RemoteEventListener;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryEvent;
import edu.rit.ds.registry.RegistryEventFilter;
import edu.rit.ds.registry.RegistryEventListener;
import edu.rit.ds.registry.RegistryProxy;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
/**
 * Class Log provides the log program in the JobScheduler system.
 * @authors Alan Kaminsky, Roshan Balaji
 */
public class Logger {
	private static String host;
	private static int port;
	private static RegistryProxy registry;
	private static RegistryEventListener registryListener;
	private static RegistryEventFilter registryFilter;
	private static RemoteEventListener<JobEvent> JobListener;

	public static void main(String[] args) throws NotBoundException, RemoteException {
		// Parse command line arguments.
				if (args.length != 3)  throw new IllegalArgumentException
		        ("Logger:java Logger <host> <port> <jsnsame>"); ;
				String host = args[0];
				final String remotehost = args[2];
				
				int port = parseInt (args[1], "port");
				// Get proxy for the Registry Server.
				try {
					// get the registry Proxy
					registry = new RegistryProxy (host, port);
				} catch (RemoteException e1) {
					throw new RemoteException
					("Logger: Registry Proxy Issue");
				}
				registryListener = new RegistryEventListener()
					{
				public void report (long seqnum, RegistryEvent event) throws RemoteException
				{
				try {
					
						listenToNode (event.objectName());
					} catch (NotBoundException e) {
						
					}catch (RemoteException e) {
					throw new RemoteException
					("Logger: Lookup issue of Remote job scheduler object");
				} 
				}
			};
		UnicastRemoteObject.exportObject (registryListener, 0);


			// Export a remote event listener object for receiving notifications
			// from JobScheduler objects.
				JobListener = new RemoteEventListener<JobEvent>()
							{
				public void report (long seqnum, JobEvent event) throws RemoteException
					{
					//listenToNode (remotehost);
					try{
					if(event.server==null){
						// event is triggered when a job is scheduled
					System.out.println ("Job "+event.job.GetName()+" scheduled");
					}
					else{
						if(event.state==0){
							// this event depicts the start of the job
						System.out.println ("Job "+event.job.GetName()+" started on "+event.server);
						}
						else{
							// this event depicts the end of the job
							System.out.println ("Job "+event.job.GetName()+" finished on "+event.server);
						}
					}
					}
					catch(RemoteException exe){
						throw new RemoteException
				        ("JobScheduler: Invalid Registry Server Not found");
					}
					}
				};
				try {
					UnicastRemoteObject.exportObject (JobListener, 0);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// Tell the Registry Server to notify us when a new JobScheduler object is
				// bound.
				registryFilter = new RegistryEventFilter()
					.reportType ("JobScheduler")
					.reportBound();
				// This function regiters the event listner to the updates of the JobScheduler
				registry.addEventListener (registryListener, registryFilter);
				try {
					listenToNode(remotehost);
				} catch (NotBoundException e) {
					
					throw new NotBoundException
					("Logger: The object is not present in the registry server ");
				
			} catch (RemoteException e){
				
				throw new RemoteException
				("Logger: Lookup issue of Remote job scheduler object");
			
		}
}
	/**
	 * Tell the given JobScheduler object to notify us of Job Migration and Job scheduling.
	 *
	 * @param  objectName  Job Scheduler object's name.
	 * @throws NotBoundException 
	 *
	 * @exception  RemoteException
	 *     Thrown if a remote error occurred.
	 */
	private static void listenToNode
		(String objectName) throws NotBoundException, RemoteException
		{
		try
			{
			JobSchedulerInterface node = (JobSchedulerInterface) registry.lookup (objectName);
			node.addListener (JobListener);
			}
		catch (NotBoundException e) {
			
			throw new NotBoundException
			("Logger: The object is not present in the registry server ");
		
	} catch (RemoteException e){
		
		throw new RemoteException
		("Logger: Lookup issue of Remote job scheduler object");
	
}
		}
	private static void usage()
	{
	System.err.println ("Usage: java Log <host> <port> <JsName>");
	System.err.println ("<host> = Registry Server's host");
	System.err.println ("<port> = Registry Server's port");
	System.err.println ("<JsName> = JobScheduler name");
	System.exit (1);
	}

/**
 * Parse an integer command line argument.
 *
 * @param  arg  Command line argument.
 * @param  name  Argument name.
 *
 * @return  Integer value of arg.
 *
 * @exception  NumberForamantException
 *     (unchecked exception) Thrown if it cannot be parsed as an
 *     integer.
 */
private static int parseInt
	(String arg,
	 String name)
	{
	try
		{
		return Integer.parseInt (arg);
		}
	catch (NumberFormatException exc)
		{
		System.err.printf ("Logger: Invalid <%s>: \"%s\"", name, arg);
		usage();
		return 0;
		}
	}

}
