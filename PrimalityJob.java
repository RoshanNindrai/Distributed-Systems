import java.math.*;
import java.rmi.RemoteException;
import edu.rit.ds.registry.*;
/**
 * Class PrimalityJob acts as a client in the GC jobScheduling system. Each client creates 
 * a job object, each object has the user input value with other fields of job filled. Once
 * the job object is created it is sent to the JobScheduler ScheduleJob function which return backs
 * with the same object with the updated result.
 * <P>
 * Class PrimalityJob also has the main program for sending primalityJob Object.
 * <P>
 * Usage: java PrimalityJob <I>host</I> <I>port</I> <I>Jsname</I> "<I>JobName</I>""<I>The value</I>"
 * <BR><I>host</I> = Registry Server's host
 * <BR><I>port</I> = Registry Server's port
 * <BR><I>Jsname</I> = The name of the JobScheduler
 * <BR><I>JobName</I> = The name of the job assigned
 * <BR><I>Value</I> = The input value given by the user
 * 
 * @author Roshan Balaji
 */
public class PrimalityJob implements JobRef{

	BigInteger input;
	Boolean output;
	static JobRef job;
	public  String Jobname;
	public PrimalityJob(BigInteger _input,String _jobname){
		this.input=_input;
		this.Jobname=_jobname;
		this.output=output;
	}
public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException {
	if (args.length != 5)
	    {
	    throw new IllegalArgumentException
	        ("Usage:java PrimalityJob <host> <port> <jsname> <jobname> <x>");
	    }
	// The host address
	String host = args[0];
	// the job scheduler name
	String jsname = args[2];
	// job name
	String jobname = args[3];
	// Input value 
	BigInteger userinput;
	int port;
	// the port is being parsed from string to Integer
	try
	    {
	    port = Integer.parseInt (args[1]);
	    }
	catch (NumberFormatException exc)
	    {
	    throw new IllegalArgumentException
	        ("PrimalityJob: Invalid port: \"" +
	         args[1] + "\"");
	    }
	// The user input is parsed from string to BigInteger
	try
    {
		 userinput = new BigInteger(args[4]);
    }
catch (NumberFormatException exc)
    {
    throw new IllegalArgumentException
        ("PrimalityJob: Invalid user input: \"" +
         args[4] + "\"");
    }
	RegistryProxy myProxy = null;
	try {
		myProxy = new RegistryProxy(host,port);
	} catch (RemoteException e) {
		throw new RemoteException
		("PrimalityJob: export object problem");
	}
	 try {
		 // The PrimalityJob search for the job scheduler object.
		 JobSchedulerInterface jobScheduler= (JobSchedulerInterface) myProxy.lookup(jsname);
		 // a new Job object is created with user input value.
		 PrimalityJob job=new PrimalityJob(userinput,jobname);
		 // The Job is scheduled using JobScheduler Object.
		 job=(PrimalityJob) jobScheduler.scheduleJob(job);
		 // The out from the server is analysed before being presented to the user.
		if(job.output){
			System.out.println("prime");
		}
		else{
			System.out.println("composite");
		}
}  catch (NotBoundException e) {
	
	throw new NotBoundException
	("PrimalityJob: The object is not present in the registry server ");

} catch (RemoteException e){

throw new RemoteException
("PrimalityJob: export Remote object reference problem");

} catch (InterruptedException e) {
	throw new InterruptedException
	("PrimalityJob: The object is not present in the registry server ");
} 
}
/**
 * This function is triggered by the Compute Sever to run the job.
 * The function perform prime operation which is called by checking
 * the flag first by the compute Server.
 *
 * @param _object Job sent by the computeServer.
 * 
 *@return  none
 *
 *@exception  InterruptedException
 *     Thrown when a thread is interrupted when it is alive.
 */
public JobRef run() 
throws InterruptedException{
	try{
	Thread.sleep(10000);
	// checking if input1 is a prime.
	this.output=this.input.isProbablePrime(64);
	return this;
	}
	catch(InterruptedException exc){
		throw new InterruptedException
		("PrimalityJob: Interrupted during sleep");
	}
	}
/**
 * This function is triggered by the JobScheduler to get
 * the name of the job to forward it to the Logger
 *
 * @param none
 * 
 *@return  String JobName
 *
 */
public String GetName(){
	return this.Jobname;
}
}
