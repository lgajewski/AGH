#ifndef SR_DEMO_ICE
#define SR_DEMO_ICE

//#include <Ice/BuiltinSequences.ice>

module demo
{

	struct Name
	{
		string firstName;
		string lastName;
	};
	
	interface User
	{
	    long getTimestamp();
		long getId();
		Name getName();
		void changeName(Name name1); 	
	};


};

#endif
