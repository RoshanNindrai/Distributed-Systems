import java.math.BigInteger;
import java.rmi.RemoteException;
import edu.rit.ds.registry.NotBoundException;
import edu.rit.ds.registry.RegistryProxy;
/**
 * Class GcdJob acts as a client in the GC jobScheduling system. Each client creates 
 * a job object, each object has the user input value with other fields of job filled. Once
 * the job object is created it is sent to the JobScheduler ScheduleJob function which returns
 * the same object with the updated result.
 * <P>
 * Class GcdJob also has the main program for creating and sending Gcd Job Object.
 * <P>
 * Usage: java GcdJob <I>host</I> <I>port</I> <I>Jsname</I> "<I>JobName</I>""<I>The value 1</I>" "<I>The value 2</I>"
 * <BR><I>host</I> = Registry Server's host
 * <BR><I>port</I> = Registry Server's port
 * <BR><I>Jsname</I> = The name of the JobScheduler
 * <BR><I>JobName</I> = The name of the job assigned
 * <BR><I>Value</I> = The input value given by the user
 * 
 * @author Roshan Balaji
 */
public class GcdJob implements JobRef {
	 /**
	 * @param args
	 */
	public BigInteger input1;
	public BigInteger input2;
	public BigInteger output;
	public String JobName;
	
public GcdJob(BigInteger _input1,BigInteger _input2, String _jobname){
			this.input1=_input1;
			this.input2=_input2;
			this.output=BigInteger.valueOf(0);
			this.JobName=_jobname;
		}
		public static void main(String[] args) throws NotBoundException, RemoteException, InterruptedException {
			if (args.length != 6)
		    {
		    throw new IllegalArgumentException
		        ("Usage:GcdJob: <host> <port> <jsname> <jobname> <value 1> <value 2>");
		    }
			// The host address
		String host = args[0];
		// the job scheduler name
		String jsname = args[2];
		// job name
		String jobname = args[3];
		// Input value 1
		BigInteger inputvalue_1;
		// Input value 2
		BigInteger inputvalue_2;
		// The output repository after GCD job is performed
		BigInteger output=BigInteger.valueOf(0);
		int port;
		// the port is being parsed from string to Integer
		try
		    {
		    port = Integer.parseInt (args[1]);
		    }
		catch (NumberFormatException exc)
		    {
		    throw new IllegalArgumentException
		        ("GcdJob: Invalid port: \"" +
		         args[1] + "\"");
		    }
		// The user input value 1 is parsed from string to BigInteger
		try
	    {
	    inputvalue_1 = new BigInteger(args[4]);
	    }
	catch (NumberFormatException exc)
	    {
	    throw new NumberFormatException
	        ("GcdJob: Invalid user input: \"" +
	         args[4] + "\"");
	    }
		// The user input value 2 is parsed from string to BigInteger
		try
	    {
			inputvalue_2 = new BigInteger(args[5]);
	    }
	catch (NumberFormatException exc)
	    {
	    throw new NumberFormatException
	        ("GcdJob: Invalid user input: \"" +
	         args[5] + "\"");
	    }
		RegistryProxy myProxy = null;
		try {
			myProxy = new RegistryProxy(host,port);
		} catch (RemoteException e){
			
			throw new RemoteException
			("GcdJob:myProxy object problem");
			}
		 try {
			 // The Gcd Job search for the job scheduler object.
			JobSchedulerInterface jobScheduler= (JobSchedulerInterface) myProxy.lookup(jsname);
			// a new Job object is created with user input values (value 1 and value 2)
			GcdJob job=new GcdJob(inputvalue_1,inputvalue_2, jobname);
			// the output from the GCD run method is printed
			GcdJob jobrefobject=(GcdJob)jobScheduler.scheduleJob(job);
			System.out.println(jobrefobject.getOutput());
		} catch (NotBoundException e) {
			
			throw new NotBoundException
			("GcdJob: The object is not present in the registry server ");
		
	} catch (RemoteException e){
		
		throw new RemoteException
		("GcdJob:export Remote object reference problem");
		
} catch (InterruptedException e) {
	throw new InterruptedException 
	("GcdJob: export object problem");
	}
	}
		/**
		 * This function is triggered by the Compute Sever to run the job.
		 * The function perform Gcd operation which is called by checking
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
				this.output=this.input1.gcd(input2);
				return this;
				}
				catch(InterruptedException exc){
					throw new InterruptedException 
					("GcdJob: Interrupted during sleep");
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
			return this.JobName;
		}
		/**
		 * This function is triggered by the GCDClient to get the processed output.
		 * The function returns the output of the job .
		 *
		 * @param none
		 * 
		 *@return Output BigInteger It contains the processed output
		 *
		 */
		public BigInteger getOutput(){
			return this.output;
		}

	}
